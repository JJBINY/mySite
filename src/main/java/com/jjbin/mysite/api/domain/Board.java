package com.jjbin.mysite.api.domain;

import com.jjbin.mysite.api.request.BoardEdit;
import com.jjbin.mysite.api.request.create.BoardCreate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Board {

    @Id @GeneratedValue
    @Column(name="board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

//TODO    private String category;

    private String title;

    @Lob
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    @Builder
    public Board(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
        createdAt = LocalDateTime.now();
        lastModifiedAt = createdAt;
    }

    //==생성 메서드==//
    public static Board createBoard(BoardCreate boardCreate, Member member){
        Board board = Board.builder()
                .member(member)
                .title(boardCreate.getTitle())
                .content(boardCreate.getContent())
                .build();

        return board;
    }

    public void edit(BoardEdit boardEdit){
        if (boardEdit.getTitle() != null) {
            title = boardEdit.getTitle();
        }
        if (boardEdit.getContent() != null) {
            content = boardEdit.getContent();
        }

        this.lastModifiedAt = LocalDateTime.now();
    }
}
