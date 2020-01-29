package com.isereb.projectreactor.playground;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyService {

    private final MyRepo repo;
    private final Scheduler scheduler =
            Schedulers.newBoundedElastic(3, 1, "my-scheduler", 60);

    @PostConstruct
    public void doSomeWork() {
        log.info("Hello from @PostConstruct");
        Disposable.Composite disposable = Disposables.composite(
                scheduler.createWorker()
                        .schedulePeriodically(this::someWork, 0, 50, TimeUnit.MILLISECONDS),
                scheduler.createWorker()
                        .schedulePeriodically(this::someWork, 0, 50, TimeUnit.MILLISECONDS),
                scheduler.createWorker()
                        .schedulePeriodically(this::someWork, 0, 50, TimeUnit.MILLISECONDS));
        scheduler.schedule(disposable::dispose, 10, TimeUnit.SECONDS);
    }

    public Flux<String> getNames() {
        return repo.getAll()
                .log()
                .map(MyEntity::getName);
    }

    public Flux<Integer> getNumbers() {
        return repo.getAll()
                .log()
                .map(MyEntity::getNumber);
    }

    public void someWork() {
        try {
            Thread.sleep(1000);
            getNames()
                    .log()
                    .doOnNext(n -> log.info("Hello {} from {}", n, Thread.currentThread().getName()))
                    .subscribe();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.info("Thread {} was interrupted", Thread.currentThread().getName());
        }
    }

}
