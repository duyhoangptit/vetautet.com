package com.vetautet.ddd.domain.repositories;

import com.vetautet.ddd.domain.models.entities.TicketDetail;

import java.util.Optional;

public interface TicketDetailRepository {

    Optional<TicketDetail> findById(Long id);
}
