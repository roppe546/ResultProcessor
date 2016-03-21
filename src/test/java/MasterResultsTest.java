import controller.ResultProcessorVertex;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import model.Answer;
import model.Question;
import model.Results;
import org.junit.*;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Created by robin on 18/3/16.
 */
@RunWith(VertxUnitRunner.class)
public class MasterResultsTest {
    private Vertx vertx;
    private static MongodProcess MONGO;
    private static int MONGO_PORT = 12345;

    @BeforeClass
    public static void initialize() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();

        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(MONGO_PORT, Network.localhostIsIPv6()))
                .build();

        MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
        MONGO = mongodExecutable.start();
    }

    @AfterClass
    public static void shutdown() {
        MONGO.stop();
    }

    /**
     * Before executing our test, let's deploy our verticle.
     * <p>
     * This method instantiates a new Vertx and deploy the verticle. Then, it waits in the verticle has successfully
     * completed its start sequence (thanks to `context.asyncAssertSuccess`).
     *
     * @param context the test context.
     */
    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        // Let's configure the verticle to listen on the 'test' port (randomly picked).
        // We create deployment options and set the _configuration_ json object:
        ServerSocket socket = new ServerSocket(0);
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                                .put("http.port", 8080)
                                .put("db_name", "evote")
                                .put("connection_string", "mongodb://localhost:" + MONGO_PORT)
                );

        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(ResultProcessorVertex.class.getName(), options);
    }

    /**
     * This method, called after our test, just cleanup everything by closing the vert.x instance
     *
     * @param context the test context
     */
    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }


    @Test
    public void checkThatWeCanAdd(TestContext context) {
        Async async = context.async();

        Results results = createResults();

        final String json = new JsonObject().put("voting", new JsonObject(Json.encode(results))).encode();

        vertx.createHttpClient().post(7670, "localhost", "/api/results/")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    System.out.println("getting resp code: " + response.statusCode() + " == " + HttpResponseStatus.OK.code());
                    context.assertEquals(response.statusCode(), HttpResponseStatus.OK.code());
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    async.complete();
                })
                .write(json)
                .end();
    }


    private Results createResults() {
        ArrayList<String> keysList1 = new ArrayList<>();
        keysList1.add("111");
        keysList1.add("222");
        keysList1.add("333");
        keysList1.add("444");
        keysList1.add("555");
        keysList1.add("666");

        ArrayList<String> keysList2 = new ArrayList<>();
        keysList1.add("777");
        keysList1.add("888");
        keysList1.add("999");
        keysList1.add("101010");
        keysList1.add("111111");

        Answer ans1 = new Answer("Yes", keysList1.size(), keysList1);
        Answer ans2 = new Answer("No", keysList2.size(), keysList2);

        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(ans1);
        answers.add(ans2);

        Question questions = new Question("Do you like cats?", answers);
        ArrayList<Question> questionsList = new ArrayList<>();
        questionsList.add(questions);

        Results results = new Results("testcollection", "Owner", "Cats", Instant.now(), Instant.now(), questionsList);

        System.out.println("TEST: " + Json.encodePrettily(results));

        return results;
    }
}
