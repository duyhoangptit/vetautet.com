package com.vetautet.ddd.application.services.events.impl;

import com.vetautet.ddd.application.services.events.EventAppService;
import org.springframework.stereotype.Service;

@Service
public class EventAppServiceImpl implements EventAppService {

    @Override
    public String sayHi(String who) {
        return "";
    }
}
