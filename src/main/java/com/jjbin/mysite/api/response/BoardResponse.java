package com.jjbin.mysite.api.response;

import com.jjbin.mysite.api.domain.Board;
import com.jjbin.mysite.api.domain.Mail;
import lombok.*;

import java.time.LocalDateTime;


@Getter
public class BoardResponse {

    private Long id;
    private String writer;
    private String title;
    private String content;


    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.writer = board.getMember().getName();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
        this.lastModifiedAt = board.getLastModifiedAt();

    }

}