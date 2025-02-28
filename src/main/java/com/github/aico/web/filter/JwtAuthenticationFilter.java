package com.github.aico.web.filter;

import com.github.aico.config.security.CustomErrorSend;
import com.github.aico.config.security.JwtTokenProvider;
import com.github.aico.service.exceptions.TokenValidateException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final HandlerMappingIntrospector introspector;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        RequestMatcher permitAllMatcher = new OrRequestMatcher(
                new MvcRequestMatcher(introspector, "/api/auth/**"),
                new MvcRequestMatcher(introspector, "/api/team/join/**") // /api/team/** 도 허용
        );
        if (permitAllMatcher.matches(request)) {

            filterChain.doFilter(request, response);
            return;
        }
        String token = jwtTokenProvider.resolveToken(request);
        try{
            if (token != null && jwtTokenProvider.validateToken(token)){
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }catch (TokenValidateException e){
//            throw new TokenValidateException("토큰이 유효하지 않습니다.");
            e.printStackTrace();
            CustomErrorSend.handleException(response, e.getMessage());
            return;
//            e.printStackTrace();
        }


        filterChain.doFilter(request,response);

    }
}
