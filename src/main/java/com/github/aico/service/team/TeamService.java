package com.github.aico.service.team;

import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.team.request.MakeTeam;
import com.github.aico.web.dto.team.response.TeamsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    public ResponseDto getMyTeamListResult(User user,Integer page) {
        Pageable pageable = PageRequest.of(page,10);
        Page<TeamUser> myTeamUser = teamUserRepository.findAllByUser(user,pageable);
        Page<Team>  myTeam = myTeamUser.map(TeamUser::getTeam);
        Page<TeamsResponse> myTeamResponse = myTeam.map(TeamsResponse::from);
        return new ResponseDto(HttpStatus.OK.value(),user.getNickname()+"님의 team 조회 성공",myTeamResponse);
    }

    public ResponseDto makeTeamResult(MakeTeam makeTeam, User user) {
        Team team = Team.from(makeTeam);
        Team successTeam = teamRepository.save(team);
        TeamUser teamUser = TeamUser.of(successTeam,user);
        teamUserRepository.save(teamUser);

        return new ResponseDto(HttpStatus.CREATED.value(),successTeam.getTeamName() + "팀이 성공적으로 만들어졌습니다.");
    }
}
