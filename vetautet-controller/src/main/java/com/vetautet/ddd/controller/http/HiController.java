package com.vetautet.ddd.controller.http;

import com.vetautet.ddd.application.services.events.EventAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HiController {

    private final EventAppService eventAppService;

    @GetMapping
    public String hello() {
        return eventAppService.sayHi("system");
    }
}
