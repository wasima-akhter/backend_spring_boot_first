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

    /**
     * Called by Spring when the application context starts up.
     *
     * Registers a PasswordEncoder bean so Spring can use BCrypt
     * to hash passwords before storing them and verify passwords
     * during authentication.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Called by Spring during application startup to register
     * the AuthenticationManager bean.
     *
     * AuthenticationManager is used when authentication is needed,
     * for example, when a user logs in.
     *
     * The actual AuthenticationManager is obtained from
     * Spring Security's AuthenticationConfiguration.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * Called by Spring during application startup to configure
     * the application's HTTP security rules.
     *
     * These rules are applied to incoming HTTP requests.
     *
     * Important:
     * - CSRF protection is disabled.
     * - Listed endpoints are accessible without authentication.
     * - All other endpoints require authentication.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
            throws Exception {

        httpSecurity

                // Disables CSRF protection.
                // Commonly disabled for stateless REST APIs that use
                // token-based authentication, when configured appropriately.
                // Cross-Site Request Forgery
                //
                // Disabling CSRF (Cross-Site Request Forgery) means turning off the security
                // feature that checks whether state-changing HTTP requests (such as POST, PUT,
                // DELETE, and PATCH) genuinely originate from your application's user interface
                // rather than a malicious external site
                .csrf(csrf -> csrf.disable())

                // Defines which HTTP requests are public and which
                // require the user to be authenticated.
                .authorizeHttpRequests(auth -> auth

                        // Anyone can access these endpoints without logging in.
                        .requestMatchers(
                                "/",
                                "/bool",
                                "/hello",
                                "/api/v1/getName/{id}",
                                "/api/v1/user/{userId}/posts",
                                "/api/v1/create-user",
                                "/api/v1/login",
                                "/api/v1/all-users",
                                "/api/v1/user/{userId}",
                                "/api/v1/user/{userId}/create-post",
                                "/api/v1/user/{userId}/posts_paged")
                        .permitAll()

                        // Every other request must be authenticated.
                        .anyRequest().authenticated());

        // Builds and returns the configured security filter chain.
        // Spring Security uses this chain to process incoming requests.
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
