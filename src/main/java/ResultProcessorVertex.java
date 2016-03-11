import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

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

        // Start fetcher vertex
        DeploymentOptions options = getDeploymentOptions();
        DataFetcherVertex fetcher = new DataFetcherVertex();
        vertx.deployVerticle(fetcher, options);

        // Routes
        Route results_v1 = router.route("/v1/results").method(HttpMethod.GET);

        results_v1.handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");

            // Get poll data from back end
            List<Entry> pollData = fetcher.getEntries();

            response.end(Json.encodePrettily(pollData));
        });

        server.requestHandler(router::accept).listen(8080);
    }

    private DeploymentOptions getDeploymentOptions() {
        // TODO: Change this to not use localhost
        return new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", 8080)
                        .put("db_name", "evote")
                        .put("connection_string", "mongodb://localhost:" + 27017)
                );
    }
}
