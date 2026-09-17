package com.example.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserCreateRequest {

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  public UserCreateRequest() {
  }

  public String getName() {
    return this.name;
  }

  public String getEmail() {
    return this.email;
  }

  public String setName(String name) {
    return this.name = name;
  }

  public String setEmail(String email) {
    return this.email = email;
  }
}
