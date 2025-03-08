package com.github.aico.web.dto.meeting.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingCreateRequest {
    @NotNull
    private Long teamId;

    @NotBlank
    private String meeting;
}
