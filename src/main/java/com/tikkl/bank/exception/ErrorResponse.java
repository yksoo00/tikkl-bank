package com.tikkl.bank.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String errorCode;
    private final String message;
    private final String path;

    public static ErrorResponse of(int status, String error, String errorCode, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, errorCode, message, path);
    }
}
