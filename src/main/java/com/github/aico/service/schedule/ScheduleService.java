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
        List<TeamUser> teamUsers = teamUserRepository.findAllById(request.getUsers());
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("TeamId를 찾을 수 없습니다."));
        List<TeamUser> teamUserList = teamUserRepository.findAllByTeam(team);
        //teamId로 받는 게 아닌 team으로 받는 걸로 변경
        Schedule schedule = Schedule.of2(request, team);
        Schedule saveSchedule =scheduleRepository.save(schedule);
        for (TeamUser teamUser : teamUsers){
            ScheduleUser scheduleUser = ScheduleUser.of(saveSchedule,teamUser);
            scheduleUserRepository.save(scheduleUser);
        }

        log.info("유저 아이디: " + user.getUserId());
        TeamUser requesterTeamUser = teamUserRepository.findByTeamTeamIdAndUserId(teamId, user.getUserId())
                .orElseThrow(() -> new NotFoundException("요청자가 팀에 속해 있지 않습니다."));

        Set<TeamUser> allRecipients = new HashSet<>(teamUserList);
        allRecipients.add(requesterTeamUser);

        allRecipients.forEach(teamUser -> {
            Notifications notification = Notifications.builder()
                    .teamUser(teamUser)
                    .schedule(schedule)
                    .content("새로운 스케줄이 등록되었습니다: " + request.getContent())
                    .isRead(false)
                    .build();
            notificationsRepository.save(notification);

            log.info(teamUser.getUser().getUserId()+"");

            messagingTemplate.convertAndSend(
                    "/topic/notification/" + teamUser.getUser().getUserId(),
                    new NotificationResponse(notification.getNotificationsId(), "스케줄 등록", request.getContent())
            );
        });

        return new ResponseDto(HttpStatus.CREATED.value(), "스케줄 등록 성공");
    }

    @Transactional
    public ResponseDto updateSchedule(User user, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다."));
        List<TeamUser> teamUsers = teamUserRepository.findAllById(request.getUsers());
        schedule.update(request, teamUsers);
        scheduleRepository.save(schedule); // 명시적 저장 추가 (필요 시)

        TeamUser requesterTeamUser = teamUserRepository.findByTeamTeamIdAndUserId(schedule.getTeam().getTeamId(), user.getUserId())
                .orElseThrow(() -> new NotFoundException("요청자가 팀에 속해 있지 않습니다."));

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

            messagingTemplate.convertAndSend(
                    "/topic/notification/" + teamUser.getUser().getUserId(),
                    new NotificationResponse(notification.getNotificationsId(), "스케줄 수정", request.getContent())
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