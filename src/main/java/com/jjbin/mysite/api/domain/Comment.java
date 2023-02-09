package com.jjbin.mysite.api.domain;

import com.jjbin.mysite.api.request.create.CommentCreate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Comment {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public Comment(CommentCreate commentCreate, Member member, Board board) {
        this.content = commentCreate.getContent();
        this.member = member;
        this.board = board;
        this.createdAt = LocalDateTime.now();
    }
}
