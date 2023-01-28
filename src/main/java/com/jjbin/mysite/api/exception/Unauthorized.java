package com.jjbin.mysite.api.exception;

/**
 * status -> 401
 */
public class Unauthorized extends MyException{

    private static final String DEFAULT_MESSAGE = "인증이 필요한 요청입니다.";

    public Unauthorized() {
        super(DEFAULT_MESSAGE);
    }

    public Unauthorized(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
