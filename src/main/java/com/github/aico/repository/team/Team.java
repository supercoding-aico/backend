package com.github.aico.repository.team;

import com.github.aico.repository.base.BaseEntity;
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
}
