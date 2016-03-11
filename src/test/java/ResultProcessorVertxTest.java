import controller.ResultProcessorVertex;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.TestSuite;
import io.vertx.ext.unit.junit.VertxUnitRunner;
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
        vertx.deployVerticle(new ResultProcessorVertex());
    }

    @Test
    public void getResultData(TestContext context) {
        HttpClient client = vertx.createHttpClient();

        client.getNow(8080, "localhost", "/results", res -> {
           context.assertEquals(res.statusCode(), 200);
        });
    }
}
