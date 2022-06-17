package com.openfaas.function;

import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import okhttp3.*;
import redis.clients.jedis.Jedis;

public class Handler extends com.openfaas.model.AbstractHandler {
    Logger logger = Logger.getLogger("RAJAONGKIR");
    OkHttpClient httpClient = new OkHttpClient.Builder().build();
    final MediaType JSON = MediaType.parse("application/json");

    public String rajaongkir_url = System.getenv("RAJAONGKIR_URL");
    public String rajaongkir_token = System.getenv("RAJAONGKIR_TOKEN");
    public String redis_url = System.getenv("REDIS_URL");

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        res.setContentType("application/json");
        res.setBody("{\"error\": \"Not Found\"}");

        String body         = req.getBody();
        String path         = req.getPathRaw();
        String pathRawQuery = req.getQueryRaw();

        logger.info("metode request1: " + req.toString());

        RequestBody reqBody = RequestBody.create(JSON, body);
        Request request;

        if (pathRawQuery != null) path = path + "?" + pathRawQuery;

        if (body == null || body == "") {
            // build request.
            logger.info("Build request sebelum kirim.");
            request = new Request.Builder()
                    .url(rajaongkir_url + path)
                    .get()
                    .addHeader("key", rajaongkir_token)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-type", "application/json")
                    .build();
        } else {
            // build request.
            logger.info("Build request sebelum kirim.");
            request = new Request.Builder()
                    .url(rajaongkir_url + path)
                    .post(reqBody)
                    .addHeader("key", rajaongkir_token)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-type", "application/json")
                    .build();
        }

        try {
            logger.info("Hitting Endpoint rajaongkir: " + rajaongkir_url + path);
            okhttp3.Response response = httpClient.newCall(request).execute();
            logger.info("method request : " + request.method());
            res.setBody(response.body().string());
            res.setStatusCode(200);
        } catch (IOException e) {
            e.printStackTrace();
            res.setBody("{\"error\": "+ e.getMessage() +" }");
        }

        return res;
    }
}
