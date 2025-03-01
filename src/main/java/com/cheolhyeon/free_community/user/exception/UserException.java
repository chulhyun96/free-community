package com.cheolhyeon.free_community.user.exception;

import com.cheolhyeon.free_community.common.exception.ErrorStatus;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final int errorCode;
    private final String errorMessage;
    private final String errorType;
    private final ErrorStatus errorStatus;

    public UserException(ErrorStatus errorStatus) {
        super(errorStatus.getErrorMessage());
        this.errorStatus = errorStatus;
        this.errorCode = errorStatus.getErrorCode();
        this.errorMessage = errorStatus.getErrorMessage();
        this.errorType = errorStatus.getErrorType();
    }
}
