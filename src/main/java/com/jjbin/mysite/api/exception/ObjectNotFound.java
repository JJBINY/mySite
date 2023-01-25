package com.jjbin.mysite.api.exception;

/**
 * status -> 404
 */
public class ObjectNotFound extends MyException{

    private static final String DEFAULT_MESSAGE = "존재하지 않는 객체입니다.";

    public ObjectNotFound() {
        super(DEFAULT_MESSAGE);
    }

    public ObjectNotFound(String message) {
        super(message);
    }

    public ObjectNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
