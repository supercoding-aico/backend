package com.github.aico.repository.team_user;

import com.github.aico.repository.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser,Long> {
    Page<TeamUser> findAllByUser(User user, Pageable pageable);
}
