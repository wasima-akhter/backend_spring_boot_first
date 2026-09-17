package com.example.backend.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.backend.user.dto.PostCreateRequest;
import com.example.backend.user.dto.PostResponse;
import com.example.backend.user.dto.UserResponse;
import com.example.backend.user.dto.UserUpdateRequest;
import com.example.backend.user.entity.Post;
import com.example.backend.user.entity.User;
import com.example.backend.user.exception.EmailAlreadyExistsException;
import com.example.backend.user.exception.UserNotFoundException;
import com.example.backend.user.repository.PostRepository;
import com.example.backend.user.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;

  private final PostRepository postRepository;

  public UserService(UserRepository userRepository, PostRepository postRepository) {
    this.userRepository = userRepository;
    this.postRepository = postRepository;

  }

  public UserResponse createUser(String name, String email) {

    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistsException(email);
    }
    User user = new User(name, email);

    User savedUser = userRepository.save(user);

    return toResponse(savedUser);
  }

  // public List<UserResponse> getAllUsers() {
  // return userRepository.findAll()
  // .stream()
  // .map(this::toResponse)
  // .toList();
  // }
  public Page<UserResponse> getAllUsers(Pageable pageable) {

    return userRepository.findAll(pageable)
        .map(this::toResponse);
  }

  public UserResponse getuserById(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

    return toResponse(user);
  }

  public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {

    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

    user.setName(userUpdateRequest.getName());
    user.setEmail(userUpdateRequest.getEmail());

    User updatedUser = userRepository.save(user);

    return toResponse(updatedUser);
  }

  public void deleteUser(Long id) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    userRepository.delete(user);
  }

  private UserResponse toResponse(User user) {
    return new UserResponse(
        user.getId(),
        user.getName(),
        user.getEmail());
  }

  // -- Post
  public PostResponse createPost(
      Long userId,
      PostCreateRequest request) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    Post post = new Post(
        request.getTitle(),
        user);

    Post savedPost = postRepository.save(post);

    return new PostResponse(
        savedPost.getId(),
        savedPost.getTitle(),
        savedPost.getUser().getId());
  }

  public List<PostResponse> getPostsByUserId(Long userId) {

    userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    return postRepository.findById(userId).stream().map(post -> new PostResponse(

        post.getId(),
        post.getTitle(),
        post.getUser().getId())).toList();

  }
}

// Postman
// ↓
// UserController
// ↓
// UserService
// ↓
// UserRepository
// ↓
// PostgreSQL

/*
 *
 *
 * Postman
 * {
 * "name": "Wasima",
 * "email": "wasima@example.com"
 * }
 *
 *
 * UserController
 *
 * Receives the HTTP request
 * ↓
 * Reads the JSON body
 * ↓
 * Passes the information to UserService
 *
 *
 * UserService
 *
 * Receives the user information
 * ↓
 * Creates a User object
 * ↓
 * Tells UserRepository to save it
 *
 *
 * UserRepository
 *
 * This is the database helper.
 *
 * You don't manually write:
 *
 * INSERT INTO users ...
 *
 * Instead:
 *
 * userRepository.save(user);
 */
