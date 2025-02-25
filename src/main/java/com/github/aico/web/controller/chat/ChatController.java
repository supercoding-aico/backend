package com.github.aico.web.controller.chat;

import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import com.github.aico.service.chat.ChatService;
import com.github.aico.web.dto.base.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    @GetMapping("/{teamId}")
    public ResponseDto getTeamChatList(@JwtUser User user,
                                        @PathVariable Long teamId,
                                       @RequestParam(required = false,defaultValue = "0",value = "page") Integer page){
        return chatService.getTeamChatListResult(user,teamId,page);
    }
}
