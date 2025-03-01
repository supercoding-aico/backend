package com.github.aico.config.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aico.web.dto.base.ResponseDto;

import ch.qos.logback.core.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aico.web.dto.base.ResponseDto;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class CustomErrorSend {

    public static void handleException(HttpServletResponse response, String errorMsg) throws IOException {
        ResponseDto responseDto = new ResponseDto(HttpStatus.UNAUTHORIZED.value(),errorMsg);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));

    }
}
