package com.github.aico.service.team;

import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamRole;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.service.exceptions.BadRequestException;
import com.github.aico.service.exceptions.NotFoundException;
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
    /**
     * 내 팀 리스트 조회
     * */
    public ResponseDto getMyTeamListResult(User user,Integer page) {
        Pageable pageable = PageRequest.of(page,10);
        log.info("N+1테스트 시작");
        // N+1 문제가 발생하여 @EntityGraph 사용
        // TeamUser 조회할 때마다 Team은 항상 필요하므로 @EntityGraph 를 통해 TeamUser 조회 시 Team 함께 가져온다.
        Page<TeamUser> myTeamUser = teamUserRepository.findAllByUser(user,pageable);
        log.info("N+1테스트 끝");
        Page<Team>  myTeam = myTeamUser.map(TeamUser::getTeam);

        Page<TeamsResponse> myTeamResponse = myTeam.map(TeamsResponse::from);
        return new ResponseDto(HttpStatus.OK.value(),user.getNickname()+"님의 team 조회 성공",myTeamResponse);
    }
    /**
     * 팀 만들기
     * */
    public ResponseDto makeTeamResult(MakeTeam makeTeam, User user) {
        Team team = Team.from(makeTeam);
        Team successTeam = teamRepository.save(team);
        TeamUser teamUser = TeamUser.of(successTeam,user);
        teamUserRepository.save(teamUser);

        return new ResponseDto(HttpStatus.CREATED.value(),successTeam.getTeamName() + "팀이 성공적으로 만들어졌습니다.");
    }



    /**
     * 팀 수정(Manger역할을 가진 사람만 수정 가능)
     * */
    public ResponseDto updateTeamResult(MakeTeam makeTeam, User user, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()->new NotFoundException(teamId+ "에 해당하는 team이 존재하지 않습니다."));
        TeamRole teamRole = checkTeamRole(team,user);
        if (!teamRole.equals(TeamRole.MANAGER)){
            throw new BadRequestException("해당 유저의 권한은 " + teamRole +"이므로 수정 불가능합니다.("+TeamRole.MANAGER+"부터 변경 가능)");
        }
        team.updateTeam(makeTeam);

        return new ResponseDto(HttpStatus.NO_CONTENT.value(),"업데이트 성공");
    }
    /**
     * 팀 삭제
     * */



    /**
     * ----------메소드----------
     * */

    //유저에 대한 팀 권한 확인
    public TeamRole checkTeamRole(Team team,User user){
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,user)
                .orElseThrow(()-> new NotFoundException(user.getNickname() + "님은 " + team.getTeamId()+"에 가입되어 있지 않습니다."));
        return teamUser.getTeamRole();
    }
}
