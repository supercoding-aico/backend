package com.github.aico.web.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto {
    private int code;
    private String message;
    private Object data;

    public ResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }
}