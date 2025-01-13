package com.vetautet.ddd.application.services.bank;

import com.vetautet.ddd.domain.models.entities.BankAccount;
import com.vetautet.ddd.domain.repositories.BankAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public void transferMoney(String fromAccount, String toAccount, BigDecimal amount) {
        // Lấy tài khoản nguồn với khóa
        BankAccount sourceAccount = bankAccountRepository.findByAccountNumberForUpdate(fromAccount)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        // Lấy tài khoản đích với khóa
        BankAccount targetAccount = bankAccountRepository.findByAccountNumberForUpdate(toAccount)
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        // Kiểm tra số dư
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Trừ tiền từ tài khoản nguồn
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        bankAccountRepository.save(sourceAccount);

        // Cộng tiền vào tài khoản đích
        targetAccount.setBalance(targetAccount.getBalance().add(amount));
        bankAccountRepository.save(targetAccount);
    }

    private final RedissonClient redissonClient;

    private static final String LOCK_KEY_PREFIX = "lock:account:";

    /**
     * Thực hiện giao dịch với Redis distributed lock
     */
    public void performTransaction(String accountNumber, BigDecimal amount) {
        String lockKey = LOCK_KEY_PREFIX + accountNumber;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Thử lấy khóa với thời gian chờ 10 giây và thời gian hết hạn 5 giây
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                // Lấy tài khoản từ DB
                BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                // Kiểm tra số dư (nếu là giao dịch trừ tiền)
                if (amount.compareTo(BigDecimal.ZERO) < 0 && account.getBalance().compareTo(amount.abs()) < 0) {
                    throw new RuntimeException("Insufficient balance");
                }

                // Cập nhật số dư
                account.setBalance(account.getBalance().add(amount));
                bankAccountRepository.save(account);
            } else {
                throw new RuntimeException("Unable to acquire lock");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock acquisition interrupted");
        } finally {
            // Giải phóng khóa
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
