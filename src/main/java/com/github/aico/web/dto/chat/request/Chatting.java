package com.github.aico.web.dto.chat.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class Chatting {
    private final Long teamId;
    private final Long userId;
    private final String content;
    private final LocalDateTime createdAt =LocalDateTime.now();


}
