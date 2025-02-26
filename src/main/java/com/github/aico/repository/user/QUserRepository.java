package com.github.aico.repository.user;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface QUserRepository {
    Optional<User> findByEmailWithRoles(String email);
}
