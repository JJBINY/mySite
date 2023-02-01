package com.jjbin.mysite.api.request;

import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Setter
public class SearchOption {

    private static final int MAX_SIZE = 2000;
    private Integer page =1;

    private Integer size =10;

    private String keyword;

    public Integer getOffset(){
        return (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
