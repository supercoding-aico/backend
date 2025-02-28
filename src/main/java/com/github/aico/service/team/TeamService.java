package com.github.aico.service.team;

import com.github.aico.config.security.JwtTokenProvider;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamRole;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.service.redis.RedisUtil;
import com.github.aico.service.exceptions.BadRequestException;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.web.dto.auth.request.EmailDuplicate;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.team.request.MakeTeam;
import com.github.aico.web.dto.team.response.TeamsResponse;
import com.github.aico.web.dto.teamUser.request.LeaveTeamMember;
import com.github.aico.web.dto.teamUser.response.TeamMember;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender sender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    private final RedisUtil redisUtil;
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
        TeamUser teamUser = TeamUser.of(successTeam,user,TeamRole.MANAGER);
        teamUserRepository.save(teamUser);

        return new ResponseDto(HttpStatus.CREATED.value(),successTeam.getTeamName() + "팀이 성공적으로 만들어졌습니다.");
    }



    /**
     * 팀 수정(Manger역할을 가진 사람만 수정 가능)
     * */
    @Transactional
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
     * 팀 삭제(Manger역할을 가진 사람만 삭제 가능)
     * */
    @Transactional
    public ResponseDto deleteTeamResult(Long teamId, User user) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()->new NotFoundException(teamId+ "에 해당하는 team이 존재하지 않습니다."));
        TeamRole teamRole = checkTeamRole(team,user);
        if (!teamRole.equals(TeamRole.MANAGER)){
            throw new BadRequestException("해당 유저의 권한은 " + teamRole +"이므로 수정 불가능합니다.("+TeamRole.MANAGER+"부터 변경 가능)");
        }

        teamRepository.deleteTeamById(teamId);
        return new ResponseDto(HttpStatus.NO_CONTENT.value(),"삭제 성공");
    }
    /**
     * 팀 멤버 조회(팀원이 아닐 경우에는 해당 팀의 멤버 조회 불가)
     * */
    public ResponseDto getTeamMemberResult(User user, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()-> new NotFoundException(teamId + "에 해당하는 팀을 찾을 수 없습니다."));
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeam(team);
        boolean isUserInTeam = teamUsers.stream()
                .anyMatch((teamUser) -> teamUser.getUser().equals(user));
        //팀원이 아니면 팀 멤버 조회 불가
        if (!isUserInTeam){
            throw new NotFoundException(user.getNickname() + "은 "+team.getTeamName() +"에 팀원이 아닙니다.");
        }
        List<TeamMember> teamMembers = teamUsers.stream().map(TeamMember::from).toList();
        return new ResponseDto(HttpStatus.OK.value(),"조회 성공", teamMembers);
    }
    /**
     * 팀 탈퇴
     * */
    @Transactional
    public ResponseDto leaveTeamResult(User user, Long teamId, LeaveTeamMember leaveTeamMember) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()-> new NotFoundException(teamId + "에 해당하는 팀을 찾을 수 없습니다."));
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeam(team);
        teamUsers.forEach((tu)->log.info(tu.getUser().getUserId()+" "));
        TeamRole teamRole = checkTeamRole(team,user);
        Long leaveUserId = leaveTeamMember.getUserId();
        //동시성을 위해 Lock 사용
        List<TeamUser> teamManagers = teamUserRepository.findByTeamAndRoleWithLockDsl(team,TeamRole.MANAGER);
        User leaveUser = userRepository.findById(leaveUserId)
                .orElseThrow(()-> new NotFoundException(leaveUserId + "에 해당하는 유저가 존재하지 않습니다."));
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,leaveUser)
                .orElseThrow(()->new NotFoundException("찾으려는 사람은 현재 팀원이 아닙니다."));
        //본인은 본인 탈퇴만 가능 매니저는 다른 팀원(매니저도 포함) 탈퇴 가능/최소 한명의 매니저는 필요
        if (teamRole.equals(TeamRole.MANAGER)){
            //동시성 고려해보기
            handleManagerLeave(team, user, leaveUserId, teamManagers, teamUser);
        }//역할이 Member일 때
        else {
            handleMemberLeave(user, leaveUserId, teamUser);
        }
        return new ResponseDto(HttpStatus.NO_CONTENT.value(), "팀 탈퇴처리되었습니다.");
    }

    @Transactional
    public ResponseDto memberInviteResult(Long teamId, User user, EmailDuplicate inviteEmail) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()-> new NotFoundException(teamId + "에 해당하는 팀이 존재하지 않습니다."));
        //공통 메소드로 빼기
        if (!teamUserRepository.existsByTeamAndUser(team,user)){
            throw new NotFoundException(teamId + "에 해당하는 팀원이 아닙니다.");
        }
        List<TeamUser> teamUsers = teamUserRepository.findByTeamWithLockDsl(team);
        if (teamUsers.size() >=10 ){
            throw new BadRequestException("현재 팀원 의 수 : " + teamUsers.size() + "명이므로 더 이상 초대가 불가능합니다.(팀원 최대 10명)");
        }
        String inviteToken = jwtTokenProvider.createInvitationToken(inviteEmail.getEmail(),team.getTeamId());
        MimeMessage sendMessage = createMessage(inviteEmail.getEmail(),team,inviteToken);
        sender.send(sendMessage);
        redisUtil.setDataExpire(inviteEmail.getEmail(),inviteToken,60*5L);
        return new ResponseDto(HttpStatus.OK.value(),inviteEmail.getEmail()+ "에 초대 링크 발송되었습니다.","token : " + inviteToken);
    }
    public void joinTeamResult(Long teamId, String inviteToken, HttpServletResponse response) {
        String tokenEmail = jwtTokenProvider.getEmail(inviteToken);
        Long tokenTeamId = jwtTokenProvider.getTeamId(inviteToken);
        log.info("tokenEmail : " + tokenEmail);
        log.info("tokenTeamId : " + tokenTeamId);

        try {
            //팀이 유효하지 않을 때
            //이미 팀에 가입된 유저일 때
            //토큰이 유효하지 않을 때
            //회원가입은 되어 있고 팀 가입이 필요할 때
            //회원가입도 안되어 있을 때
            getResponse(teamId, tokenEmail, response,inviteToken);
        }catch (IOException ioe){
            throw new NotFoundException("잘못된 페이지 요청입니다.");
        }
    }
