package com.example.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
}
