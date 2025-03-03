package com.github.aico.service.schedule;

import com.github.aico.repository.schedule.Schedule;
import com.github.aico.repository.schedule.ScheduleRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.schedule.request.ScheduleRequest;
import com.github.aico.web.dto.schedule.response.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional(readOnly = true)
    public ResponseDto getSchedules(User user, Long teamId, ScheduleRequest request) {
        List<ScheduleResponse> schedules = scheduleRepository.findByTeamIdAndDateRange(teamId, request.getStartDate(), request.getEndDate())
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
        return new ResponseDto(HttpStatus.OK.value(), "팀 스케줄 조회 성공", schedules);
    }

    @Transactional
    public ResponseDto createSchedule(User user, Long teamId, ScheduleRequest request) {
        List<TeamUser> teamUsers = teamUserRepository.findAllById(request.getUsers());

        Schedule schedule = Schedule.of(request, teamId, teamUsers);
        scheduleRepository.save(schedule);

        return new ResponseDto(HttpStatus.CREATED.value(), "스케줄 등록 성공");
    }


    @Transactional
    public ResponseDto updateSchedule(User user, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다."));

        List<TeamUser> teamUsers = teamUserRepository.findAllById(request.getUsers());

        schedule.update(request, teamUsers);

        return new ResponseDto(HttpStatus.OK.value(), "스케줄 수정 성공");
    }


    @Transactional
    public ResponseDto deleteSchedule(User user, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다."));
        scheduleRepository.delete(schedule);
        return new ResponseDto(HttpStatus.OK.value(), "스케줄 삭제 성공");
    }

    @Transactional(readOnly = true)
    public ResponseDto getMySchedules(User user, Long teamId) {
        List<ScheduleResponse> schedules = scheduleRepository.findByUserAndTeamId(user.getUserId(), teamId)
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
        return new ResponseDto(HttpStatus.OK.value(), "개인 스케줄 조회 성공", schedules);
    }
}
