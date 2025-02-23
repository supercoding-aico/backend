package com.github.aico.web.controller.team;

import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import com.github.aico.service.team.TeamService;
import com.github.aico.web.dto.base.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
