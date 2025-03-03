package com.github.aico.service.auth;

import com.github.aico.config.security.JwtTokenProvider;
import com.github.aico.repository.refresh.RefreshToken;
import com.github.aico.repository.refresh.RefreshTokenRepository;
import com.github.aico.repository.role.Role;
import com.github.aico.repository.role.RoleRepository;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamRole;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.repository.user_role.UserRole;
import com.github.aico.repository.user_role.UserRoleRepository;
import com.github.aico.service.exceptions.NotAcceptException;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.service.exceptions.TokenValidateException;
import com.github.aico.service.redis.RedisUtil;
import com.github.aico.web.dto.auth.request.EmailDuplicate;
import com.github.aico.web.dto.auth.request.LoginRequest;
import com.github.aico.web.dto.auth.request.NicknameDuplicate;
import com.github.aico.web.dto.auth.request.SignUpRequest;
import com.github.aico.web.dto.auth.resposne.DuplicateResult;
import com.github.aico.web.dto.auth.resposne.UserInfo;
import com.github.aico.web.dto.base.ResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisUtil redisUtil;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    /**
     * 닉네임 중복확인
     * */
    public ResponseDto getNickNameDuplicateCheckResult(NicknameDuplicate nicknameDuplicate) {
        String nickname = nicknameDuplicate.getNickname();
        if (userRepository.existsByNickname(nickname)){
            return new ResponseDto(HttpStatus.CONFLICT.value(),"중복확인",DuplicateResult.of(nickname+"은 이미 사용 중입니다.",false) );
        }else {
            return new ResponseDto(HttpStatus.OK.value(),"중복확인",DuplicateResult.of(nickname+"은 사용 가능합니다.",true) );
        }
    }
    /**
     * 이메일 중복확인
     * */
    public ResponseDto getEmailDuplicateCheckResult(EmailDuplicate emailDuplicate) {
        String email = emailDuplicate.getEmail();
        if (userRepository.existsByEmail(email)){
            return new ResponseDto(HttpStatus.CONFLICT.value(),"중복확인",DuplicateResult.of(email+"은 이미 사용 중입니다.",false) );
        }else {
            return new ResponseDto(HttpStatus.OK.value(),"중복확인",DuplicateResult.of(email+"은 사용 가능합니다.",true) );
        }
    }
    /**
     * 회원가입
     * */
    @Transactional
    public ResponseDto signUpResult(SignUpRequest signUpRequest,String token) {

        String email = signUpRequest.getEmail();
        String nickname = signUpRequest.getNickname();
        if (userRepository.existsByEmail(email)){
            return new ResponseDto(HttpStatus.CONFLICT.value(),email+"은 이미 사용 중입니다." );
        }
        if (userRepository.existsByNickname(nickname)){
            return new ResponseDto(HttpStatus.CONFLICT.value(),nickname+"은 이미 사용 중입니다." );
        }
        User user = User.from(signUpRequest);
        user.updatePassword(passwordEncoder.encode(signUpRequest.getPassword()));
        Role role = roleRepository.findById(1)
                .orElseThrow(()->new NotFoundException("USER 역할이 존재하지 않습니다."));
        UserRole userRole = UserRole.of(role,user);
        User saveUser = userRepository.save(user);
        userRoleRepository.save(userRole);
        //팀가입으로 회원가입하는 경우
        if (token !=null){
           joinTeam(token,signUpRequest,saveUser);
        }
        return new ResponseDto(HttpStatus.CREATED.value(),user.getNickname()+"님 Ai-Co 회원가입이 완료되었습니다.");
    }



    /**
     * 로그인
     * */
    public String loginResult(LoginRequest loginRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByEmailWithRoles(loginRequest.getEmail())
                    .orElseThrow(() -> new NotFoundException("User를 찾을 수 없습니다."));
            List<String> roles = user.getUserRoles()
                    .stream().map(UserRole::getRole)
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            String refresh = jwtTokenProvider.createRefreshToken(loginRequest.getEmail());
            if (!refreshTokenRepository.existsByUser(user)){
                RefreshToken refreshToken = RefreshToken.of(user,refresh);
                refreshTokenRepository.save(refreshToken);
            }

            return jwtTokenProvider.createToken(loginRequest.getEmail(), roles);
        }catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptException("로그인 정보가 일치하지 않습니다..");
        }
    }

    public UserInfo getUserInfo(LoginRequest loginRequest) {
        User user = userRepository.findByEmailUserFetchJoin(loginRequest.getEmail())
                .orElseThrow(()->new NotFoundException(loginRequest.getEmail()+"에 해당하는 유저를 찾을 수 없습니다."));
        return UserInfo.from(user);

    }

    public ResponseDto loginValidRequest(User user) {
        return new ResponseDto(HttpStatus.OK.value(),"토큰이 유효합니다.", UserInfo.from(user));
    }

    public ResponseDto refreshToken(String accessToken, HttpServletResponse response) {
        String email = jwtTokenProvider.getEmail(accessToken);
        if (email == null) {
            // 만약 이메일을 추출할 수 없다면, 토큰이 잘못되었거나 만료되었음을 의미
            throw new TokenValidateException("토큰이 잘못되었습니다.");
        }
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(()->new NotFoundException(email+ "에 해당하는 유저가 존재하지 않습니다."));
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(()-> new NotFoundException(user.getNickname() + "님의 refresh 토큰이 존재하지 않습니다."));
        deleteCookie(response);
        if (refreshToken.getExpirationDate().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new TokenValidateException("refreshToken이 만료되었습니다. 다시 로그인 해주세요");
        }
        String newAccessToken = jwtTokenProvider.createRefreshToken(email);
        createCookie(newAccessToken,response);
        return new ResponseDto(HttpStatus.CREATED.value(),"새로운 토큰이 발급되었습니다.");
    }
    private void deleteCookie(HttpServletResponse response){
        Cookie oldCookie = new Cookie("access_token", null);
        oldCookie.setHttpOnly(true);
//        oldCookie.setSecure(true);
        oldCookie.setPath("/");
        oldCookie.setMaxAge(0);
        response.addCookie(oldCookie);
    }
    private void createCookie(String newAccessToken,HttpServletResponse response){
        Cookie newCookie = new Cookie("access_token", newAccessToken);
        newCookie.setHttpOnly(true);
//        newCookie.setSecure(true);
        newCookie.setPath("/");
        newCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(newCookie);
    }
    /**
     * team가입
     * */
    private void joinTeam(String token, SignUpRequest signUpRequest,User saveUser) {
        String tokenEmail = jwtTokenProvider.getEmail(token);
        Long tokenTeamId = jwtTokenProvider.getTeamId(token);

        // Redis에서 이메일로 저장된 데이터가 없거나 이메일이 일치하지 않으면 예외 처리
        if (redisUtil.getData(tokenEmail) == null || !tokenEmail.equals(signUpRequest.getEmail())) {
            throw new NotFoundException("초대 토큰이 유효하지 않거나 초대 받은 이메일과 가입하려는 이메일이 동일하지 않습니다.");
        }

        // 초대 토큰에 대한 데이터 삭제
        redisUtil.deleteData(tokenEmail);

        // 팀 ID로 팀 조회
        Team joinTeam = teamRepository.findById(tokenTeamId)
                .orElseThrow(() -> new NotFoundException("가입하려는 팀이 존재하지 않습니다."));

        // 팀에 이미 10명이 있으면 가입 불가
        List<TeamUser> teamUsers = teamUserRepository.findByTeamWithLockDsl(joinTeam);
        if (teamUsers.size() >= 10) {
            throw new NotFoundException("가입하려는 팀은 이미 10명이라 참여 불가능합니다.");
        }

        // 새로운 팀 유저 추가
        TeamUser teamUser = TeamUser.of(joinTeam, saveUser, TeamRole.MEMBER);
        teamUserRepository.save(teamUser);

    }
}
