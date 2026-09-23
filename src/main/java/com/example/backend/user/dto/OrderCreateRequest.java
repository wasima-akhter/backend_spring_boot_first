package com.example.backend.user.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.example.backend.model.OrderStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class OrderCreateRequest {

  @NotBlank(message = "Name cannot be blank")
  private String name;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;

  @Size(min = 8, message = "Password must be at least 8 characters long")
  @NotBlank(message = "Password is required")
  private String password;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be a positive number")
  private BigDecimal price;

  @NotNull(message = "Scheduled date/time is required")
  private OffsetDateTime scheduledAt; // Matches "2026-09-22T18:30:00+06:00"

  @NotNull(message = "Creation timestamp is required")
  private Instant createdAt; // For exact instants in time

  // • 2026-09-23T03:09:45Z (The year 2026, September 23rd, 03:09 AM and 45
  // seconds UTC)
  //
  // • 2026-09-23T03:09:45.123456789Z (Includes nanosecond precision)

  @NotNull(message = "Date is required")
  private LocalDate date; // For a date without time (YYYY-MM-DD)

  @NotNull(message = "Status is required")
  private OrderStatus status; // Maps text like "PENDING" directly to the Enum

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public OffsetDateTime getScheduledAt() {
    return scheduledAt;
  }

  public void setScheduledAt(OffsetDateTime scheduledAt) {
    this.scheduledAt = scheduledAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }
}
