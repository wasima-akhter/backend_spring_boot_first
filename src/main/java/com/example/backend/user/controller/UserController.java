
package com.example.backend.user.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.dto.PostCreateRequest;
import com.example.backend.user.dto.PostResponse;
import com.example.backend.user.dto.UserCreateRequest;
import com.example.backend.user.dto.UserResponse;
import com.example.backend.user.dto.UserUpdateRequest;
import com.example.backend.user.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/api/v1/create-user")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(
      @Valid @RequestBody UserCreateRequest request) {
    return userService.createUser(
        request.getName(),
        request.getEmail());
  }

  @GetMapping("/api/v1/all-users")
  public Page<UserResponse> getAllUsers(Pageable pageable) {
    return userService.getAllUsers(pageable);
  }

  @GetMapping("/api/v1/user/{id}")
  public UserResponse getUserById(
      @PathVariable Long id) {
    return userService.getuserById(id);
  }

  @PutMapping("/api/v1/user/{id}")
  public UserResponse updateUser(
      @PathVariable Long id,
      @Valid @RequestBody UserUpdateRequest request) {
    return userService.updateUser(id, request);
  }

  @DeleteMapping("/api/v1/user/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }

  // -- Post
  @PostMapping("/api/v1/user/{userId}/create-post")
  @ResponseStatus(HttpStatus.CREATED)
  public PostResponse createPost(
      @PathVariable Long userId,
      @Valid @RequestBody PostCreateRequest request) {
    return userService.createPost(userId, request);
  }

  @GetMapping("api/v1/user/{userId}/posts")
  public List<PostResponse> getPostsByUserId(@PathVariable Long userId) {
    return userService.getPostsByUserId(userId);
  }
}

// Security
/*
 * Request
 * ↓
 * Spring Security
 * ↓
 * ├── Not authenticated → 401
 * │
 * └── Authenticated
 * ↓
 * Controller
 * ↓
 * Service
 * ↓
 * Repository
 */

/*
 * @Valid @RequestBody UserCreateRequest request
 *
 * There are actually two different jobs:
 *
 * @RequestBody
 * ↓
 * "Take the JSON and turn it into UserCreateRequest"
 *
 * @Valid
 * ↓
 * "Check whether that UserCreateRequest follows my rules"
 */
