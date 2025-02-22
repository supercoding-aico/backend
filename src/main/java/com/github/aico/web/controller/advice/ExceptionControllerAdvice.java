package com.github.aico.web.controller.advice;

import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.web.dto.base.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
}
