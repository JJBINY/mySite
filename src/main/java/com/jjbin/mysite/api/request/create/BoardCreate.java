package com.jjbin.mysite.api.request.create;

import com.jjbin.mysite.api.domain.Category;
import lombok.*;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCreate {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    private Category category;
    @Lob
    private String content;

    @Builder
    public BoardCreate(String title, String content) {
        this.title = title;
        this.content = content;

    }

    public void validate() {
    }
}