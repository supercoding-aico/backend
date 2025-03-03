package com.github.aico.repository.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profile_image")
public class UserProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    public static UserProfileImage createDefault(User user) {
        return UserProfileImage.builder()
                .user(user)
                .imageUrl("https://aicoproject.s3.ap-northeast-2.amazonaws.com/default-profile.PNG") // 기본 이미지 설정
                .build();
    }

    public void updateImage(String newImageUrl) {
        this.imageUrl = newImageUrl;
    }
}
