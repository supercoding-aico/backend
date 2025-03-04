package com.github.aico.web.controller.schedule;

import com.github.aico.web.dto.schedule.request.ScheduleRequest;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.service.schedule.ScheduleService;
import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/{teamId}")
    public ResponseDto getSchedules(@JwtUser User user, @PathVariable Long teamId, @RequestBody ScheduleRequest request) {
        return scheduleService.getSchedules(user, teamId, request);
    }

    @PostMapping("/{teamId}")
    public ResponseDto createSchedule(@JwtUser User user, @PathVariable Long teamId, @RequestBody ScheduleRequest request) {
        return scheduleService.createSchedule(user, teamId, request);
    }

    @PutMapping("/{scheduleId}")
    public ResponseDto updateSchedule(@JwtUser User user, @PathVariable Long scheduleId, @RequestBody ScheduleRequest request) {
        return scheduleService.updateSchedule(user, scheduleId, request);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseDto deleteSchedule(@JwtUser User user, @PathVariable Long scheduleId) {
        return scheduleService.deleteSchedule(user, scheduleId);
    }

    @GetMapping("/me/{teamId}")
    public ResponseDto getMySchedules(@JwtUser User user, @PathVariable Long teamId) {
        return scheduleService.getMySchedules(user, teamId);
    }
}
