package com.jjbin.mysite.api.request.create;

import com.jjbin.mysite.api.domain.Comment;
import com.jjbin.mysite.api.repository.board.CommentRepository;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreate {

    @NotBlank(message = "댓글을 작성해주세요.")
    private String content;

    @Nullable
    private Long parentId;

    @Builder
    public CommentCreate(String content, Long parentId) {
        this.content = content;
        this.parentId = parentId;

    }
}
