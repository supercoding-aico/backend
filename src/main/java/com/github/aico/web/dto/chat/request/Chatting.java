package com.github.aico.web.dto.chat.request;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Chatting {
    private Long teamId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    public void saveCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }




}
