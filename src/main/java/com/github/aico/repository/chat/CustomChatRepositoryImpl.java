package com.github.aico.repository.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomChatRepositoryImpl implements CustomChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllBatch(List<Chat> chats) {
        String sql = "INSERT INTO chat (team_user_id, content,created_at,updated_at,created_at_millis) " +
                "VALUES (?, ?, ?, ?, ?)";
        log.info("배치 삽입 시작, 삽입할 데이터 크기: {}", chats.size());
        int[] updateCounts =jdbcTemplate.batchUpdate(sql,
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
        log.info("배치 삽입 완료, 처리된 행 수: {}", updateCounts.length);
        //  각 행별 성공 여부 확인
        for (int i = 0; i < updateCounts.length; i++) {
            if (updateCounts[i] == 0) {
                log.warn("행 {} 삽입 실패", i);
            }
        }

    }
}
