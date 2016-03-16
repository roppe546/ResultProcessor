import controller.ResultProcessorVertex;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests the API.
 *
 * Created by robin on 2016-03-10.
 */
@RunWith(VertxUnitRunner.class)
public class ResultProcessorVertxTest {
    private Vertx vertx;


    @Before
    public void setUp() {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new ResultProcessorVertex(), getDeploymentOptions());
    }


    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }


    @Test
    public void getResultData(TestContext context) {
        Async async = context.async();

        HttpClient client = vertx.createHttpClient();

        client.getNow(8080, "localhost", "/v1/testcollection/", res -> {
            context.assertEquals(res.statusCode(), 200);

            res.bodyHandler(body -> {
                JsonObject json = body.toJsonObject();

                context.assertTrue(json.containsKey("pollId"));
                context.assertTrue(json.containsKey("topic"));
                context.assertTrue(json.containsKey("pollStartTime"));
                context.assertTrue(json.containsKey("pollFinishTime"));
                context.assertTrue(json.containsKey("questions"));

                async.complete();
            });
        });
    }


    @Test
    public void getResultDataError(TestContext context) {
        Async async = context.async();

        HttpClient client = vertx.createHttpClient();

        client.getNow(8080, "localhost", "/v1/nonexistantpoll/", res -> {
            context.assertEquals(res.statusCode(), 200);

            res.bodyHandler(body -> {
                JsonObject json = body.toJsonObject();

                context.assertTrue(json.containsKey("error"));

                async.complete();
            });
        });
    }


    private static DeploymentOptions getDeploymentOptions() {
        return new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", 8080)
                        .put("db_name", "evote")
                        .put("connection_string", "mongodb://localhost:" + 27017)
                );
    }
}
