package com.github.aico.web.dto.auth.resposne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DuplicateResult {
    private final String message;
    private final boolean check;

    public static DuplicateResult of(String message, boolean check){
        return DuplicateResult.builder()
                .message(message)
                .check(check)
                .build();
    }
}
