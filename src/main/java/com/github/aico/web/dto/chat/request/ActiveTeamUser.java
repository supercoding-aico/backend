package com.github.aico.web.dto.chat.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ActiveTeamUser {
    private Long teamId;
    private Long userId;

}
