# Spring Security Flow — Root to End Revision Guide

> **Goal:** Memorize the exact chain of calls from incoming HTTP request to final response, with each component's exact role.

---

## 1. The Big Picture (Mental Map)

```text
[ Client HTTP Request ]
          │
          ▼
1. DelegatingFilterProxy / FilterChainProxy
          │
          ▼
2. SecurityFilterChain (CSRF, Request Matchers, Custom Filters)
          │
    ┌─────┴────────────────────────────────┐
    │                                      │
[ Public / Login URL ]           [ Protected URL ]
    │                                      │
    ▼                                      ▼
Controller / Service              SecurityContext Check
    │                                      │
    ▼                                      ▼
AuthenticationManager            AuthorizationFilter
    │                                      │
    ▼                                      ▼
DaoAuthenticationProvider         Controller / Endpoint
    ├── UserDetailsService (DB lookup)
    └── PasswordEncoder (BCrypt match)
    │
    ▼
Authenticated Token / JWT Response
```

---

## 2. Flow A: Login Authentication (`POST /api/v1/login`)

Trace of what happens when a user attempts to log in:

| Step | Component | Code / Action | Exact Task Description |
|---|---|---|---|
| **1. Root Call** | **Client** | `POST /api/v1/login` `{email, password}` | Initiates login request carrying raw credentials. |
| **2. Filter Chain** | **`SecurityFilterChain`** | `.requestMatchers("/api/v1/login").permitAll()` | Matches URL and allows request without prior authentication. |
| **3. Controller** | **`UserController`** | `@PostMapping("/api/v1/login")` | Receives JSON request and passes credentials to Service layer. |
| **4. Service** | **`UserService`** | `new UsernamePasswordAuthenticationToken(email, password)` | Packages raw email & raw password into an **unauthenticated** token object. |
| **5. Root Auth Call** | **`AuthenticationManager`** | `authenticationManager.authenticate(token)` | Coordinates the authentication process (implemented by `ProviderManager`). |
| **6. Provider** | **`DaoAuthenticationProvider`** | Checks if it supports username/password tokens | Delegates fetching user data and verifying credentials. |
| **7. Fetch User** | **`CustomUserDetailsService`** | `loadUserByUsername(email)` | Calls `userRepository.findByEmail(email)`. Returns `UserDetails` containing stored hashed password and authorities. |
| **8. Password Verify** | **`PasswordEncoder`** | `passwordEncoder.matches(rawPassword, encodedPassword)` | BCrypt checks if the submitted raw password matches the database hash. |
| **9. Decision** | **Outcome** | Match = Success<br>Mismatch = `BadCredentialsException` | If invalid, aborts with 401. If valid, marks token as **authenticated**. |
| **10. Token / Return** | **`JwtService` / Response** | `jwtService.generateToken(...)` or `LoginResponse` | Issues JWT token or returns user details to client. |

---

## 3. Flow B: Protected Request (`GET /api/v1/user/10/posts`)

Trace of what happens when a request hits a secured endpoint:

| Step | Component | Exact Task Description |
|---|---|---|
| **1. Incoming Call** | **Client** | Sends request with `Authorization: Bearer <jwt-token>` header. |
| **2. Entry Proxy** | **`DelegatingFilterProxy`** | Standard servlet filter that hands request over to Spring-managed security filters. |
| **3. Dispatcher** | **`FilterChainProxy`** | Selects the matching `SecurityFilterChain` for the request path. |
| **4. Custom JWT Filter** | **`JwtAuthFilter`** *(Once configured)* | Extracts Bearer token, validates signature/expiration via `JwtService`, extracts email. |
| **5. Load & Context** | **`SecurityContextHolder`** | Sets `SecurityContextHolder.getContext().setAuthentication(...)` so Spring knows who is logged in for this thread. |
| **6. Auth Check** | **`AuthorizationFilter`** | Checks `.anyRequest().authenticated()` rule. If `SecurityContext` has authenticated principal → allows through; else 403 Forbidden / 401 Unauthorized. |
| **7. Target Handler** | **`DispatcherServlet` ➔ `Controller`** | Request reaches target controller method and executes business logic. |

---

## 4. Quick Component Dictionary (Memorize Roles)

- **`SecurityFilterChain`**: The gatekeeper. Defines which paths are public (`permitAll()`) and which are guarded (`authenticated()`).
- **`UsernamePasswordAuthenticationToken`**: The transport envelope for credentials (unauthenticated before check, authenticated after check).
- **`AuthenticationManager`**: The coordinator. Receives the token and delegates work to the provider.
- **`DaoAuthenticationProvider`**: The judge. Combines `UserDetailsService` and `PasswordEncoder` to decide if login is valid.
- **`UserDetailsService`**: The data fetcher. Loads user entity from DB and adapts it into Spring's `UserDetails`.
- **`PasswordEncoder`**: The hasher. Hashes passwords and checks hashes (`BCryptPasswordEncoder`).
- **`SecurityContextHolder`**: The memory box. Thread-local storage holding the currently authenticated user for the duration of the request.
- **`JwtService`**: The passport maker/checker. Generates signed tokens and verifies token claims.

---

## 5. 7-Word Mental Anchor

> **Filter ➔ Token ➔ Manager ➔ Provider ➔ UserDetails ➔ Encoder ➔ Context**
