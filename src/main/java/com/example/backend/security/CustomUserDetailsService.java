
package com.example.backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(
            "User not found"));

    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities("USER")
        .build();
  }
}

/*
 * 
 * ┌─────────────────────────────────────┐
 * │ Your application │
 * │ │
 * │ User entity │
 * │ id, name, email, password, posts │
 * └──────────────────┬──────────────────┘
 * │
 * │ convert
 * ▼
 * ┌─────────────────────────────────────┐
 * │ Spring Security │
 * │ │
 * │ UserDetails │
 * │ username, password, authorities │
 * └──────────────────┬──────────────────┘
 * │
 * ▼
 * AuthenticationManager
 * │
 * ▼
 * Authentication result
 */
