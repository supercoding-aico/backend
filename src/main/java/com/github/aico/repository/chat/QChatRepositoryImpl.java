package com.github.aico.repository.chat;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.github.aico.repository.chat.QChat.chat;

@Repository
@RequiredArgsConstructor
public class QChatRepositoryImpl implements QChatRepository{
    private final JPAQueryFactory queryFactory;
    @Override
    public List<TeamLatestChatTimeDto> findLatestChatTimesByTeamIds(List<Long> teamIds) {
        return queryFactory
                .select(Projections.constructor(
                        TeamLatestChatTimeDto.class,
                        chat.teamUser.team.teamId,
                        chat.createdAtMillis.max()
                ))
                .from(chat)
                .where(chat.teamUser.team.teamId.in(teamIds))
                .groupBy(chat.teamUser.team.teamId)
                .fetch();
    }
}
