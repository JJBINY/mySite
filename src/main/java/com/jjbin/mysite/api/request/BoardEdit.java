package com.jjbin.mysite.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class BoardEdit {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @Lob
    private String content;

    @Builder
    public BoardEdit(String title, String content) {
        this.title = title;
        this.content = content;

    }

    public void validate() {
    }
}
