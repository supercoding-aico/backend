package com.github.aico.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileImageRepository extends JpaRepository<UserProfileImage, Long> {
    Optional<UserProfileImage> findByUser(User user);
}
