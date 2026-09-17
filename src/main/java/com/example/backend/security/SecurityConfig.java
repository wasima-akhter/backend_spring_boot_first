package com.example.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {

    httpSecurity.csrf(t -> t.disable())
        .authorizeHttpRequests(
            t -> t.requestMatchers("/hello", "/api/v1/create-user", "/api/v1/login").permitAll().anyRequest()
                .authenticated());

    return httpSecurity.build();
  }

}

/*
 *
 * @Configuration
 * ↓
 * Spring, this class contains configuration.
 *
 *
 * @Bean
 * ↓
 * Spring, manage the object returned by this method.
 *
 *
 * SecurityFilterChain
 * ↓
 * This is the security pipeline for HTTP requests.
 *
 *
 * HttpSecurity
 * ↓
 * Spring gives us a configuration object.
 *
 *
 * csrf(...)
 * ↓
 * Configure CSRF.
 *
 *
 * csrf -> csrf.disable()
 * ↓
 * Disable CSRF.
 *
 *
 * authorizeHttpRequests(...)
 * ↓
 * Configure which HTTP requests are allowed.
 *
 *
 * requestMatchers("/hello").permitAll()
 * ↓
 * /hello is public.
 *
 *
 * anyRequest().authenticated()
 * ↓
 * Everything else requires login/authentication.
 *
 *
 * http.build()
 * ↓
 * Build the final security filter chain.
 *
 *
 * return
 * ↓
 * Give it to Spring.
 */
