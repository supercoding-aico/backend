package com.github.aico.web.dto.chat.response;

import com.github.aico.repository.chat.Chat;
import com.github.aico.web.dto.auth.resposne.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class ChatResponse {
    private final Long roomId;
    private final Long chatId;
    private final UserInfo userInfo;
    private final String content;
    private final LocalDateTime createdAt;

    public static ChatResponse from(Chat chat){
        return ChatResponse.builder()
                .roomId(chat.getTeamUser().getTeam().getTeamId())
                .chatId(chat.getChatId())
                .userInfo(UserInfo.from(chat.getTeamUser().getUser()))
                .content(chat.getContent())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
