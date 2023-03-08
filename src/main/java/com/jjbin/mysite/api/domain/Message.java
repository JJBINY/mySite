package com.jjbin.mysite.api.domain;

import com.jjbin.mysite.api.request.create.MessageCreate;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member from;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member to;
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;


    @Builder
    public Message(Member from, Member to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
        createdAt = LocalDateTime.now();
        isRead=false;
    }

    //==생성 메서드==//
    public static Message createMessage(Member from, Member to,String content){
        Message message = Message.builder()
                .from(from)
                .to(to)
                .content(content)
                .build();

        return message;
    }

    public boolean isRead(){
        return isRead;
    }
}