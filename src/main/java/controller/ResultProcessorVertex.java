package controller;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
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
        router.get("/v1/:pollId/").handler(this::getResults);
        router.get("/v1/:pollId/dump").handler(this::getResultsDump);

        // POST
        router.route("/v1/:pollId/").method(HttpMethod.POST).handler(BodyHandler.create());
        router.post("/v1/:pollId/").handler(this::addResults);

        server.requestHandler(router::accept).listen(8080);
    }


    /**
     * This method returns the poll results without the keys.
     *
     * @param routingContext    contains info about the current context
     */
    private void getResults(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json");

        // Get results from local database, if master has pushed it here
        MongoClient mongoClient = MongoClient.createShared(vertx, config());
        JsonObject query = new JsonObject();

        String pollId = routingContext.request().getParam("pollId");

        mongoClient.find(pollId, query, res -> {
            if (res.succeeded()) {
                try {
                    // Get 0 as each poll is its own collection, which means there's
                    // only one results object per collection.
                    JsonObject json = res.result().get(0);
                    json.remove("_id");
                    removeKeys(json);
                    response.end(Json.encodePrettily(json));
                }
                catch (Exception ex) {
                    // Return error json in case data is not available
                    JsonObject error = new JsonObject();
                    error.put("error", "Could not retrieve data. Please try again.");
                    response.end(Json.encodePrettily(error));
                }
            }
        });
    }


    /**
     * This method returns the poll results with the keys
     *
     * @param routingContext    contains info about the current context
     */
    private void getResultsDump(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json");

        // Get results from local database, if master has pushed it here
        MongoClient mongoClient = MongoClient.createShared(vertx, config());
        JsonObject query = new JsonObject();

        String pollId = routingContext.request().getParam("pollId");

        mongoClient.find(pollId, query, res -> {
            if (res.succeeded()) {
                try {
                    // Get 0 as each poll is its own collection, which means there's
                    // only one results object per collection.
                    response.end(Json.encodePrettily(res.result().get(0)));
                }
                catch (Exception ex) {
                    // Return error json in case data is not available
                    JsonObject error = new JsonObject();
                    error.put("error", "Could not retrieve data. Please try again.");
                    response.end(Json.encodePrettily(error));
                }
            }
        });
    }


    /**
     * This method allows master to push results data to the result processors local
     * database.
     *
     * @param routingContext    contains info about the current context
     */
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
                // Return error in case data couldn't be written to db
                JsonObject error = new JsonObject();
                error.put("error", "Could not add data. Please try again.");
                response.end(Json.encodePrettily(error));
            }
        });
    }


    /**
     * This method removes the keys from the json in case they are not needed.
     *
     * @param json  the json from which to remove the keys
     */
    private void removeKeys(JsonObject json) {
        JsonArray questions = json.getJsonArray("questions");

        // For each question
        for (int i = 0; i < questions.size(); i++) {
            JsonArray answers = questions.getJsonObject(i).getJsonArray("answers");

            // Remove keysVoted array from each answer for a question
            for (int j = 0; j < answers.size(); j++) {
                answers.getJsonObject(j).remove("keysVoted");
            }
        }
    }
}
