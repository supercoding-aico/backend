package com.github.aico.service.auth;

import com.github.aico.config.security.JwtTokenProvider;
import com.github.aico.repository.role.Role;
import com.github.aico.repository.role.RoleRepository;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.repository.user_role.UserRole;
import com.github.aico.repository.user_role.UserRoleRepository;
import com.github.aico.service.exceptions.NotAcceptException;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.web.dto.auth.request.EmailDuplicate;
import com.github.aico.web.dto.auth.request.LoginRequest;
import com.github.aico.web.dto.auth.request.NicknameDuplicate;
import com.github.aico.web.dto.auth.request.SignUpRequest;
import com.github.aico.web.dto.auth.resposne.DuplicateResult;
import com.github.aico.web.dto.auth.resposne.UserInfo;
import com.github.aico.web.dto.base.ResponseDto;
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
    public ResponseDto signUpResult(SignUpRequest signUpRequest) {
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
        userRepository.save(user);
        userRoleRepository.save(userRole);
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
            User user = userRepository.findByEmailUserFetchJoin(loginRequest.getEmail())
                    .orElseThrow(() -> new NotFoundException("User를 찾을 수 없습니다."));
            List<String> roles = user.getUserRoles()
                    .stream().map(UserRole::getRole)
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());

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

    }
}
