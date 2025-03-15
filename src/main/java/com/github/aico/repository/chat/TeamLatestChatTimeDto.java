package com.github.aico.repository.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class TeamLatestChatTimeDto {
    private final Long teamId;
    private final LocalDateTime latestTime;
}
