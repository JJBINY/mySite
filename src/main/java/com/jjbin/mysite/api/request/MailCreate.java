package com.jjbin.mysite.api.request;

import com.jjbin.mysite.api.exception.InvalidRequest;
import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MailCreate {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @Lob
    private String content;


    @Builder
    public MailCreate(String title, String content) {
        Assert.hasText(title,"제목은 필수입니다."); //
        this.title = title;
        this.content = content;
    }

    public void validate() {
        if(title.length()<4){
            throw new InvalidRequest("title", "제목은 4자 이상이어야 합니다.");
        }
    }
}