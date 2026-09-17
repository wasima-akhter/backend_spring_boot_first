package com.example.backend.common.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.user.exception.EmailAlreadyExistsException;
import com.example.backend.user.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // 400 - Request body validation failed
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(
      MethodArgumentNotValidException exception,
      HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(
        HttpStatus.BAD_REQUEST);

    problem.setTitle("Validation failed");
    problem.setDetail("One or more fields are invalid.");

    problem.setProperty("timestamp", Instant.now());
    problem.setProperty("path", request.getRequestURI());

    Map<String, String> fieldErrors = new LinkedHashMap<>();

    exception.getBindingResult()
        .getFieldErrors()
        .forEach(error -> fieldErrors.put(
            error.getField(),
            error.getDefaultMessage()));

    problem.setProperty("fieldErrors", fieldErrors);

    return problem;
  }

  // 404 - User not found
  @ExceptionHandler(UserNotFoundException.class)
  public ProblemDetail handleUserNotFoundException(
      UserNotFoundException exception,
      HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(
        HttpStatus.NOT_FOUND);

    problem.setTitle("User not found");
    problem.setDetail(exception.getMessage());

    problem.setProperty("timestamp", Instant.now());
    problem.setProperty("path", request.getRequestURI());

    return problem;
  }

  // 400 - General validation constraint violation
  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolationException(
      ConstraintViolationException exception,
      HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(
        HttpStatus.BAD_REQUEST);

    problem.setTitle("Validation failed");
    problem.setDetail(exception.getMessage());

    problem.setProperty("timestamp", Instant.now());
    problem.setProperty("path", request.getRequestURI());

    return problem;
  }

  //
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ProblemDetail handleEmailAlreadyExistsException(
      EmailAlreadyExistsException exception,
      HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(
        HttpStatus.CONFLICT);

    problem.setTitle("Email already exists");
    problem.setDetail(exception.getMessage());

    problem.setProperty("timestamp", Instant.now());
    problem.setProperty("path", request.getRequestURI());

    return problem;
  }

  // 500 - Unexpected errors
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpectedException(
      Exception exception,
      HttpServletRequest request) {

    log.error(
        "Unhandled exception occurred. Method: {}, Path: {}",
        request.getMethod(),
        request.getRequestURI(),
        exception);

    ProblemDetail problem = ProblemDetail.forStatus(
        HttpStatus.INTERNAL_SERVER_ERROR);

    problem.setTitle("Internal server error");
    problem.setDetail("An unexpected error occurred.");

    problem.setProperty("timestamp", Instant.now());
    problem.setProperty("path", request.getRequestURI());

    return problem;
  }

}
