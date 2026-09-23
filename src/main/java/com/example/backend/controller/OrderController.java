package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.dto.OrderCreateRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  @PostMapping
  public ResponseEntity<String> createOrder(@Valid @RequestBody OrderCreateRequest request) {
    // @RequestBody converts incoming JSON -> Java Object
    // @Valid triggers the @NotBlank, @Email, @Positive checks
    return ResponseEntity.ok("Order processed successfully for " + request.getName());
  }
}
