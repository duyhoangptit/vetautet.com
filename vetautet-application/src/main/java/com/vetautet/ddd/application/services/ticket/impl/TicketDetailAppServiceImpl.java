package com.vetautet.ddd.application.services.ticket.impl;

import com.vetautet.ddd.application.services.ticket.TicketDetailAppService;
import com.vetautet.ddd.application.services.ticket.cache.TicketDetailCacheService;
import com.vetautet.ddd.domain.models.entities.TicketDetail;
import com.vetautet.ddd.domain.services.TicketDetailDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TicketDetailAppServiceImpl implements TicketDetailAppService {

    // CALL Service Domain Module
    @Autowired
    private TicketDetailDomainService ticketDetailDomainService;

    // CALL CACHE
    @Autowired
    private TicketDetailCacheService ticketDetailCacheService;

    @Override
    public TicketDetail getTicketDetailById(Long ticketId) {
        log.info("Implement Application : {}", ticketId);
//        return ticketDetailDomainService.getTicketDetailById(ticketId);
//        return ticketDetailCacheService.getTicketDefaultCacheNormal(ticketId, System.currentTimeMillis());
//        return ticketDetailCacheService.getTicketDefaultCacheVip(ticketId, System.currentTimeMillis());
        return ticketDetailCacheService.getTicketDetailCacheClean(ticketId, System.currentTimeMillis());
    }
}
