package com.cheolhyeon.free_community.user.exception;

import com.cheolhyeon.free_community.common.exception.ErrorStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorStatus implements ErrorStatus {
    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "유저 정보를 다시 확인해주세요."
    );

    private final int errorCode;
    private final String errorType;
    private final String errorMessage;

    UserErrorStatus(int errorCode, String errorType, String errorMessage) {
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

}
