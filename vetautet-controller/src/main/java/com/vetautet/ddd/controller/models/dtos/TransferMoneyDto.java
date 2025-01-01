package com.vetautet.ddd.controller.models.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferMoneyDto {
    String fromAccount;
    String toAccount;
    BigDecimal amount;
}
