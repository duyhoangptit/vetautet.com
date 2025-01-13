package com.vetautet.ddd.application.services.gif;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GiftInitializer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GiftService giftService;

    @PostConstruct
    public void initializeGiftCount() {
        redisTemplate.opsForValue().getAndExpire("gift:remaining", 1, TimeUnit.SECONDS);
        redisTemplate.opsForValue().getAndExpire("gift:1", 1, TimeUnit.SECONDS);
//        giftService.getRemainingGifts(1L);
    }
}
