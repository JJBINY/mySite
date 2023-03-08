package com.jjbin.mysite.api.request.create;

import lombok.*;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MessageCreate {

    @NotBlank(message = "전송할 대상을 입력해주세요.")
    private String toLoginId;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @Builder
    public MessageCreate(String toLoginId, String content) {
        this.toLoginId = toLoginId;
        this.content = content;
    }

    public void validate() {
    }
}