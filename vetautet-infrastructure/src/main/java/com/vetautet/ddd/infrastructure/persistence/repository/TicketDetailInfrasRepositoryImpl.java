package com.vetautet.ddd.infrastructure.persistence.repository;

import com.vetautet.ddd.domain.models.entities.TicketDetail;
import com.vetautet.ddd.domain.repositories.TicketDetailRepository;
import com.vetautet.ddd.infrastructure.persistence.mapper.TicketDetailJPAMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TicketDetailInfrasRepositoryImpl implements TicketDetailRepository {

    @Autowired
    private TicketDetailJPAMapper ticketDetailJPAMapper;

    @Override
    public Optional<TicketDetail> findById(Long id) {
        log.info("Implement Infrastructure : {}", id);
        return ticketDetailJPAMapper.findById(id);
    }
}
