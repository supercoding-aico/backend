package com.github.aico.repository.user_role;

import com.github.aico.repository.role.Role;
import com.github.aico.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="userRoleId")
@Builder
@Entity
@Table(name="user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_role_id")
    private Integer userRoleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public static UserRole of(Role role, User newUser){
        return UserRole.builder()
                .role(role)
                .user(newUser).build();
    }
}
