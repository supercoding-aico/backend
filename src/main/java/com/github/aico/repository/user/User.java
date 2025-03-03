package com.github.aico.repository.user;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.user_role.UserRole;
import com.github.aico.service.exceptions.BadRequestException;
import com.github.aico.web.dto.auth.request.SignUpRequest;
import com.github.aico.web.dto.user.request.ProfileUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "userId")
@Builder
@Entity
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;
    @Column(name = "email", length = 100, nullable = false)
    private String email;
    @Column(name = "password", length = 100, nullable = false)
    private String password;
    @Column(name = "phone_number", length = 11, nullable = true)
    private String phoneNumber;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserRole> userRoles;

    public static User from(SignUpRequest signUpRequest) {
        return User.builder()
                .nickname(signUpRequest.getNickname())
                .email(signUpRequest.getEmail())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .build();
    }

    public void updatePassword(String password){
        if (password == null || password.equals("")) {
            throw new BadRequestException("비밀번호를 입력해주세요");
        }
        this.password = password;
    }

    public void updateProfile(ProfileUpdateRequest request) {
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            this.nickname = request.getNickname();
        }
    }


}
