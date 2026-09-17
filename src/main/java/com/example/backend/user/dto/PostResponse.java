package com.example.backend.user.dto;

public class PostResponse {

  private Long id;
  private String title;
  private Long userId;

  public PostResponse(Long id, String title, Long userId) {
    this.id = id;
    this.title = title;
    this.userId = userId;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public Long getUserId() {
    return userId;
  }
}
