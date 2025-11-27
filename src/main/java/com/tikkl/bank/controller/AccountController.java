package com.tikkl.bank.controller;

import com.tikkl.bank.entity.Account;
import com.tikkl.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> request) {
        String accountHolder = (String) request.get("accountHolder");
        BigDecimal initialBalance = request.get("initialBalance") != null
                ? new BigDecimal(request.get("initialBalance").toString())
                : BigDecimal.ZERO;
        Account account = accountService.createAccount(accountHolder, initialBalance);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        Account account = accountService.deposit(id, amount);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        Account account = accountService.withdraw(id, amount);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
