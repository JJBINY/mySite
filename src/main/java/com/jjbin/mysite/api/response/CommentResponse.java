package com.jjbin.mysite.api.response;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class CommentResponse {

    private Long id;
    private String writer;
    private String content;

    private LocalDateTime createdAt;

    @Builder
    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.writer = comment.getMember().getName();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();

    }

}