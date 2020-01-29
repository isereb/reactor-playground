package com.isereb.projectreactor.playground;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyService {

    WebClient client = WebClient.create("http://localhost:8080");

    private final MyRepo repo;
    private final Scheduler scheduler =
            Schedulers.newBoundedElastic(3, 1, "my-scheduler", 5);
    private final Scheduler responseScheduler =
            Schedulers.newBoundedElastic(3, 1, "resoibse", 5);

    @PostConstruct
    public void doSomeWork() {
        log.info("Hello from @PostConstruct");
        Disposable disposable = Flux.just(1).delayElements(Duration.ofSeconds(2))
                .log("scheduled")
                .doOnNext(a -> someWork())
                .subscribe();
        scheduler.schedule(disposable::dispose, 10, TimeUnit.SECONDS);

    }

    public Flux<String> getNames() {
        return repo.getAll()
                .publishOn(responseScheduler)
                .log("my-service")
                .map(MyEntity::getName);
    }

    public Flux<Integer> getNumbers() {
        return repo.getAll()
                .publishOn(responseScheduler)
                .log("my-service")
                .map(MyEntity::getNumber);
    }

    public void someWork() {
        try {
            client.get()
                    .uri("/v1/names")
                    .retrieve()
                    .bodyToFlux(String.class)
                    .publishOn(scheduler)
                    .log("web-client")
                    .doOnNext(e -> log.info("Hello to {} from thread {}", e, Thread.currentThread().getName()))
                    .subscribe();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.info("Thread {} was interrupted", Thread.currentThread().getName());
        }
    }

}
