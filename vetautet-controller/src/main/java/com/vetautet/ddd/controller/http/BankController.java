package com.vetautet.ddd.controller.http;

import com.vetautet.ddd.application.services.bank.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/bank")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping("/transfer")
    public void transferMoney() {
        int randomNumber = (int) (Math.random() * 4) + 1;
        String fromAccount = "";
        String toAccount = "";
        if (randomNumber == 4) {
            fromAccount =  "AC000" + randomNumber;
            toAccount = "AC000" + (randomNumber - 1);
        } else if (randomNumber == 1) {
            fromAccount =  "AC000" + randomNumber;
            toAccount = "AC000" + (randomNumber + 1);
        } else {
            fromAccount =  "AC000" + randomNumber;
            toAccount = "AC000" + (randomNumber + 1);
        }
        BigDecimal amount = new BigDecimal(100);

        log.info("fromAccount {}", fromAccount);
        log.info("toAccount {}", toAccount);
        log.info("amount {}", amount);

        bankService.transferMoney(fromAccount, toAccount, amount);
    }
}
