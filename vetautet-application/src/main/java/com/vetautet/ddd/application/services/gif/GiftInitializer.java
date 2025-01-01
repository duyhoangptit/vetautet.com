package com.vetautet.ddd.application.services.gif;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class GiftInitializer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GiftService giftService;

    @PostConstruct
    public void initializeGiftCount() {
        redisTemplate.opsForValue().set("gift:remaining", "100");
        giftService.getRemainingGifts(1L);
    }
}
