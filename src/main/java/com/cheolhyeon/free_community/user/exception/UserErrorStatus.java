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
    ), INVALID_NICKNAME_LENGTH(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "닉네임은 1~10자 사이여야 합니다."
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
