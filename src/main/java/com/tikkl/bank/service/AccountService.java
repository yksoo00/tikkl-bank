package com.tikkl.bank.service;

import com.tikkl.bank.dto.request.AccountRequest;
import com.tikkl.bank.dto.request.DepositRequest;
import com.tikkl.bank.dto.request.WithdrawRequest;
import com.tikkl.bank.dto.response.AccountResponse;
import com.tikkl.bank.dto.response.TransactionResponse;
import com.tikkl.bank.entity.Account;
import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.Transaction;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.AccountRepository;
import com.tikkl.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MemberService memberService;

    public List<AccountResponse> getAccounts(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return accountRepository.findByMemberAndIsActiveTrue(member).stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccount(Long memberId, Long accountId) {
        Member member = memberService.findMemberById(memberId);
        Account account = findAccountById(accountId);
        
        validateAccountOwner(account, member);
        return AccountResponse.from(account);
    }

    @Transactional
    public AccountResponse registerAccount(Long memberId, AccountRequest request) {
        Member member = memberService.findMemberById(memberId);

        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new AccountException(ErrorCode.DUPLICATE_ACCOUNT);
        }

        Account account = Account.builder()
                .member(member)
                .accountNumber(request.getAccountNumber())
                .bankName(request.getBankName())
                .accountType(Account.AccountType.CHECKING)
                .isPrimary(request.getIsPrimary() != null && request.getIsPrimary())
                .build();

        // 주 계좌로 설정 시 기존 주 계좌 해제
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            accountRepository.findByMemberAndIsPrimaryTrue(member)
                    .ifPresent(existingPrimary -> existingPrimary.setIsPrimary(false));
        }

        Account savedAccount = accountRepository.save(account);
        return AccountResponse.from(savedAccount);
    }

    @Transactional
    public AccountResponse setPrimaryAccount(Long memberId, Long accountId) {
        Member member = memberService.findMemberById(memberId);
        Account account = findAccountById(accountId);
        
        validateAccountOwner(account, member);

        // 기존 주 계좌 해제
        accountRepository.findByMemberAndIsPrimaryTrue(member)
                .ifPresent(existingPrimary -> existingPrimary.setIsPrimary(false));

        account.setIsPrimary(true);
        return AccountResponse.from(account);
    }

    @Transactional
    public void deleteAccount(Long memberId, Long accountId) {
        Member member = memberService.findMemberById(memberId);
        Account account = findAccountById(accountId);
        
        validateAccountOwner(account, member);
        account.setIsActive(false);
    }

    @Transactional
    public TransactionResponse deposit(Long memberId, Long accountId, DepositRequest request) {
        Member member = memberService.findMemberById(memberId);
        Account account = findAccountById(accountId);
        
        validateAccountOwner(account, member);

        // 잔액 증가
        account.setBalance(account.getBalance().add(request.getAmount()));

        // 거래 내역 생성
        Transaction transaction = Transaction.builder()
                .member(member)
                .account(account)
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .balanceAfter(account.getBalance())
                .description(request.getDescription() != null ? request.getDescription() : "입금")
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionResponse.from(savedTransaction);
    }

    @Transactional
    public TransactionResponse withdraw(Long memberId, Long accountId, WithdrawRequest request) {
        Member member = memberService.findMemberById(memberId);
        Account account = findAccountById(accountId);
        
        validateAccountOwner(account, member);

        // 잔액 확인
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new AccountException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 잔액 감소
        account.setBalance(account.getBalance().subtract(request.getAmount()));

        // 거래 내역 생성
        Transaction transaction = Transaction.builder()
                .member(member)
                .account(account)
                .transactionType(Transaction.TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .balanceAfter(account.getBalance())
                .description(request.getDescription() != null ? request.getDescription() : "출금")
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionResponse.from(savedTransaction);
    }

    public Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private void validateAccountOwner(Account account, Member member) {
        if (!account.getMember().getId().equals(member.getId())) {
            throw new AccountException(ErrorCode.FORBIDDEN);
        }
    }

    public static class AccountException extends CustomException {
        public AccountException(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
