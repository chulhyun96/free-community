package com.cheolhyeon.free_community.common.exception;

public interface ErrorStatus {
    int getErrorCode();
    String getErrorMessage();
    String getErrorType();
}
