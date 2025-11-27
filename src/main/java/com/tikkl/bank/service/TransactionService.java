package com.tikkl.bank.service;

import com.tikkl.bank.dto.request.TransactionSearchRequest;
import com.tikkl.bank.dto.response.TransactionResponse;
import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.Transaction;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MemberService memberService;

    public List<TransactionResponse> getRecentTransactions(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return transactionRepository.findTop10ByMemberOrderByTransactionAtDesc(member).stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public Page<TransactionResponse> searchTransactions(Long memberId, TransactionSearchRequest request) {
        Member member = memberService.findMemberById(memberId);
        
        Transaction.TransactionType transactionType = null;
        if (request.getTransactionType() != null && !request.getTransactionType().isEmpty()) {
            try {
                transactionType = Transaction.TransactionType.valueOf(request.getTransactionType().toUpperCase());
            } catch (IllegalArgumentException e) {
                // 무시하고 전체 조회
            }
        }

        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20
        );

        Page<Transaction> transactions = transactionRepository.searchTransactions(
                member,
                transactionType,
                request.getCategory(),
                request.getStartDate(),
                request.getEndDate(),
                request.getKeyword(),
                pageable
        );

        return transactions.map(TransactionResponse::from);
    }

    public TransactionResponse getTransaction(Long memberId, Long transactionId) {
        Member member = memberService.findMemberById(memberId);
        Transaction transaction = findTransactionById(transactionId);
        
        validateTransactionOwner(transaction, member);
        return TransactionResponse.from(transaction);
    }

    private Transaction findTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    private void validateTransactionOwner(Transaction transaction, Member member) {
        if (!transaction.getMember().getId().equals(member.getId())) {
            throw new TransactionException(ErrorCode.FORBIDDEN);
        }
    }

    public static class TransactionException extends CustomException {
        public TransactionException(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
