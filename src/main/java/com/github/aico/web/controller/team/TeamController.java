package com.github.aico.web.controller.team;

import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import com.github.aico.service.team.TeamService;
import com.github.aico.web.dto.auth.request.EmailDuplicate;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.team.request.MakeTeam;
import com.github.aico.web.dto.teamUser.request.LeaveTeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final TeamService teamService;

    @GetMapping("/all")
    public ResponseDto getMyTeamList(@RequestParam(defaultValue = "0",required = false,value = "page") Integer page,
                                     @JwtUser User user){
        return teamService.getMyTeamListResult(user,page);
    }
    @PostMapping
    public ResponseDto makeTeam(@RequestBody MakeTeam makeTeam,@JwtUser User user){
        return teamService.makeTeamResult(makeTeam,user);
    }
    @PutMapping("/{teamId}")
    public ResponseDto updateTeam(@PathVariable Long teamId,@RequestBody MakeTeam makeTeam,@JwtUser User user){
        return teamService.updateTeamResult(makeTeam,user,teamId);
    }
    @DeleteMapping("/{teamId}")
    public ResponseDto deleteTeam(@PathVariable Long teamId,@JwtUser User user){
        return teamService.deleteTeamResult(teamId,user);
    }
    @GetMapping("/{teamId}/member")
    public ResponseDto getTeamMember(@JwtUser User user,@PathVariable Long teamId){
        return teamService.getTeamMemberResult(user,teamId);
    }
    @DeleteMapping("/leave/{teamId}")
    public ResponseDto leaveTeam(@JwtUser User user, @PathVariable Long teamId, @RequestBody LeaveTeamMember leaveTeamMember){
        return teamService.leaveTeamResult(user,teamId,leaveTeamMember);
    }

    @PostMapping("/{teamId}/invite")
    public ResponseDto memberInvite(@PathVariable Long teamId,@JwtUser User user, @RequestBody EmailDuplicate inviteEmail){
        return teamService.memberInviteResult(teamId,user,inviteEmail);

    }
    @GetMapping("/{teamId}")
    public String memberInvite(@PathVariable Long teamId, @RequestParam("token")String inviteToken){
        return inviteToken;

    }
}
