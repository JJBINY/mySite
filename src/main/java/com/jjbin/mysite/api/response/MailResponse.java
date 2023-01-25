package com.jjbin.mysite.api.response;

import com.jjbin.mysite.api.domain.Mail;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MailResponse {

    private Long id;
    private String title;
    private String content;

    private LocalDateTime createdDate;
    public MailResponse(Mail mail) {
        this.id = mail.getId();
        this.title = mail.getTitle();
        this.content = mail.getContent();
        this.createdDate = mail.getCreatedDate();
    }

    @Builder
    public MailResponse(Long id, String title,String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}