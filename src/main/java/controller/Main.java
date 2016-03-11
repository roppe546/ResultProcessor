package controller;

import io.vertx.core.Vertx;

/**
 * This class starts the verticle responsible for the web service.
 *
 * Created by robin on 2016-03-10.
 */
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ResultProcessorVertex());
    }
}
