package com.github.aico.config.security;

import com.github.aico.repository.userDetails.CustomUserDetails;
import com.github.aico.service.exceptions.TokenValidateException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
//    private final long tokenValidMilisecond = 2000L * 60 * 60; // 2시간
    private final long tokenValidMilisecond = 1000L * 60; // 1분
    private  final long refreshTokenValidMilisecond = 1000L * 60L * 60L * 24L * 7L;

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
    public String createRefreshToken(String email){
        Claims claims = Jwts.claims()
                .setSubject(email);

        Date now = new Date();
        // 리프레시 토큰의 만료 시간을 7일로 설정
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMilisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String createInvitationToken(String email, Long teamId) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("invitation", true);  // 초대 토큰임을 명시
        claims.put("teamId", teamId);    // 초대받은 팀 ID

        Date now = new Date();
        long invitationTokenValidity = 24 * 60 * 60 * 1000L; // 24시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + invitationTokenValidity))
                .signWith(SignatureAlgorithm.HS256, secretKey)
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
            return claims.getExpiration().after(now);
        }catch (ExpiredJwtException eje){
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
    public Long getTeamId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        // teamId를 claims에서 추출
        return claims.get("teamId", Long.class);
    }
}
