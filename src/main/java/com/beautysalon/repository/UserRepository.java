package com.beautysalon.repository;

import com.beautysalon.entity.User;
import com.beautysalon.enums.Category;
import com.beautysalon.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByCategoryAndRole(Category category, Role role);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetToken(String token);
}