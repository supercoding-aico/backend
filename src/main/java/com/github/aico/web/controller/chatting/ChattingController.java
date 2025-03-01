package com.github.aico.web.controller.chatting;

import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import com.github.aico.repository.userDetails.CustomUserDetails;
import com.github.aico.service.chatting.ChattingService;
import com.github.aico.web.dto.chat.request.Chatting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChattingController {

    private final ChattingService chattingService;
    @MessageMapping("/room")
    public void sendChatting(@Payload Chatting chatting) {
        chattingService.sendChatting(chatting);

    }
}
