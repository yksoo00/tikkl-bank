package com.tikkl.bank.service;

import com.tikkl.bank.entity.Account;
import com.tikkl.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Transactional
    public Account createAccount(String accountHolder, BigDecimal initialBalance) {
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountHolder(accountHolder);
        account.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
