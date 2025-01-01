package com.vetautet.ddd.controller.http;

import com.vetautet.ddd.application.services.gif.GiftService;
import com.vetautet.ddd.controller.models.enums.ResultUtil;
import com.vetautet.ddd.controller.models.vo.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/gif")
public class GifController {
    @Autowired
    private GiftService giftService;

    @GetMapping("/return")
    public ResultMessage<Void> gifReturn() {
        log.info("gifReturn:{}", Thread.currentThread().getName());
        giftService.claimGiftVip(1L);
        return ResultUtil.success();
    }
}
