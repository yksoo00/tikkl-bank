package com.tikkl.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal savingsRatio = BigDecimal.valueOf(10); // 기본 저축 비율 10%

    @Column(nullable = false)
    @Builder.Default
    private Boolean autoSavingsEnabled = false; // 자동 저축 활성화 여부

    @Column(nullable = false)
    @Builder.Default
    private Boolean onboardingCompleted = false; // 서비스 온보딩 완료 여부

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
