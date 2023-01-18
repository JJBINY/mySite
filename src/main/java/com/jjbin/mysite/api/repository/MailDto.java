package com.jjbin.mysite.api.repository;

import com.jjbin.mysite.api.domain.Mail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

@Getter
public class MailDto {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime date;

    public MailDto(Mail mail) {
        this.id = mail.getId();
        this.title = mail.getTitle();
        this.content = mail.getContent();
        this.date = mail.getDate();
    }
}
