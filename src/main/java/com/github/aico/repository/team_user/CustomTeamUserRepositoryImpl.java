package com.github.aico.repository.team_user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomTeamUserRepositoryImpl implements CustomTeamUserRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void updateChatReadAtBulk(Long userId, Map<Long, LocalDateTime> teamIdToLastReadAt) {
        if (teamIdToLastReadAt.isEmpty()) {
            return;
        }

        // CASE WHEN 동적 생성
        StringBuilder caseClause = new StringBuilder();
        for (Map.Entry<Long, LocalDateTime> entry : teamIdToLastReadAt.entrySet()) {
            caseClause.append(String.format("WHEN %d THEN ? ", entry.getKey()));
        }

        // IN 절 동적 생성
        List<Long> teamIds = new ArrayList<>(teamIdToLastReadAt.keySet());
        String inClause = String.join(",", Collections.nCopies(teamIds.size(), "?"));

        // SQL 쿼리
        String sql = String.format(
                "UPDATE team_user " +
                        "SET chat_read_at = CASE team_id %s ELSE chat_read_at END " +
                        "WHERE user_id = ? AND team_id IN (%s)",
                caseClause.toString(),
                inClause
        );

        // 파라미터 준비
        List<Object> params = new ArrayList<>();
        params.addAll(teamIdToLastReadAt.values()); // WHEN 절의 값들
        params.add(userId);                         // WHERE user_id
        params.addAll(teamIds);                     // WHERE team_id IN

        // 실행
        jdbcTemplate.update(sql, params.toArray());
    }
}
