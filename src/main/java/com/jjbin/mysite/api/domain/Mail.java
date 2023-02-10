package com.jjbin.mysite.api.domain;

import com.jjbin.mysite.api.request.create.MailCreate;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Mail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String destination;

    private String title;

    @Lob
    private String content;


    private LocalDateTime createdAt;


    @Builder
    public Mail(Member member, String destination, String title, String content) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.destination = destination;
        createdAt = LocalDateTime.now();
    }

    //==생성 메서드==//
    public static Mail createMail(MailCreate mailCreate, Member member){
        Mail mail = Mail.builder()
                .member(member)
                .destination(mailCreate.getDestination())
                .title(mailCreate.getTitle())
                .content(mailCreate.getContent())
                .build();

        return mail;
    }
}