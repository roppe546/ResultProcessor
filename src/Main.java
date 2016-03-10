import io.vertx.core.Vertx;

/**
 * Created by robin on 10/3/16.
 */
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ResultProcesserVertex());
    }
}
