package controller;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.JadeTemplateEngine;

/**
 * This class is handling requests that are to do with retrieving results from a
 * vote.
 * <p>
 * Created by robin on 2016-03-10.
 */
public class ResultProcessorVertex extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);


        setTemplating(router);
        setResources(router);

        // Add routes and handlers
        // GET
        router.get("/api/view/:id").handler(this::getResults);
        router.get("/api/download/:id").handler(this::getResultsDump);

        // POST
        router.route("/api/results/").method(HttpMethod.POST).handler(BodyHandler.create());
        router.post("/api/results/").handler(this::addResults);

        server.requestHandler(router::accept).listen(7670);
    }

    private void setTemplating(Router router) {
        JadeTemplateEngine jade = JadeTemplateEngine.create();

        router.route("/").handler(context -> {
            jade.render(context, "templates/index", result -> {
                if (result.succeeded())
                    context.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(result.result());
                else
                    context.fail(result.cause());
            });
        });
    }

    private void setResources(Router router) {
        router.route("/resources/*").handler(StaticHandler.create()
                .setCachingEnabled(true));
    }


    /**
     * This method returns the poll results without the keys.
     *
     * @param routingContext contains info about the current context
     */
    private void getResults(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();

        // Get results from local database, if master has pushed it here
        MongoClient mongoClient = MongoClient.createShared(vertx, config());
        JsonObject query = new JsonObject();
        JsonObject filter = new JsonObject().put("options.values.keys", 0);

        String pollId = routingContext.request().getParam("id");

        mongoClient.findOne(pollId, query, filter, res -> {
            if (res.succeeded()) {
                try {
                    // Get 0 as each poll is its own collection, which means there's
                    // only one results object per collection.
                    JsonObject json = res.result();
                    json.remove("_id");
                    response.setStatusCode(HttpResponseStatus.OK.code()).end(json.encode());
                } catch (Exception ex) {
                    // Return error json in case data is not available
                    JsonObject error = new JsonObject();
                    error.put("error", "Could not retrieve data. Please try again.");
                    response.setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end(Json.encodePrettily(error));
                }
            }
        });
    }


    /**
     * This method returns the poll results with the keys
     *
     * @param routingContext contains info about the current context
     */
    private void getResultsDump(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();

        // Get results from local database, if master has pushed it here
        MongoClient mongoClient = MongoClient.createShared(vertx, config());
        JsonObject query = new JsonObject();

        String pollId = routingContext.request().getParam("id");

        mongoClient.findOne(pollId, query, null, res -> {
            if (res.succeeded()) {
                try {
                    // Get 0 as each poll is its own collection, which means there's
                    // only one results object per collection.
                    response.putHeader("Content-Type", "application/octet-stream");
                    response.putHeader("Content-Disposition", "attachment; filename=" + res.result().getString("topic") + ".json");
                    response.setStatusCode(HttpResponseStatus.OK.code()).end(res.result().encodePrettily());
                } catch (Exception ex) {
                    // Return error json in case data is not available
                    JsonObject error = new JsonObject();
                    error.put("error", "Could not retrieve data. Please try again.");
                    response.setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end(Json.encodePrettily(error));
                }
            }
        });
    }


    /**
     * This method allows master to push results data to the result processors local
     * database.
     *
     * @param routingContext contains info about the current context
     */
    private void addResults(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json");

        JsonObject addQuery = routingContext.getBodyAsJson().getJsonObject("voting");
        MongoClient mongoClient = MongoClient.createShared(vertx, config());


        mongoClient.insert(addQuery.getString("id"), addQuery, res -> {
            if (res.succeeded()) {
                response.setStatusCode(HttpResponseStatus.OK.code()).end(Json.encodePrettily(res.result()));
            } else {
                // Return error in case data couldn't be written to db
                JsonObject error = new JsonObject();
                error.put("error", "Could not add data. Please try again.");
                response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(Json.encodePrettily(error));
            }
        });
    }
}
