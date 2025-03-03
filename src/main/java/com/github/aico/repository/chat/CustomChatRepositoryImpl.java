package com.github.aico.repository.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class CustomChatRepositoryImpl implements CustomChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllBatch(List<Chat> chats) {
        String sql = "INSERT INTO chat (team_user_id, content,created_at,updated_at,created_at_millis) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Chat chat = chats.get(i);
                        ps.setLong(1, chat.getTeamUser().getTeamUserId()); // team_user_id
                        ps.setString(2, chat.getContent()); // content
                        ps.setTimestamp(5, Timestamp.valueOf(chat.getCreatedAtMillis()));
                        ps.setTimestamp(3, Timestamp.valueOf(chat.getCreatedAt()));
                        ps.setTimestamp(4, Timestamp.valueOf(chat.getUpdatedAt()));
                    }



                    @Override
                    public int getBatchSize() {
                        return chats.size();
                    }
                });

    }
}
