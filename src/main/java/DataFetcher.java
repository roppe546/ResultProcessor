import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;

/**
 * This class fetches fresh data from back end every five seconds.
 *
 * Created by robin on 2016-03-10.
 */
public class DataFetcher extends AbstractVerticle {
    private static final int WAIT_TIME = 5000;

    private List<Entry> entries = new ArrayList<>();

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
                        String entryName = json.getString("entryName");
                        int voteCount = json.getInteger("voteCount");
                        float percentage = json.getFloat("percentage");

                        Entry entry = new Entry(entryName, voteCount, percentage);
                        temp.add(entry);

                        entries = temp;
                    }
                } else {
                    res.cause().printStackTrace();
                }
            });

            System.out.println("DataFetcher: Current list: " + entries.toString());
        });
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
