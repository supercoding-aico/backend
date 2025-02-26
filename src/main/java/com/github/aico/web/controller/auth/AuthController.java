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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseDto signUp(@RequestBody SignUpRequest signUpRequest){
        return authService.signUpResult(signUpRequest);
    }
    @PostMapping("/login")
    public ResponseDto login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        String  token = authService.loginResult(loginRequest);

        if (token != null && !token.isEmpty()) {
//            response.setHeader("Authorization", "Bearer " + token);
            Cookie cookie = new Cookie("access_token", token);  // "token"은 쿠키의 이름
            cookie.setHttpOnly(true);  // HttpOnly 속성 설정
            cookie.setSecure(true);  // HTTPS 연결일 때만 쿠키를 보냄 (선택적, 보안을 강화하려면 설정)
            cookie.setPath("/");  // 쿠키가 유효한 경로를 지정 (전체 경로에서 유효하게 설정)
            cookie.setMaxAge(60 * 60 * 24);  // 쿠키의 유효 기간 설정 (여기서는 1일)

            // 응답에 쿠키 추가
            response.addCookie(cookie);
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
    public UserInfo test(@JwtUser User user){
        log.info(user.getEmail());
        User user1 = userRepository.findByEmailWithRoles(user.getEmail()).orElseThrow(()-> new NotFoundException("유저 찾기 불가"));
        return UserInfo.from(user1);
    }
    @PostMapping("/token/refresh")
    public ResponseDto refreshToken(@CookieValue(value = "access_token") String accessToken,HttpServletResponse response){
        return authService.refreshToken(accessToken,response);
    }
}
