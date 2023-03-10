package com.jjbin.mysite.api.exception;

/**
 * status -> 400
 */
public class InvalidRequest extends MyException{

    private static final String DEFAULT_MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidRequest(String fieldName, String fieldMessage) {
        super(DEFAULT_MESSAGE);
        addValidation(fieldName,fieldMessage);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
