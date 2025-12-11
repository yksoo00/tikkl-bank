package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String loginId;
    private String name;
    private String phoneNumber;
    private boolean onboardingCompleted;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
            .id(member.getId())
            .loginId(member.getLoginId())
            .name(member.getName())
            .phoneNumber(member.getPhoneNumber())
            .onboardingCompleted(member.getOnboardingCompleted())
            .build();
    }
}