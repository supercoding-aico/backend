package com.github.aico.repository.chat;

import java.util.List;

public interface QChatRepository {
    List<TeamLatestChatTimeDto> findLatestChatTimesByTeamIds(List<Long> teamIds);
}
