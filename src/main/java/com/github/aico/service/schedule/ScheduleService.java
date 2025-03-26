package com.github.aico.service.schedule;

import com.github.aico.repository.notifications.Notifications;
import com.github.aico.repository.notifications.NotificationsRepository;
import com.github.aico.repository.schedule.Schedule;
import com.github.aico.repository.schedule.ScheduleRepository;
import com.github.aico.repository.schedule_user.ScheduleUser;
import com.github.aico.repository.schedule_user.ScheduleUserRepository;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.notification.NotificationResponse;
import com.github.aico.web.dto.schedule.request.ScheduleRequest;
import com.github.aico.web.dto.schedule.response.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TeamUserRepository teamUserRepository;
    private final NotificationsRepository notificationsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final TeamRepository teamRepository;
    private final ScheduleUserRepository scheduleUserRepository;

    @Transactional(readOnly = true)
    public ResponseDto getSchedules(User user, Long teamId, LocalDate startDate, LocalDate endDate) {
        List<ScheduleResponse> schedules = scheduleRepository.findByTeamIdAndDateRange(teamId, startDate, endDate)
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
        return new ResponseDto(HttpStatus.OK.value(), "팀 스케줄 조회 성공", schedules);
    }

    @Transactional
    public ResponseDto createSchedule(User user, Long teamId, ScheduleRequest request) {
        log.info("Request Users: {}", request.getUsers());
        List<TeamUser> teamUsers = teamUserRepository.findByTeamTeamIdAndUserUserIdIn(teamId, request.getUsers());
        log.info("Found TeamUsers: {}", teamUsers.stream().map(tu -> tu.getUser().getUserId()).collect(Collectors.toList()));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("TeamId를 찾을 수 없습니다."));
        Schedule schedule = Schedule.of2(request, team, teamUsers);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Saved Schedule Users: {}", savedSchedule.getScheduleUsers().stream().map(su -> su.getTeamUser().getUser().getUserId()).collect(Collectors.toList()));

        // 알림 로직 수정
        List<TeamUser> teamUserList = teamUserRepository.findAllByTeam(team);
        TeamUser requesterTeamUser = teamUserRepository.findByTeamTeamIdAndUserId(teamId, user.getUserId())
                .orElseThrow(() -> new NotFoundException("요청자가 팀에 속해 있지 않습니다."));
        Set<TeamUser> allRecipients = new HashSet<>(teamUserList);
        allRecipients.add(requesterTeamUser);

        allRecipients.forEach(teamUser -> {
            Notifications notification = Notifications.builder()
                    .teamUser(teamUser)
                    .schedule(savedSchedule)
                    .content(request.getContent())
                    .isRead(false)
                    .build();
            notificationsRepository.save(notification);

            // NotificationResponse에 scheduleId, teamId, registeredBy 추가
            NotificationResponse notificationResponse = new NotificationResponse(
                    notification.getNotificationsId(),
                    "스케줄 등록",
                    request.getContent(),
                    savedSchedule.getScheduleId(), // Schedule에서 가져옴
                    team.getTeamId(),              // Team에서 가져옴
                    user.getNickname()             // 등록자 정보 (User에서 가져옴)
            );
            messagingTemplate.convertAndSend(
                    "/topic/notification/" + teamUser.getUser().getUserId(),
                    notificationResponse
            );
        });

        return new ResponseDto(HttpStatus.CREATED.value(), "스케줄 등록 성공");
    }

    @Transactional
    public ResponseDto updateSchedule(User user, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다."));

        // 수정: 팀 소속 유저만 조회
        List<TeamUser> teamUsers = teamUserRepository.findByTeamTeamIdAndUserUserIdIn(
                schedule.getTeam().getTeamId(),
                request.getUsers()
        );
        if (teamUsers.isEmpty()) {
            throw new NotFoundException("요청된 사용자가 팀에 속해 있지 않습니다.");
        }

        schedule.update(request, teamUsers);
        scheduleRepository.save(schedule);

        TeamUser requesterTeamUser = teamUserRepository.findByTeamTeamIdAndUserId(
                schedule.getTeam().getTeamId(),
                user.getUserId()
        ).orElseThrow(() -> new NotFoundException("요청자가 팀에 속해 있지 않습니다."));

        Set<TeamUser> allRecipients = new HashSet<>(teamUsers);
        allRecipients.add(requesterTeamUser);

        allRecipients.forEach(teamUser -> {
            Notifications notification = Notifications.builder()
                    .teamUser(teamUser)
                    .schedule(schedule)
                    .content("스케줄이 수정되었습니다: " + request.getContent())
                    .isRead(false)
                    .build();
            notificationsRepository.save(notification);

            NotificationResponse notificationResponse = new NotificationResponse(
                    notification.getNotificationsId(),
                    "스케줄 수정",
                    request.getContent(),
                    schedule.getScheduleId(),
                    schedule.getTeam().getTeamId(),
                    user.getNickname()
            );
            messagingTemplate.convertAndSend(
                    "/topic/notification/" + teamUser.getUser().getUserId(),
                    notificationResponse
            );
        });

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