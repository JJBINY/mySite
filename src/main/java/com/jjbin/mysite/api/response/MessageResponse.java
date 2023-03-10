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
    private String toLoginId;
    private String fromLoginId;
    private LocalDateTime createdAt;
    public MessageResponse(Message message) {
        this.id = message.getId();
        this.fromLoginId = message.getFrom().getLoginId();
        this.toLoginId =message.getTo().getLoginId();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }

    @Builder
    public MessageResponse(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}