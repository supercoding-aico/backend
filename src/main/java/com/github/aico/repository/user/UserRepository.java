package com.github.aico.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>,QUserRepository {
    @Query("SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role r WHERE u.email =:email")
    Optional<User> findByEmailUserFetchJoin(String email);

    boolean existsByEmail(String email);
    boolean existsByNickname (String nickname);
}
