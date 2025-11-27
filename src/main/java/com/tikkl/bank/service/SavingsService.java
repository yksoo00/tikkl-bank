package com.tikkl.bank.service;

import com.tikkl.bank.dto.response.SavingsAccountResponse;
import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.SavingsAccount;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavingsService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final MemberService memberService;

    public SavingsAccountResponse getSavingsAccount(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        SavingsAccount savingsAccount = findSavingsAccountByMember(member);
        return SavingsAccountResponse.from(savingsAccount);
    }

    public SavingsAccount findSavingsAccountByMember(Member member) {
        return savingsAccountRepository.findByMember(member)
                .orElseThrow(() -> new SavingsException(ErrorCode.SAVINGS_ACCOUNT_NOT_FOUND));
    }

    public static class SavingsException extends CustomException {
        public SavingsException(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
