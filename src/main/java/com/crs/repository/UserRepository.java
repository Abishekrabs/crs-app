package com.crs.repository;

import com.crs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);
    Boolean existsByEmail(String email);
}
