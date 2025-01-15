package com.vetautet.ddd.domain.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gifts")
public class Gift {
    @Id
    private Long id;
    private String name;
    @Column(name = "remaining_quantity")
    private Integer remainingQuantity;
}
