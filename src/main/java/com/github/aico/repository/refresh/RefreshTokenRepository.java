package com.github.aico.repository.refresh;

import com.github.aico.repository.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    boolean existsByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}
