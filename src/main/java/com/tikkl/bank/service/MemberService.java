package com.tikkl.bank.service;

import com.tikkl.bank.dto.request.LoginRequest;
import com.tikkl.bank.dto.request.SavingsSettingRequest;
import com.tikkl.bank.dto.request.SignupRequest;
import com.tikkl.bank.dto.response.MemberResponse;
import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.SavingsAccount;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.MemberRepository;
import com.tikkl.bank.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final SavingsAccountRepository savingsAccountRepository;

    @Transactional
    public MemberResponse signup(SignupRequest request) {
        // 아이디 중복 확인
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new MemberException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 전화번호 중복 확인
        if (memberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new MemberException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(request.getPassword()) // TODO: 비밀번호 암호화 필요
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .phoneNumber(request.getPhoneNumber())
                .build();

        Member savedMember = memberRepository.save(member);

        // 티끌 전용 저축 계좌 자동 생성
        createSavingsAccount(savedMember);

        return MemberResponse.from(savedMember);
    }

    public MemberResponse login(LoginRequest request) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // TODO: 비밀번호 암호화 비교 필요
        if (!member.getPassword().equals(request.getPassword())) {
            throw new MemberException(ErrorCode.INVALID_PASSWORD);
        }

        return MemberResponse.from(member);
    }

    public MemberResponse getMember(Long memberId) {
        Member member = findMemberById(memberId);
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updateSavingsSettings(Long memberId, SavingsSettingRequest request) {
        Member member = findMemberById(memberId);

        if (request.getSavingsRatio() != null) {
            member.setSavingsRatio(request.getSavingsRatio());
        }
        if (request.getAutoSavingsEnabled() != null) {
            member.setAutoSavingsEnabled(request.getAutoSavingsEnabled());
        }

        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse completeOnboarding(Long memberId) {
        Member member = findMemberById(memberId);
        member.setOnboardingCompleted(true);
        return MemberResponse.from(member);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void createSavingsAccount(Member member) {
        String accountNumber = "TIKKL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        SavingsAccount savingsAccount = SavingsAccount.builder()
                .member(member)
                .accountNumber(accountNumber)
                .build();

        savingsAccountRepository.save(savingsAccount);
    }

    public static class MemberException extends CustomException {
        public MemberException(ErrorCode errorCode) {
            super(errorCode);
        }

        public MemberException(ErrorCode errorCode, String message) {
            super(errorCode, message);
        }
    }
}
