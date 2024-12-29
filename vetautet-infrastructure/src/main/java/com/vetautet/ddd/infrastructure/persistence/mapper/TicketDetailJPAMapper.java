package com.vetautet.ddd.infrastructure.persistence.mapper;

import com.vetautet.ddd.domain.models.entities.TicketDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketDetailJPAMapper extends JpaRepository<TicketDetail, Long> {

    Optional<TicketDetail> findById(Long id);
}
