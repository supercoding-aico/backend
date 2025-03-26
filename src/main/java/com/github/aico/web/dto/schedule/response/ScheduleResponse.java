package com.github.aico.web.dto.schedule.response;

import com.github.aico.repository.schedule.Schedule;
import com.github.aico.repository.schedule.ScheduleStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ScheduleResponse {
    private final Long scheduleId;
    private final String content;
    private final ScheduleStatus scheduleStatus;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<UserInfo> users;

    public ScheduleResponse(Schedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.content = schedule.getContent();
        this.scheduleStatus = schedule.getScheduleStatus();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
        this.users = schedule.getScheduleUsers().stream()
                .map(scheduleUser -> new UserInfo(scheduleUser.getTeamUser().getUser().getUserId(),scheduleUser.getTeamUser().getUser().getNickname()))
                .collect(Collectors.toList());
    }

    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(schedule);
    }

    @Getter
    public static class UserInfo {
        private final Long userId;
        private final String nickName;

        public UserInfo(Long userId,String nickName) {
            this.userId = userId;
            this.nickName = nickName;
        }
    }
}