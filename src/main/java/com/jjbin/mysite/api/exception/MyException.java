package com.jjbin.mysite.api.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class MyException extends RuntimeException{

    public final Map<String, String> validation = new HashMap<>();

    public MyException(String message) {
        super(message);
    }


    public abstract int getStatusCode();

    public void addValidation(String fieldName, String fieldMessage){
        validation.put(fieldName, fieldMessage);
    }
}
