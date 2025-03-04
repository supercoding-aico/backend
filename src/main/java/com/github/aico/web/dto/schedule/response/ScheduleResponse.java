package com.github.aico.web.dto.schedule.response;

import com.github.aico.repository.schedule.Schedule;
import com.github.aico.repository.schedule.ScheduleStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ScheduleResponse {
    private final Long scheduleId;
    private final Long teamId;
    private final ScheduleStatus scheduleStatus;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public ScheduleResponse(Schedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.teamId = schedule.getTeam().getTeamId();
        this.scheduleStatus = schedule.getScheduleStatus();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
    }

    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(schedule);
    }

}
