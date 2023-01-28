package com.jjbin.mysite.api.request;

import com.jjbin.mysite.api.exception.InvalidRequest;
import lombok.*;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MailCreate {

    @NotBlank(message = "목적지 주소를 입력해주세요.")
    private String destination;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @Lob
    private String content;

    @Builder
    public MailCreate(String destination, String title, String content) {
        this.destination = destination;
        this.title = title;
        this.content = content;

    }

    public void validate() {
    }
}