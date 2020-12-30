package com.example.demo;

import io.netty.handler.codec.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
public class Service {
    HttpClient client;
    reactor.netty.http.client.HttpClient reactorHttpClient;
    RestTemplate restTemplate = new RestTemplate();

    {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        client = HttpClient.newBuilder().executor(executorService).build();

        reactorHttpClient = reactor.netty.http.client.HttpClient.create();
    }

    public String doWork() {

        System.out.println("callable - " + Thread.currentThread());

        String result = restTemplate.getForObject("http://localhost:8081/", String.class);
        return result;
    }

    public void doWorkAsync(DeferredResult<String> result) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/"))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(el -> {
                    System.out.println("nio - " + Thread.currentThread());
                    result.setResult(el);
                });
    }

    public void doWorkAsyncReactor(DeferredResult<String> result) {

        reactorHttpClient.request(HttpMethod.GET)
                .uri("http://localhost:8081/")
                .responseContent()
                .asString()
                .subscribe(el -> {
                    System.out.println("reactor - " + Thread.currentThread());
                    result.setResult(el);
                });
    }

    public Flux<String> doWorkAsyncReactorFlux() {

        return reactorHttpClient.request(HttpMethod.GET)
                .uri("http://localhost:8081/")
                .responseContent()
                .asString();
    }
}
