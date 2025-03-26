package com.github.aico.web.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class Toggle {
    private final Boolean newMessage;

    public static Toggle status(){
        return Toggle.builder()
                .newMessage(true)
                .build();
    }
}
