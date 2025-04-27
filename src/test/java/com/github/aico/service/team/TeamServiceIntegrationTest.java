package com.github.aico.service.team;

import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamRole;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.service.exceptions.BadRequestException;

import com.github.aico.web.dto.base.ResponseDto;

import com.github.aico.web.dto.teamUser.request.LeaveTeamMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false) // 롤백 비활성화
class TeamServiceIntegrationTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamUserRepository teamUserRepository;

    @Test
    void leaveTeamResult_SingleManager_SelfLeaveFails() {
        // Given
        User user = User.builder()
                .nickname("Manager1")
                .email("manager1@example.com")
                .password("pass123")
                .build();
        User savedUser = userRepository.save(user); // 별도 변수로 저장

        Team team = Team.builder()
                .teamName("Test Team")
                .build();
        Team savedTeam = teamRepository.save(team); // 별도 변수로 저장

        TeamUser teamUser = TeamUser.builder()
                .team(savedTeam)
                .user(savedUser)
                .teamRole(TeamRole.MANAGER)
                .build();
        teamUserRepository.save(teamUser);

        LeaveTeamMember leaveTeamMember = new LeaveTeamMember(savedUser.getUserId());

        // When & Then
        assertThrows(BadRequestException.class, () ->
                teamService.leaveTeamResult(savedUser, savedTeam.getTeamId(), leaveTeamMember));
    }

//    @Test
//    void leaveTeamResult_MultipleManagers_SelfLeaveSucceeds() {
//        // Given
//        User user1 = User.builder()
//                .nickname("Manager1")
//                .email("manager1@example.com")
//                .password("pass123")
//                .build();
//        User savedUser1 = userRepository.save(user1); // 별도 변수로 저장
//
//        User user2 = User.builder()
//                .nickname("Manager2")
//                .email("manager2@example.com")
//                .password("pass123")
//                .build();
//        User savedUser2 = userRepository.save(user2); // 별도 변수로 저장
//
//        Team team = Team.builder()
//                .teamName("Test Team")
//                .build();
//        Team savedTeam = teamRepository.save(team); // 별도 변수로 저장
//
//        teamUserRepository.save(TeamUser.builder()
//                .team(savedTeam)
//                .user(savedUser1)
//                .teamRole(TeamRole.MANAGER)
//                .build());
//        teamUserRepository.save(TeamUser.builder()
//                .team(savedTeam)
//                .user(savedUser2)
//                .teamRole(TeamRole.MANAGER)
//                .build());
//
//        LeaveTeamMember leaveTeamMember = new LeaveTeamMember(savedUser1.getUserId());
//
//        // When
//        ResponseDto response = teamService.leaveTeamResult(savedUser1, savedTeam.getTeamId(), leaveTeamMember);
//
//        // Then
//        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
//        assertFalse(teamUserRepository.findByTeamAndUser(savedTeam, savedUser1).isPresent());
//    }

    @Test
    @Transactional
    void leaveTeamResult_ConcurrentManagerLeave() throws InterruptedException {
        // Given: DB에 미리 삽입된 데이터 조회
        Team savedTeam = teamRepository.findById(103L)
                .orElseThrow(() -> new IllegalStateException("Team 103을 찾을 수 없습니다."));
        User savedUser1 = userRepository.findById(216L)
                .orElseThrow(() -> new IllegalStateException("User 216을 찾을 수 없습니다."));
        User savedUser2 = userRepository.findById(217L)
                .orElseThrow(() -> new IllegalStateException("User 217을 찾을 수 없습니다."));

        Long teamId = savedTeam.getTeamId();


        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);

        // When
        Runnable leaveTask1 = () -> {
            try {
                System.out.println("User1이 떠난 팀 Id: " + teamId);
                teamService.leaveTeamResult(savedUser1, teamId,
                        new LeaveTeamMember(savedUser1.getUserId()));
                System.out.println("User1 정상적으로 떠났습니다.");
                successCount.incrementAndGet();
            } catch (BadRequestException e) {
                System.out.println("User1 탈퇴 실패: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        Runnable leaveTask2 = () -> {
            try {
                System.out.println("User2가 떠난 팀 Id: " + teamId);
                teamService.leaveTeamResult(savedUser2, teamId,
                        new LeaveTeamMember(savedUser2.getUserId()));
                System.out.println("User2 탈퇴 성공");
                successCount.incrementAndGet();
            } catch (BadRequestException e) {
                System.out.println("User2 탈퇴 실패: " + e.getMessage());
            } catch (Exception e) {

                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        executor.submit(leaveTask1);
        executor.submit(leaveTask2);

        latch.await();

        // Then
        List<TeamUser> remainingManagers = teamUserRepository.findByTeamAndRoleWithLockDsl(savedTeam, TeamRole.MANAGER);
        System.out.println("남은 매니저 수: " + remainingManagers.size());
        System.out.println("성공 카운트: " + successCount.get());
        assertEquals(1, remainingManagers.size(), "Exactly one manager should remain");
        assertEquals(1, successCount.get(), "Only one manager should leave successfully");
    }
}