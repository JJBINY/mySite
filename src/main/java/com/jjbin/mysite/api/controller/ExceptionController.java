package com.jjbin.mysite.api.controller;

import com.jjbin.mysite.api.exception.MyException;
import com.jjbin.mysite.api.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@Slf4j
@ControllerAdvice
public class ExceptionController {
    /**
     *스프링 기본 검증 에러 핸들러
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {

        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return response;
    }

    /**
     * 커스텀 에러 핸들러
     */
    @ResponseBody
    @ExceptionHandler(MyException.class)
    public ResponseEntity<ErrorResponse> myException(MyException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .build();

        for (String field : e.getValidation().keySet()) {
            body.addValidation(field,e.getValidation().get(field));
        }

        ResponseEntity<ErrorResponse> response = ResponseEntity
                .status(statusCode)
                .body(body);

        return response;
    }
}
