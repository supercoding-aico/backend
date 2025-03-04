package com.github.aico.web.dto.schedule.request;

import com.github.aico.repository.schedule.ScheduleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ScheduleRequest {
    private final Long teamId;
    private final ScheduleStatus scheduleStatus;
    private final String content;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<Long> users;

    @Builder
    public ScheduleRequest(Long teamId, ScheduleStatus scheduleStatus, String content, LocalDate startDate, LocalDate endDate, List<Long> users) {
        this.teamId = teamId;
        this.scheduleStatus = scheduleStatus;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.users = users;
    }
}
