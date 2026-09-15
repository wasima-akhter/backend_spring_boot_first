package com.example.backend.user.service;

import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.jetbrains.exported.JBRApi.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;

  }

  public User createUser(String name, String email) {

    User user = new User(name, email);
    return userRepository.save(user);
  }
}
