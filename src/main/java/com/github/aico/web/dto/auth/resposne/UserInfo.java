package com.github.aico.web.dto.auth.resposne;

import com.github.aico.repository.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserInfo {
    private final Long userId;
    private final String nickname;
    private final String email;
    private final String phoneNumber;

    public static UserInfo from(User user){
        return UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
