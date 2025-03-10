package com.github.aico.repository.team_user;

import java.time.LocalDateTime;
import java.util.Map;

public interface CustomTeamUserRepository {
    void updateChatReadAtBulk(Long userId, Map<Long, LocalDateTime> teamIdToLastReadAt);
}
