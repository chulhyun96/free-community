package com.cheolhyeon.free_community.common.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int errorCode;
    private final String errorType;
    private final String errorMessage;

    private ErrorResponse(ErrorStatus errorStatus) {
        this.errorCode = errorStatus.getErrorCode();
        this.errorType = errorStatus.getErrorType();
        this.errorMessage = errorStatus.getErrorMessage();
    }

    public static ErrorResponse of(ErrorStatus errorStatus) {
        return new ErrorResponse(errorStatus);
    }
}
