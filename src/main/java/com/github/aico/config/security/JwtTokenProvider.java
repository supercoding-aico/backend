package com.github.aico.config.security;

import com.github.aico.repository.userDetails.CustomUserDetails;
import com.github.aico.service.exceptions.TokenValidateException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret-key-source}")
    private  String secretKeySource;
    private String secretKey;
    @PostConstruct
    public void setUp(){
        secretKey = Base64.getEncoder()
                .encodeToString(secretKeySource.getBytes());
    }
    private final long tokenValidMilisecond = 2000L * 60 * 60; // 2시간

    private final UserDetailsService userDetailsService;
    public String createToken(String email, List<String> roles){
        Claims claims = Jwts.claims()
                .setSubject(email);
        claims.put("roles",roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+tokenValidMilisecond))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // "Bearer " 접두사 제거
        }
        return null;
    }


    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date now = new Date();
            if (claims.getExpiration().before(now)){
                throw new TokenValidateException("해당 토큰 유효 기간이 지났습니다.");
            }
            return claims.getExpiration().after(now);
        }catch (TokenValidateException tve){
            throw new TokenValidateException("토큰이 유효하지 않습니다.");
        }

    }

    public Authentication getAuthentication(String token) {
        String email = getEmail(token);
        UserDetails userDetails =userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }
    public String getEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
