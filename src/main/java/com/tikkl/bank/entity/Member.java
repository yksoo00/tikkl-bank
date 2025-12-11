package com.tikkl.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
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

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    private LocalDate birthDate;

    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;

    // 저축 설정 (지금은 구조 유지용)
    @Column(precision = 5, scale = 2)
    private BigDecimal savingsRatio;

    private Boolean autoSavingsEnabled;

    @Builder.Default
    @Column(nullable = false)
    private Boolean onboardingCompleted = false;
}