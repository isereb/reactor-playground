package com.isereb.projectreactor.playground;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyController {

    private final MyService service;

    @GetMapping("/v1/names")
    public Mono<List<String>> names() {
        return service.getNames().collectList();
    }

    @GetMapping("/v1/numbers")
    public Flux<Integer> numbers() {
        return service.getNumbers();
    }

}
