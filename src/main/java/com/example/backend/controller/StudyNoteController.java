package com.example.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StudyNoteController {

    // =========================================================================
    // 1 & 2. REQUIRED QUERY PARAMETERS (Default Behavior)
    // =========================================================================

    // Explicit way (required = true is default, but explicit makes intent clear)
    // Success: GET /api/users/search?name=John
    // Failure (400 Bad Request): GET /api/users/search
    @GetMapping("/users/search")
    public ResponseEntity<String> searchUserExplicit(
            @RequestParam(name = "name", required = true) String name) {
        return ResponseEntity.ok("Searching for mandatory name: " + name);
    }

    // =========================================================================
    // 3. OPTIONAL QUERY PARAMETERS & DEFAULT VALUES
    // =========================================================================

    // Optional using standard property mapping
    // Works for both: GET /api/users/filter AND GET /api/users/filter?name=John
    @GetMapping("/users/filter")
    public ResponseEntity<String> filterUserOptional(
            @RequestParam(name = "name", required = false) String name) {
        if (name == null) {
            return ResponseEntity.ok("No filter applied. Returning all users.");
        }
        return ResponseEntity.ok("Filtering users by name: " + name);
    }

    // Optional parameter with a fallback Default Value
    // Match: GET /api/users/archive?page=2 -> page evaluates to 2
    // Match: GET /api/users/archive -> page evaluates to 0.
    @GetMapping("/users/archive")
    public ResponseEntity<String> getArchivedPages(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok("Fetching archive view items from page index: " + page);
    }

    // =========================================================================
    // 5. QUERY PARAMETER TYPES DEMONSTRATION
    // =========================================================================

    // Multi-type data binders matching core primitives and primitives wrappers
    // Target: GET /api/types/primitive?age=25&active=true&minPrice=100.50
    @GetMapping("/types/primitive")
    public ResponseEntity<String> handlePrimitives(
            @RequestParam(required = false) Integer age,
            @RequestParam Boolean active,
            @RequestParam Double minPrice) {
        return ResponseEntity.ok(String.format(
                "Parsed Types -> Age: %d, Active Status: %b, Minimum Boundary Price: %.2f",
                age, active, minPrice));
    }

    @GetMapping("/types/primitive-optional")
    public ResponseEntity<String> handlePrimitivesOptional(
            @RequestParam Optional<Integer> age,
            @RequestParam Boolean active,
            @RequestParam Double minPrice) {

        // Clean, functional fallback processing using .orElse()
        int ageValue = age.orElse(0);

        return ResponseEntity.ok(String.format(
                "Parsed Types -> Age: %d, Active Status: %b, Minimum Boundary Price: %.2f",
                ageValue, active, minPrice));

    }

    // Temporal/Date Data Binding
    // Target: GET /api/types/dates?date=2026-09-22&timestamp=2026-09-22T10:30:00
    @GetMapping("/types/dates")
    public ResponseEntity<String> handleTemporalTypes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp) {
        return ResponseEntity.ok(String.format(
                "Parsed Temporal Targets -> Local ISO Date: %s, Precise Timestamp: %s",
                date, timestamp));
    }

    // Arrays & Iterables / Collections Collections Matching
    // Handles alternate forms:
    // Form 1: GET /api/types/lists?ids=10,20,30
    // Form 2: GET /api/types/lists?ids=10&ids=20&ids=30
    @GetMapping("/types/lists")
    public ResponseEntity<List<Long>> handleCollectionLists(
            @RequestParam List<Long> ids) {
        return ResponseEntity.ok(ids);
    }

    // =========================================================================
    // 6. PRODUCTION SEARCH ENDPOINT (Combined Matrix Blueprint)
    // =========================================================================

    // Standard complex multi-parameter filter route matching enterprise pagination
    // formats
    // Execution: GET
    // /api/products?search=phone&category=electronics&minPrice=500&maxPrice=1500&page=0&size=20
    @GetMapping("/products")
    public ResponseEntity<Page<String>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // Mock processing logic utilizing incoming data structures
        String matrixSummary = String.format(
                "Query executed with criteria [Term: %s, Category: %s, Range: %s to %s]",
                search, category, minPrice, maxPrice);

        List<String> mockResults = Collections.singletonList(matrixSummary);
        PageRequest pageable = PageRequest.of(page, size);

        Page<String> productPage = new PageImpl<>(mockResults, pageable, 1);
        return ResponseEntity.ok(productPage);
    }

    // =========================================================================
    // 6.1 PRODUCTION SEARCH ENDPOINT (Optional Refactored Blueprint)
    // =========================================================================

    // Multi-parameter filter route utilizing Java Optional wrappers for clean
    // fallbacks
    // Execution: GET
    // /api/products?search=phone&category=electronics&minPrice=500&maxPrice=1500&page=0&size=20
    @GetMapping("/products")
    public ResponseEntity<Page<String>> getProducts(
            @RequestParam Optional<String> search,
            @RequestParam Optional<String> category,
            @RequestParam Optional<Double> minPrice,
            @RequestParam Optional<Double> maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // Extract values safely with standard defaults if they are missing
        String searchTerm = search.orElse("ALL");
        String categoryTerm = category.orElse("ANY");
        Double minPriceVal = minPrice.orElse(0.0);
        Double maxPriceVal = maxPrice.orElse(Double.MAX_VALUE);

        // Build the criteria summary using the extracted safe values
        String matrixSummary = String.format(
                "Query executed with criteria [Term: %s, Category: %s, Range: %.2f to %.2f]",
                searchTerm, categoryTerm, minPriceVal, maxPriceVal);

        List<String> mockResults = Collections.singletonList(matrixSummary);
        PageRequest pageable = PageRequest.of(page, size);

        Page<String> productPage = new PageImpl<>(mockResults, pageable, 1);
        return ResponseEntity.ok(productPage);
    }

}
