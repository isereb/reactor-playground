package com.isereb.projectreactor.playground;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class MyController {

    private final MyService service;

    @GetMapping("/v1/names")
    public Flux<String> names() {
        return service.getNames();
    }

    @GetMapping("/v1/numbers")
    public Flux<Integer> numbers() {
        return service.getNumbers();
    }

}