//    @Transactional
//    public ResponseDto leaveTeamResult(User user, Long teamId, LeaveTeamMember leaveTeamMember) {
//        Team team = teamRepository.findById(teamId)
//                .orElseThrow(()-> new NotFoundException(teamId + "에 해당하는 팀을 찾을 수 없습니다."));
//        TeamRole teamRole = checkTeamRole(team,user);
//        Long leaveUserId = leaveTeamMember.getUserId();
//        List<TeamUser> teamUsers = teamUserRepository.findAllByTeam(team);
//        List<TeamUser> teamManagers = teamUsers.stream()
//                .filter((tu)->tu.getTeamRole().equals(TeamRole.MANAGER))
//                .toList();
//        User leaveUser = userRepository.findById(leaveUserId)
//                .orElseThrow(()-> new NotFoundException(leaveUserId + "에 해당하는 유저가 존재하지 않습니다."));
//        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,leaveUser)
//                .orElseThrow(()->new NotFoundException("찾으려는 사람은 현재 팀원이 아닙니다."));
//        //본인은 본인 탈퇴만 가능 매니저는 다른 팀원(매니저도 포함) 탈퇴 가능/최소 한명의 매니저는 필요
//        if (teamRole.equals(TeamRole.MANAGER)){
//            //동시성 고려해보기
//            //매니저 인원이 1명일 때
//            if (teamManagers.size() == 1){
//                //탈퇴하려는 인원이 본인 아이디랑 같을 때
//                if (leaveUserId.equals(user.getUserId())){
//                    throw new BadRequestException("현재 Manager의 수는" + teamManagers.size() +"명 본인 혼자이므로 탈퇴 불가능합니다." );
//                }// 다른 사람을 추방할 때
//                else {
//                    teamUserRepository.delete(teamUser);
//                    return new ResponseDto(HttpStatus.NO_CONTENT.value(),"팀 탈퇴처리되었습니다.");
//                }
//            }//매니저 인원이 1명이 아닐 때
//            else {
//                teamUserRepository.delete(teamUser);
//                return new ResponseDto(HttpStatus.NO_CONTENT.value(),"팀 탈퇴처리되었습니다.");
//            }
//        }//역할이 Member일 때
//        else {
//            //본인만 탈퇴 가능
//            if (leaveUserId.equals(user.getUserId())){
//                teamUserRepository.delete(teamUser);
//                return new ResponseDto(HttpStatus.NO_CONTENT.value(),"팀 탈퇴처리되었습니다.");
//            }else {
//                throw new BadRequestException("해당 유저의 역할은" + teamUser.getTeamRole()+"이므로 다른 팀원은 탈퇴처리가 불가능합니다.");
//            }
//        }
//
//    }


    /**
     * ----------메소드----------
     * */

    //유저에 대한 팀 권한 확인
    public TeamRole checkTeamRole(Team team,User user){
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,user)
                .orElseThrow(()-> new NotFoundException(user.getNickname() + "님은 " + team.getTeamId()+"에 가입되어 있지 않습니다."));
        return teamUser.getTeamRole();
    }
    /**
     * 매니저가 탈퇴할 때
     * */
    private void handleManagerLeave(Team team, User user, Long leaveUserId, List<TeamUser> teamManagers, TeamUser teamUser) {
        if (teamManagers.size() == 1) {
            // 매니저가 1명일 때
            // 본인은 탈퇴 불가
            if (leaveUserId.equals(user.getUserId())) {
                throw new BadRequestException("현재 Manager의 수는 " + teamManagers.size() + "명 본인 혼자이므로 탈퇴 불가능합니다.");
            } else { //다른 유저는 탈퇴 가능
                teamUserRepository.delete(teamUser);
            } //1명 아닐 때는 본인도 탈퇴 가능
        } else {
            teamUserRepository.delete(teamUser);
        }
    }
    /**
     * 일반 멤버가 탈퇴할 때
     * */
    private void handleMemberLeave(User user, Long leaveUserId, TeamUser teamUser) {
        if (leaveUserId.equals(user.getUserId())) {
            teamUserRepository.delete(teamUser);
        } else {
            throw new BadRequestException("해당 유저의 역할은 " + teamUser.getTeamRole() + "이므로 다른 팀원은 탈퇴처리가 불가능합니다.");
        }
    }
    /**
     * 메일에 보낼 메시지 만들기
     * */
    public MimeMessage createMessage(String inviteEmail,Team team,String inviteToken){

        MimeMessage mimeMessage = sender.createMimeMessage();


        String backendUrl = "http://localhost:8080/api/team/join/"+team.getTeamId()+"?token=" + inviteToken; // 초대 수락 URL
        try {
            mimeMessage.setFrom(senderEmail);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO,inviteEmail);
            mimeMessage.setSubject("Ai-Co 프로젝트 팀명 : " + team.getTeamName() + "초대 링크입니다." );


            StringBuilder body = new StringBuilder();
            body.append("<h1>팀 초대</h1>")
                    .append("<h3>팀에 초대되었습니다! 아래 버튼을 눌러 가입하세요.</h3><br>")
                    .append("<table cellspacing='0' cellpadding='0' border='0' style='margin: 10px 0;'>")
                    .append("<tr><td align='center' bgcolor='#007BFF' style='border-radius: 5px;'>")
                    .append("<a href='").append(backendUrl)
                    .append("' style='display: inline-block; font-size: 16px; color: white; background-color: #007BFF; text-decoration: none; padding: 10px 20px; border-radius: 5px;'>")
                    .append("Join Team</a>")
                    .append("</td></tr>")
                    .append("</table>");

            String emailBody = body.toString();
            mimeMessage.setText(emailBody,"UTF-8", "html");
        }catch (MessagingException messageE){
             messageE.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            }
        return mimeMessage;
    }
    /**
     * 초대 토큰 생성
     * */
    public String createInviteToken(String inviteEmail,Long teamId){
        return jwtTokenProvider.createInvitationToken(inviteEmail,teamId);
    }

    private void getResponse(Long teamId, String tokenEmail, HttpServletResponse response,String inviteToken) throws IOException {
        // 팀이 유효하지 않을 때
        Team joinTeam = teamRepository.findById(teamId).orElse(null);
        if (joinTeam == null) {
            response.sendRedirect("http://localhost:3000?code=404");    // http://localhost:3000?code=404
            return;
        }


        User user = userRepository.findByEmailWithRoles(tokenEmail).orElse(null);

        //이미 팀에 가입되어 있을 때
        if (teamUserRepository.existsByTeamAndUser(joinTeam, user)) {
            response.sendRedirect("http://localhost:3000/team/detail/"+teamId); // http://localhost:3000/team/detail/{teamId}
            return;
        }

        // 토큰이 유효하지 않을 때
        if (redisUtil.getData(tokenEmail) == null) {
            response.sendRedirect("http://localhost:3000?code=401");// http://localhost:3000?code=401
            return;
        }

        // 회원가입은 되어 있고 팀이 아닐 때
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(joinTeam, user).orElse(null);
        if (teamUser == null) {
            List<TeamUser> teamUsers = teamUserRepository.findByTeamWithLockDsl(joinTeam);
            if (teamUsers.size() >= 10){
                response.sendRedirect("http://localhost:3000?code=400"); // http://localhost:3000?code=400
                return;
            }
            TeamUser joinTeamUser = TeamUser.of(joinTeam, user,TeamRole.MEMBER);
            teamUserRepository.save(joinTeamUser);
            redisUtil.deleteData(tokenEmail);  // 가입 후 토큰 삭제
            response.sendRedirect("http://localhost:3000/team/detail/"+teamId); // http://localhost:3000/team/detail/{teamId}
            return;
        }
        // 회원가입이 되어 있지 않을 때
        if (user == null) {
            response.sendRedirect("http://localhost:3000/signup?token="+inviteToken); // http://localhost:3000/signup?token={토큰}
            return;
        }
    }



}
