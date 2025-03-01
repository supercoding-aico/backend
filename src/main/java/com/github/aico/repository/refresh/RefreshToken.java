package com.github.aico.repository.refresh;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "refreshTokenId")
@Builder
@Entity
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long refreshTokenId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "token")
    private String token;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    public static RefreshToken of(User user,String token){
        return RefreshToken.builder()
                .user(user)
                .token(token)
                .expirationDate(LocalDateTime.now().plusDays(7))
                .build();
    }
}
