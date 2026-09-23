
package com.example.backend.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.user.dto.ProductCreateRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // 24. Single File + JSON DTO Object
    @PostMapping(value = "/single-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createProductWithSingleImage(
            @Valid @RequestPart("product") ProductCreateRequest request,
            @RequestPart("image") MultipartFile image) {

        String message = String.format("Product '%s' created at price $%s with image: %s",
                request.getName(), request.getPrice(), image.getOriginalFilename());
        return ResponseEntity.ok(message);
    }

    // 25. Multiple Files + JSON DTO Object
    @PostMapping(value = "/multiple-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createProductWithMultipleImages(
            @Valid @RequestPart("product") ProductCreateRequest request,
            @RequestPart("images") List<MultipartFile> images) {

        String message = String.format("Product '%s' created at price $%s with %d gallery images.",
                request.getName(), request.getPrice(), images.size());
        return ResponseEntity.ok(message);
    }
}
