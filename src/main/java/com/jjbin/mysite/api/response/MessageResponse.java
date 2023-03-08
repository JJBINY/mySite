package com.jjbin.mysite.api.response;

import com.jjbin.mysite.api.domain.Message;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MessageResponse {

    private Long id;
    private String content;
    private String toName;
    private String fromName;
    private LocalDateTime createdAt;
    public MessageResponse(Message message) {
        this.id = message.getId();
        this.fromName = message.getFrom().getName();
        this.toName=message.getTo().getName();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }

    @Builder
    public MessageResponse(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}