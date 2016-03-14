package controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import model.Answer;
import model.Question;
import model.Results;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

        // Routes
        Route results_v1 = router.route("/v1/:poll/results").method(HttpMethod.GET);

        results_v1.handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");

            // Get results from back end
            // TODO: DON'T GET RESULTS FROM BACK END, LET BACK END PUSH DATA TO US INSTEAD
            MongoClient mongoClient = MongoClient.createShared(vertx, config());
            JsonObject query = new JsonObject();

            String poll = routingContext.request().getParam("poll");

            mongoClient.find(poll, query, res -> {
                if (res.succeeded()) {
                    try {
                        Results results = getResultsData(res);
                        response.end(Json.encodePrettily(results));
                    }
                    catch (Exception e) {
                        JsonObject error = new JsonObject();
                        error.put("error", "Could not retrieve data. Please try again.");
                        response.end(Json.encodePrettily(error));
                    }

                } else {
                    res.cause().printStackTrace();
                }
            });
        });

        server.requestHandler(router::accept).listen(8080);
    }


    private Results getResultsData(AsyncResult<List<JsonObject>> res) {
        Results results = new Results();

        // Translate json to java objects
        for (JsonObject json : res.result()) {
            if (json.containsKey("owner")) {
                results.setOwner(json.getString("owner"));
            }
            else if (json.containsKey("topic")) {
                results.setTopic(json.getString("topic"));
            }
            else if (json.containsKey("pollFinishTime")) {
                JsonObject finishTimeJson = json.getJsonObject("pollFinishTime");
                Instant finishTime = Instant.parse(finishTimeJson.getString("$date"));
                results.setPollFinishTime(finishTime);
            }
            else if (json.containsKey("questions")) {
                results.setQuestions(getQuestions(json.getJsonArray("questions")));
            }
            else {
                System.out.println("JSON WRONG");
            }
        }

        return results;
    }


    private ArrayList<Question> getQuestions(JsonArray questionsArr) {
        // Holds all the questions for the vote/poll in question
        ArrayList<Question> questions = new ArrayList<>();

        // For each question
        for (int i = 0; i < questionsArr.size(); i++) {
            JsonObject question = questionsArr.getJsonObject(i);

            String questionStr = question.getString("question");
            JsonArray answersArr = question.getJsonArray("answers");

            ArrayList<Answer> answers = new ArrayList<>();

            // For each answer (for question)
            for (int j = 0; j < answersArr.size(); j++) {
                JsonObject answer = answersArr.getJsonObject(j);

                String answerStr = answer.getString("answer");
                int count = answer.getInteger("voteCount");
                JsonArray keysArr = answer.getJsonArray("keysVoted");

                ArrayList<String> keysVoted = new ArrayList<>();

                // For each key (for answer)
                for (int k = 0; k < keysArr.size(); k++) {
                    String str = keysArr.getString(k);
                    keysVoted.add(str);
                }

                answers.add(new Answer(answerStr, count, keysVoted));
            }

            questions.add(new Question(questionStr, answers));
        }

        return questions;
    }
}
