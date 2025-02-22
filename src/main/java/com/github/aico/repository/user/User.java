package com.github.aico.repository.user;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.user_role.UserRole;
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
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserRole> userRoles;
}
