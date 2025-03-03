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
    private final String imageUrl;

    // 기존 of() 메서드 (imageUrl 포함)
    public static UserInfo of(User user, String imageUrl) {
        return UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .imageUrl(imageUrl)
                .build();
    }

    // 새로운 of() 메서드 (imageUrl 없이 사용 가능)
    public static UserInfo from(User user) {
        return UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .imageUrl(null)  // imageUrl 없이 사용할 경우 null로 설정
                .build();
    }
}