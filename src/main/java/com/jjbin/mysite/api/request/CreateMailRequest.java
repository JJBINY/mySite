package com.jjbin.mysite.api.request;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CreateMailRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @Lob
    private String content;


    @Builder
    public CreateMailRequest(String title, String content) {
        Assert.hasText(title,"제목은 필수입니다."); //
        this.title = title;
        this.content = content;
    }
}