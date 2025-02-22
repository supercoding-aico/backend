package com.github.aico.web.controller.auth;

import com.github.aico.service.auth.AuthService;
import com.github.aico.web.dto.auth.request.EmailDuplicate;
import com.github.aico.web.dto.auth.request.LoginRequest;
import com.github.aico.web.dto.auth.request.NicknameDuplicate;
import com.github.aico.web.dto.auth.request.SignUpRequest;
import com.github.aico.web.dto.base.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/nickname")
    public ResponseDto nickNameDuplicateCheck(@RequestBody NicknameDuplicate nicknameDuplicate){
        return authService.getNickNameDuplicateCheckResult(nicknameDuplicate);

    }
    @PostMapping("/email")
    public ResponseDto emailDuplicateCheck(@RequestBody EmailDuplicate emailDuplicate){
        return authService.getEmailDuplicateCheckResult(emailDuplicate);

    }
    @PostMapping("/sign-up")
    public ResponseDto signUp(@RequestBody SignUpRequest signUpRequest){
        return authService.signUpResult(signUpRequest);
    }
    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        String  token = authService.loginResult(loginRequest);
        if (token != null && !token.isEmpty()) {
            response.setHeader("Authorization", "Bearer " + token);
            return new ResponseDto(HttpStatus.OK.value(), "로그인에 성공하였습니다.");
        } else {
            return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "아이디 또는 비밀번호를 다시 확인해주세요");
        }
    }
}
