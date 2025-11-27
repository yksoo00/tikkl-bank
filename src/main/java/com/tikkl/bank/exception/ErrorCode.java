package com.tikkl.bank.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검사에 실패했습니다"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),

    // Member errors
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다"),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "이미 등록된 전화번호입니다"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),

    // Account errors
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다"),
    DUPLICATE_ACCOUNT(HttpStatus.CONFLICT, "이미 등록된 계좌입니다"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다"),

    // Card errors
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "카드를 찾을 수 없습니다"),
    INVALID_CARD_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 카드 유형입니다"),

    // Savings errors
    SAVINGS_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "저축 계좌를 찾을 수 없습니다"),
    SAVINGS_ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 저축 계좌가 존재합니다"),

    // Transaction errors
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다"),
    TRANSACTION_FAILED(HttpStatus.BAD_REQUEST, "거래 처리에 실패했습니다"),

    // Product errors
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다");

    private final HttpStatus status;
    private final String message;
}
