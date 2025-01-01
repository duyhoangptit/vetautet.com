package com.vetautet.ddd.domain.repositories;


import com.vetautet.ddd.domain.models.entities.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftRepository extends JpaRepository<Gift, Long> {
}
