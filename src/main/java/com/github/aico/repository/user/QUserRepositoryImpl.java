package com.github.aico.repository.user;

import com.github.aico.repository.role.QRole;
import com.github.aico.repository.user_role.QUserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QUserRepositoryImpl implements QUserRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findByEmailWithRoles(String email) {
        QUser user = QUser.user;
        QUserRole userRole = QUserRole.userRole;
        QRole role = QRole.role;

        User foundUserByEmail = jpaQueryFactory.selectFrom(user)
                .join(user.userRoles,userRole).fetchJoin()
                .join(userRole.role,role).fetchJoin()
                .where(user.email.eq(email))
                .fetchOne();
        return Optional.ofNullable(foundUserByEmail);
    }
}
