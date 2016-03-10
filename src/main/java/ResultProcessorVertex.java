import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.LinkedHashMap;
import java.util.Map;

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

        Route results_v1 = router.route("/v1/results").method(HttpMethod.GET);

        results_v1.handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");

            // Get poll data from back end
            // TODO: Should probably use a custom data structure instead of a Map
            Map<Integer, Entry> pollData = getPollData();

            response.end(Json.encodePrettily(pollData.values()));
        });

        server.requestHandler(router::accept).listen(8080);
    }


    /**
     * Retrieve poll data from a specific vote.
     */
    // TODO: Should probably use a custom data structure instead of a Map
    private Map<Integer, Entry> getPollData() {
        // TODO: Get data from back end instead of using mockup data
        return createMockData();
    }


    /**
     * Create some mock data for sending back
     */
    private Map<Integer, Entry> createMockData() {
        Entry entry1 = new Entry("Entry 1", 1000000, 0.50f);
        Entry entry2 = new Entry("Entry 2", 500000, 0.25f);
        Entry entry3 = new Entry("Entry 3", 500000, 0.25f);

        Map<Integer, Entry> data = new LinkedHashMap<>();

        data.put(entry1.getId(), entry1);
        data.put(entry2.getId(), entry2);
        data.put(entry3.getId(), entry3);

        return data;
    }
}
