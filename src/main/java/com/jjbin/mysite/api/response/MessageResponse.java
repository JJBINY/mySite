package com.jjbin.mysite.api.response;

import com.jjbin.mysite.api.domain.Message;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MessageResponse {

    private Long id;
    private String destination;
    private String title;
    private String content;

    private LocalDateTime createdAt;
    public MessageResponse(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }

    @Builder
    public MessageResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}