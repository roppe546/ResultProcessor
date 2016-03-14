package controller;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * This class starts the verticle responsible for the web service.
 *
 * Created by robin on 2016-03-10.
 */
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ResultProcessorVertex(), getDeploymentOptions());
    }

    private static DeploymentOptions getDeploymentOptions() {
        // TODO: Change this to not use localhost
        return new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", 8080)
                        .put("db_name", "evote")
                        .put("connection_string", "mongodb://localhost:" + 27017)
                );
    }
}
