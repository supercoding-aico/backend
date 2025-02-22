package com.github.aico.web.controller.advice;

import com.github.aico.service.exceptions.CAuthenticationEntryPointException;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.service.exceptions.TokenValidateException;
import com.github.aico.web.dto.base.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseDto handleNotFoundException(NotFoundException nfe){
        log.error("클라이언트 요청 이후 DB검색 중 발생한 에러입니다. " + nfe.getMessage());
        return new ResponseDto(HttpStatus.NOT_FOUND.value(),nfe.getMessage());
    }
    @ExceptionHandler(TokenValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto handleTokenValidateException(TokenValidateException tve){
        log.error("클라이언트 요청 중 문제 발생 " + tve.getMessage());
        return new ResponseDto(HttpStatus.BAD_REQUEST.value(),tve.getMessage());
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseDto handleAccessDeniedException(AccessDeniedException ade){
        log.error("Client 요청에 문제가 있어 다음처럼 출력합니다. " + ade.getMessage());
        return new ResponseDto(HttpStatus.FORBIDDEN.value(),ade.getMessage());
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(CAuthenticationEntryPointException.class)
    public ResponseDto handleAuthenticationException(CAuthenticationEntryPointException ae){
        log.error("Client 요청에 문제가 있어 다음처럼 출력합니다. " + ae.getMessage());
        return new ResponseDto(HttpStatus.UNAUTHORIZED.value(),ae.getMessage());
    }
}
