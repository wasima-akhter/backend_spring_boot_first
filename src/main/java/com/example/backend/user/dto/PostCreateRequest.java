package com.example.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public class PostCreateRequest {

  @NotBlank(message = "Title is required")
  private String title;

  public PostCreateRequest() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
