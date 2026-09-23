package com.example.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> createUserProfile(
      @RequestParam("name") String name,
      @RequestParam("file") MultipartFile file) {

    String responseMessage = String.format("Profile created for Name: %s. Attachment: %s",
        name, file.getOriginalFilename());
    return ResponseEntity.ok(responseMessage);
  }
}
