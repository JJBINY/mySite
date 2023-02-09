package com.jjbin.mysite.api.request.create;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreate {

    @NotBlank(message = "댓글을 작성해주세요.")
    private String content;

    @Builder
    public CommentCreate(String content) {
        this.content = content;
    }
}
