package com.github.aico.repository.chat;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.team_user.TeamUser;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "chatId")
@Builder
@Entity
@Table(name = "chat")
public class Chat extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_user_id")
    private TeamUser teamUser;
    @Column(name = "content",length = 255)
    private String content;

}
