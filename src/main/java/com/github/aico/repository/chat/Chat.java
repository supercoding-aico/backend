package com.github.aico.repository.chat;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.web.dto.chat.request.Chatting;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "chatId")
@Builder
@Entity
@Table(name = "chat")
public class Chat {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_user_id")
    private TeamUser teamUser;
    @Column(name = "content",length = 255)
    private String content;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "created_at_millis")
    private LocalDateTime createdAtMillis;

    public static Chat of(Chatting chatting,TeamUser teamUser){
        return Chat.builder()
                .teamUser(teamUser)
                .content(chatting.getContent())
                .createdAtMillis(chatting.getCreatedAt())
                .createdAt(chatting.getCreatedAt())
                .updatedAt(chatting.getCreatedAt())
                .build();
    }

}
