package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RestController
@RequestMapping
public class Resource {

    @Autowired
    private Service service;

    @GetMapping("/slow-sync")
    public String slowSync() throws InterruptedException {
        return service.doWork();
    }

    @GetMapping("/slow-async")
    public Callable<String> slowAsync() throws InterruptedException {

        System.out.println("callable controller - " + Thread.currentThread());

        return () -> service.doWork();
    }

    @GetMapping("/slow-nio")
    public DeferredResult<String> slowAsyncNio() throws InterruptedException {

        System.out.println("nio controller - " + Thread.currentThread());

        DeferredResult<String> result = new DeferredResult<>();
        service.doWorkAsync(result);
        return result;
    }

    @GetMapping("/slow-reactor")
    public DeferredResult<String> slowAsyncReactor() throws InterruptedException {
        System.out.println("reactor controller - " + Thread.currentThread());
        DeferredResult<String> result = new DeferredResult<>();
        service.doWorkAsyncReactor(result);
        return result;
    }

    @GetMapping("/slow-reactor-flux")
    public Object slowAsyncReactorFlux() throws InterruptedException {

        System.out.println("reactor flux controller - " + Thread.currentThread());

        return service.doWorkAsyncReactorFlux();
    }

    @GetMapping("/fast")
    public String fast() throws InterruptedException {
        return "it is fast ok";
    }
}
