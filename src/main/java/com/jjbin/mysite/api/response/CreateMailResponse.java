package com.jjbin.mysite.api.response;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CreateMailResponse {

    private Long id;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @Builder
    public CreateMailResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}