package com.github.aico.web.controller.team;

import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import com.github.aico.service.team.TeamService;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.team.request.MakeTeam;
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
}
