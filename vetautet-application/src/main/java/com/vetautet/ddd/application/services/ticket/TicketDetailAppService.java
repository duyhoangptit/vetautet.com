package com.vetautet.ddd.application.services.ticket;

import com.vetautet.ddd.domain.models.entities.TicketDetail;

public interface TicketDetailAppService {

    TicketDetail getTicketDetailById(Long ticketId); // should convert to TickDetailDTO by Application Module
}
