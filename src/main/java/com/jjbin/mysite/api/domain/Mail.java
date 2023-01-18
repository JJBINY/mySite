package com.jjbin.mysite.api.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Mail {

    @Id @GeneratedValue
    @Column(name = "mail_id")
    private Long id;

    private String title;

    @Lob
    private String content;

    @CreatedDate
    private LocalDateTime date;

    @Builder
    public Mail(String title, String content) {
        this.title = title;
        this.content = content;
    }
}