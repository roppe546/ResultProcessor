package controller;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This class is handling requests that are to do with retrieving results from a
 * vote.
 *
 * Created by robin on 2016-03-10.
 */
public class ResultProcessorVertex extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        // Add routes and handlers
        // GET
        router.get("/v1/:pollId/results").handler(this::getResults);

        // POST
        router.route("/v1/:pollId/").method(HttpMethod.POST).handler(BodyHandler.create());
        router.post("/v1/:pollId/").handler(this::addResults);

        server.requestHandler(router::accept).listen(8080);
    }


    private void getResults(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json");

        // Get results from back end
        MongoClient mongoClient = MongoClient.createShared(vertx, config());
        JsonObject query = new JsonObject();

        String poll = routingContext.request().getParam("poll");

        mongoClient.find(poll, query, res -> {
            if (res.succeeded()) {
                try {
                    // Get 0 as each poll is its own collection, which means there's
                    // only one results object per collection.
                    response.end(Json.encodePrettily(res.result().get(0)));
                }
                catch (Exception ex) {
                    JsonObject error = new JsonObject();
                    error.put("error", "Could not retrieve data. Please try again.");
                    response.end(Json.encodePrettily(error));
                }
            }
        });
    }


    private void addResults(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json");

        String pollId = routingContext.request().getParam("pollId");
        JsonObject findQuery = new JsonObject().put("pollId", pollId);
        JsonObject addQuery = routingContext.getBodyAsJson();

        MongoClient mongoClient = MongoClient.createShared(vertx, config());

        // Delete previous results from this particular poll if it already exists,
        // so we don't have several result objects of the same poll.
        mongoClient.remove(pollId, findQuery, res -> {});

        mongoClient.insert(pollId, addQuery, res -> {
            if (res.succeeded()) {
                response.setStatusCode(HttpResponseStatus.OK.code()).end();
            }
            else {
                JsonObject error = new JsonObject();
                error.put("error", "Could not add data. Please try again.");
                response.end(Json.encodePrettily(error));
            }
        });
    }
}
