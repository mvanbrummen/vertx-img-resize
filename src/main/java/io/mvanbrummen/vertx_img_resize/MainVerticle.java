package io.mvanbrummen.vertx_img_resize;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var router = Router.router(vertx);

    var webClient = WebClient.create(vertx, new WebClientOptions().setDefaultHost("nginx-test-images-bucket.s3-ap-southeast-2.amazonaws.com"));

    router.get("/v1/asset/:id").handler(ctx -> {
      var response = ctx.response();
      response.setChunked(true);
      webClient.get("/perm/" + ctx.pathParam("id"))
        .as(BodyCodec.pipe(response))
        .send(ar -> {
          if (ar.failed()) {
            ctx.fail(ar.cause());
          } else {
            // nothing to do
          }
        });
    });

    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

}
