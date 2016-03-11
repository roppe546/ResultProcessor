import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;

/**
 * This class fetches fresh data from back end every five seconds.
 *
 * Created by robin on 2016-03-10.
 */
public class DataFetcherVertex extends AbstractVerticle {
    private static final int WAIT_TIME = 5000;

    private PollResults pollResults = new PollResults();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        MongoClient mongoClient = MongoClient.createShared(vertx, config());
        JsonObject query = new JsonObject();

        vertx.setPeriodic (5000, fetch -> {
            ArrayList<Entry> temp = new ArrayList<>();

            mongoClient.find("votes", query, res -> {
                if (res.succeeded()) {
                    // Translate json to java objects
                    for (JsonObject json : res.result()) {
                        Set<String> fieldNames = json.fieldNames();

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

                            for (int i = 0; i < resultsArr.size(); i++) {
                                JsonObject entryJson = resultsArr.getJsonObject(i);

                                String entryName = entryJson.getString("entryName");
                                int voteCount = entryJson.getInteger("voteCount");

                                Entry entry = new Entry(entryName, voteCount);
                                temp.add(entry);
                            }
                        }
                    }

                    pollResults.setPollFetchTime(Instant.now());
                    pollResults.setEntries(temp);

                } else {
                    res.cause().printStackTrace();
                }
            });
        });
    }

    public PollResults getPollResults() {
        return pollResults;
    }
}
