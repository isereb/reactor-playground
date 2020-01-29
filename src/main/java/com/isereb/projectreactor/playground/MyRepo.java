package com.isereb.projectreactor.playground;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;

@Component // aka @Repository
public class MyRepo {

    private final static Collection<MyEntity> ENTITIES = new ArrayList<>(3);

    static {
        ENTITIES.add(new MyEntity("Foo", 8));
        ENTITIES.add(new MyEntity("Bar", 17));
        ENTITIES.add(new MyEntity("Zak", 42));
    }

    public Flux<MyEntity> getAll() {
        return Flux.fromIterable(ENTITIES);
    }

}
