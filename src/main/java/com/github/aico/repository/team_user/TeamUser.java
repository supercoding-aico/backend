package com.github.aico.repository.team_user;

import com.github.aico.repository.team.Team;
import com.github.aico.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "teamUserId")
@Builder
@Entity
@Table(name = "team_user")
public class TeamUser {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_user_id")
    private Long teamUserId;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "team_role",nullable = false)
    private TeamRole teamRole;

    public static TeamUser of(Team team, User user,TeamRole teamRole){
        return TeamUser.builder()
                .team(team)
                .user(user)
                .teamRole(teamRole)
                .build();
    }
}
