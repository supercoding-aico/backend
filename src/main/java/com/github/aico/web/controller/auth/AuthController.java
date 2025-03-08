package com.github.aico.web.controller.auth;

import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.repository.userDetails.CustomUserDetails;
import com.github.aico.service.auth.AuthService;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.web.dto.auth.request.EmailDuplicate;
import com.github.aico.web.dto.auth.request.LoginRequest;
import com.github.aico.web.dto.auth.request.NicknameDuplicate;
import com.github.aico.web.dto.auth.request.SignUpRequest;
import com.github.aico.web.dto.auth.resposne.UserInfo;
import com.github.aico.web.dto.base.ResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/nickname")
    public ResponseDto nickNameDuplicateCheck(@RequestBody NicknameDuplicate nicknameDuplicate){
        return authService.getNickNameDuplicateCheckResult(nicknameDuplicate);
    }
    @PostMapping("/email")
    public ResponseDto emailDuplicateCheck(@RequestBody EmailDuplicate emailDuplicate){
        return authService.getEmailDuplicateCheckResult(emailDuplicate);

    }
    @PostMapping("/sign-up")
    public ResponseDto signUp(@RequestParam(required = false,value = "token")String token,@Valid @RequestBody SignUpRequest signUpRequest){
        return authService.signUpResult(signUpRequest,token);
    }
    @PostMapping("/logout")
    public ResponseDto logout(@JwtUser User user, HttpServletResponse response){
        authService.logoutResult(user,response);

        return new ResponseDto(HttpStatus.OK.value(), "로그아웃 성공");
    }
    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        String  token = authService.loginResult(loginRequest);
        if (token != null && !token.isEmpty()) {
            authService.createCookie(token,response);
            return new ResponseDto(HttpStatus.OK.value(), "로그인에 성공하였습니다.",authService.getUserInfo(loginRequest));
        } else {
            return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "아이디 또는 비밀번호를 다시 확인해주세요");
        }
    }
    @GetMapping("/login-valid")
    public ResponseDto loginValid(@JwtUser User user){
        return authService.loginValidRequest(user);
    }
    @GetMapping("/test")
    public UserInfo test(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user1 = userRepository.findByEmailWithRoles(customUserDetails.getUsername()).orElseThrow(()-> new NotFoundException("유저 찾기 불가"));
        return UserInfo.from(user1);
    }
    @PostMapping("/token/refresh")
    public ResponseDto refreshToken(@CookieValue(value = "Authorization") String accessToken,HttpServletResponse response){
        return authService.refreshToken(accessToken,response);
    }
}
