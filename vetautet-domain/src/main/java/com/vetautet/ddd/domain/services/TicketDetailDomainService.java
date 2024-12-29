package com.vetautet.ddd.domain.services;

import com.vetautet.ddd.domain.models.entities.TicketDetail;

public interface TicketDetailDomainService {
    TicketDetail getTicketDetailById(Long ticketId);
}
