package com.github.aico.repository.team;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.web.dto.team.request.MakeTeam;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "teamId")
@Builder
@Entity
@Table(name = "team")
public class Team extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id",nullable = false)
    private Long teamId;
    @Column(name = "team_name", nullable = false)
    private String teamName;

    public static Team from(MakeTeam makeTeam){
        return Team.builder()
                .teamName(makeTeam.getName())
                .build();
    }
    public void updateTeam(MakeTeam makeTeam){
        this.teamName = makeTeam.getName();
    }

    public Team(Long teamId) {
        this.teamId = teamId;
    }
}
