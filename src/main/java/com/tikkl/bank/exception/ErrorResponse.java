package com.tikkl.bank.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final ErrorCode code;
    private final String message;

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code, code.getMessage());
    }

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(code, message);
    }
}
