package com.vetautet.ddd.controller.http;


import com.vetautet.ddd.application.services.ticket.TicketDetailAppService;
import com.vetautet.ddd.controller.models.enums.ResultUtil;
import com.vetautet.ddd.controller.models.vo.ResultMessage;
import com.vetautet.ddd.domain.models.entities.TicketDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ticket")
public class TicketDetailController {

    @Autowired
    private TicketDetailAppService ticketDetailAppService;

    @GetMapping("/{ticketId}/detail/{detailId}")
    public ResultMessage<TicketDetail> getTicketDetail(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("detailId") Long detailId,
            @RequestParam Long version
    ) {
        log.info(" ticketId:{}, detailId:{}", ticketId, detailId);
        return ResultUtil.data(ticketDetailAppService.getTicketDetailById(detailId, version));
    }
}

