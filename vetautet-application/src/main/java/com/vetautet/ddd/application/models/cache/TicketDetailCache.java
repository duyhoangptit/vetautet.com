package com.vetautet.ddd.application.models.cache;

import com.vetautet.ddd.domain.models.entities.TicketDetail;
import lombok.Getter;

@Getter
public class TicketDetailCache {
    private Long version;
    private TicketDetail ticketDetail;

    public TicketDetailCache() {}

    public TicketDetailCache(TicketDetail ticketDetail) {
        this.ticketDetail = ticketDetail;
    }

    public TicketDetailCache withVersion(Long version) {
        this.version = version;
        return this;
    }

    public TicketDetailCache withTicketDetail(TicketDetail ticketDetail) {
        this.ticketDetail = ticketDetail;
        return this;
    }
}
