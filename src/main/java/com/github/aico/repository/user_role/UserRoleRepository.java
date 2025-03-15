package com.github.aico.repository.user_role;

import com.github.aico.repository.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {
    List<UserRole> findByUser(User user);
}
