package com.github.aico.service.exceptions;

public class TokenValidateException extends RuntimeException{
    public TokenValidateException(String message) {
        super(message);
    }
}
