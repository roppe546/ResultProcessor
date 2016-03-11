package controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import model.Entry;
import model.PollResults;

import java.time.Instant;
import java.util.ArrayList;

/**
 * This class fetches fresh data from back end every five seconds.
 *
 * Created by robin on 2016-03-10.
 */
public class DataFetcherVertex extends AbstractVerticle {
    private static final int REFETCH_TIME = 5000;

    private PollResults pollResults = new PollResults();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        MongoClient mongoClient = MongoClient.createShared(vertx, config());
        JsonObject query = new JsonObject();

        vertx.setPeriodic (REFETCH_TIME, fetch -> {
            mongoClient.find("votes", query, res -> {
                if (res.succeeded()) {
                    getResultsData(res);
                } else {
                    res.cause().printStackTrace();
                }
            });
        });
    }

    private void getResultsData(AsyncResult<java.util.List<JsonObject>> res) {
        ArrayList<Entry> temp = new ArrayList<>();

        // Translate json to java objects
        for (JsonObject json : res.result()) {
            if (json.containsKey("finishTime")) {
                JsonObject finishTimeJson = json.getJsonObject("finishTime");
                Instant finishTime = Instant.parse(finishTimeJson.getString("$date"));
                pollResults.setPollFinishTime(finishTime);
            }
            else if (json.containsKey("isFinished")) {
                pollResults.setFinalResults(json.getBoolean("isFinished"));
            }
            else if (json.containsKey("results")) {
                JsonArray resultsArr = json.getJsonArray("results");

                // For each entry in the results list
                for (int i = 0; i < resultsArr.size(); i++) {
                    JsonObject entryJson = resultsArr.getJsonObject(i);

                    String entryName = entryJson.getString("entryName");
                    int voteCount = entryJson.getInteger("voteCount");

                    JsonArray keysVotedJson = entryJson.getJsonArray("keysVoted");
                    ArrayList<Long> keys = new ArrayList<>();

                    // For each key in the keysVoted list
                    for (int j = 0; j < keysVotedJson.size(); j++) {
                        JsonObject key = keysVotedJson.getJsonObject(j);
                        Long f = key.getLong("key");
                        keys.add(f);
                    }

                    Entry entry = new Entry(entryName, voteCount);
                    entry.setKeysVoted(keys);
                    temp.add(entry);
                }
            }
        }

        pollResults.setPollFetchTime(Instant.now());
        pollResults.setEntries(temp);
    }

    public PollResults getPollResults() {
        return pollResults;
    }
}
