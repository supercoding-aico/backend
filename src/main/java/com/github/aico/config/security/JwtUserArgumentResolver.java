package com.github.aico.config.security;


import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.repository.userDetails.CustomUserDetails;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.service.exceptions.TokenValidateException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class JwtUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class) &&
                parameter.hasParameterAnnotation(JwtUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = jwtTokenProvider.resolveToken(request); // 요청에서 토큰 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new TokenValidateException("유효하지 않은 토큰입니다.");
        }
        if (authentication == null || !(authentication instanceof UsernamePasswordAuthenticationToken)) {
            throw new NotFoundException("인증 정보가 없습니다.");
        }

        // 인증 객체에서 principal 가져오기
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new NotFoundException("잘못된 인증 정보입니다.");
        }

        // UserDetails로 변환
        CustomUserDetails userDetails = (CustomUserDetails) principal;

        // 데이터베이스에서 유저 조회
        return userRepository.findByEmailUserFetchJoin(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
    }
}
