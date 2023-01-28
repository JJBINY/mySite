package com.jjbin.mysite.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Setter
public class MailSearch {

    private static final int MAX_SIZE = 2000;
    private Integer page =1;

    private Integer size =10;

    public Integer getOffset(){
        return (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
