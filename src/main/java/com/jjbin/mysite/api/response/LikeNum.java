package com.jjbin.mysite.api.response;

import com.jjbin.mysite.api.domain.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
public class LikeNum {
    private Long count;

    @Builder
    public LikeNum(Long count) {
        this.count = count;
    }


}