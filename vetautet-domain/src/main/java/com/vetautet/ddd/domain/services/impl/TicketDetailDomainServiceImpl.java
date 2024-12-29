package com.vetautet.ddd.domain.services.impl;

import com.vetautet.ddd.domain.models.entities.TicketDetail;
import com.vetautet.ddd.domain.repositories.TicketDetailRepository;
import com.vetautet.ddd.domain.services.TicketDetailDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TicketDetailDomainServiceImpl implements TicketDetailDomainService {
    // Call repository in domain

    @Autowired
    private TicketDetailRepository ticketDetailRepository;

    @Override
    public TicketDetail getTicketDetailById(Long ticketId) {
//        log.info("Implement Domain : {}", ticketId);
        return ticketDetailRepository.findById(ticketId).orElse(null);
    }
}