import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestSuite;
import org.junit.Test;

/**
 * Created by robin on 10/3/16.
 */
public class ResultProcessorVertxTest {
    @Test
    public void testAPI() {
        TestSuite suite = TestSuite.create("testSuite");

        suite.beforeEach(testContext2 -> {
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(new ResultProcessorVertex());
        }).test("Test API", testContext -> {
            System.out.println("running test...");

            // TODO: Add test here.
        });
    }
}
