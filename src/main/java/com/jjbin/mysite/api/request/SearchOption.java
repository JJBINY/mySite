package com.jjbin.mysite.api.request;

import com.jjbin.mysite.api.domain.Comment;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.min;


@Getter
@Setter
public class SearchOption {

    private static final int MAX_SIZE = 1000;
    private Integer page;

    private Integer size;

    private String keyword;

    public Integer getOffset(){
        return (max(1, page) - 1) * min(size, MAX_SIZE);
    }

    public void validate() {
        if (size == null) {
            size = 10;
        }
        if (page == null) {
            page = 0;
        }
//        if(target instanceof Comment){
//            keyword = null;
//        }
    }
}
