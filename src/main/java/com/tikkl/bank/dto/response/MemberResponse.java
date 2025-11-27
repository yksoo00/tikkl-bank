package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.Member;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String loginId;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private BigDecimal savingsRatio;
    private Boolean autoSavingsEnabled;
    private Boolean onboardingCompleted;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .birthDate(member.getBirthDate())
                .phoneNumber(member.getPhoneNumber())
                .savingsRatio(member.getSavingsRatio())
                .autoSavingsEnabled(member.getAutoSavingsEnabled())
                .onboardingCompleted(member.getOnboardingCompleted())
                .build();
    }
}
