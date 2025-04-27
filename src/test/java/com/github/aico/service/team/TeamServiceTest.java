//package com.github.aico.service.team;
//
//import com.github.aico.repository.team.Team;
//import com.github.aico.repository.team_user.TeamRole;
//import com.github.aico.repository.team_user.TeamUser;
//import com.github.aico.repository.team_user.TeamUserRepository;
//import com.github.aico.repository.user.User;
//import com.github.aico.service.exceptions.BadRequestException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TeamServiceTest {
//
//    @Mock
//    private TeamUserRepository teamUserRepository;
//
//    @InjectMocks
//    private TeamService teamService; // leaveTeamResult와 handleManagerLeave가 있는 클래스
//
//    @Test
//    void handleManagerLeave_WhenSingleManager_SelfCannotLeave() {
//        // Given
//        Team team = Team.builder().teamId(1L).teamName("Test Team").build();
//        User user = User.builder().userId(1L).nickname("Manager1").email("manager1@example.com").password("pass").build();
//        Long leaveUserId = 1L; // 본인 ID
//        List<TeamUser> teamManagers = Collections.singletonList(
//                TeamUser.builder().teamUserId(1L).team(team).user(user).teamRole(TeamRole.MANAGER).build()
//        );
//        TeamUser teamUser = TeamUser.builder().teamUserId(1L).team(team).user(user).teamRole(TeamRole.MANAGER).build();
//
//        // When & Then
//        assertThrows(BadRequestException.class, () ->
//                teamService.handleManagerLeave(team, user, leaveUserId, teamManagers, teamUser));
//        verify(teamUserRepository, never()).delete(any()); // 삭제 호출 안 됨
//    }
//
//    @Test
//    void handleManagerLeave_WhenSingleManager_OtherCanLeave() {
//        // Given
//        Team team = Team.builder().teamId(1L).teamName("Test Team").build();
//        User user = User.builder().userId(1L).nickname("Manager1").email("manager1@example.com").password("pass").build();
//        Long leaveUserId = 2L; // 다른 유저 ID
//        User otherUser = User.builder().userId(leaveUserId).nickname("Member1").email("member1@example.com").password("pass").build();
//        List<TeamUser> teamManagers = Collections.singletonList(
//                TeamUser.builder().teamUserId(1L).team(team).user(user).teamRole(TeamRole.MANAGER).build()
//        );
//        TeamUser teamUser = TeamUser.builder().teamUserId(2L).team(team).user(otherUser).teamRole(TeamRole.MEMBER).build();
//
//        // When
//        teamService.handleManagerLeave(team, user, leaveUserId, teamManagers, teamUser);
//
//        // Then
//        verify(teamUserRepository, times(1)).delete(teamUser); // 다른 유저 삭제됨
//    }
//
//    @Test
//    void handleManagerLeave_WhenMultipleManagers_SelfCanLeave() {
//        // Given
//        Team team = Team.builder().teamId(1L).teamName("Test Team").build();
//        User user = User.builder().userId(1L).nickname("Manager1").email("manager1@example.com").password("pass").build();
//        User otherManager = User.builder().userId(2L).nickname("Manager2").email("manager2@example.com").password("pass").build();
//        Long leaveUserId = 1L; // 본인 ID
//        List<TeamUser> teamManagers = List.of(
//                TeamUser.builder().teamUserId(1L).team(team).user(user).teamRole(TeamRole.MANAGER).build(),
//                TeamUser.builder().teamUserId(2L).team(team).user(otherManager).teamRole(TeamRole.MANAGER).build()
//        );
//        TeamUser teamUser = TeamUser.builder().teamUserId(1L).team(team).user(user).teamRole(TeamRole.MANAGER).build();
//
//        // When
//        teamService.handleManagerLeave(team, user, leaveUserId, teamManagers, teamUser);
//
//        // Then
//        verify(teamUserRepository, times(1)).delete(teamUser); // 본인 삭제됨
//    }
//}
