package com.example.backend.common.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.backend.user.exception.EmailAlreadyExistsException;
import com.example.backend.user.exception.InvalidCredentialsException;
import com.example.backend.user.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler2 {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==========================================
    // 400 - BAD REQUEST EXCEPTIONS
    // ==========================================

    // Request body field validation failed (e.g., @Valid checks)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Validation Failed");
        problem.setDetail("One or more fields failed validation checks.");

        // Frontend anchors
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorKey", "VALIDATION_FAILED");
        problem.setProperty("userMessage", "Please check the form inputs and try again.");

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        problem.setProperty("fieldErrors", fieldErrors);
        return problem;
    }

    // Parameters or URI constraints broken
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Constraint Violation");
        problem.setDetail(exception.getMessage());

        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorKey", "CONSTRAINT_VIOLATION");
        problem.setProperty("userMessage", "The requested operation contains invalid parameter values.");

        return problem;
    }

    // Invalid parameters passing (e.g. passing a string string into an
    // integer/Instant path parameter)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Type Mismatch Error");
        problem.setDetail(String.format("Parameter '%s' expects a different type format.", exception.getName()));

        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorKey", "INVALID_PARAMETER_TYPE");
        problem.setProperty("userMessage", "The request format is invalid.");

        return problem;
    }

    // ==========================================
    // 401 & 403 - SECURITY EXCEPTIONS
    // ==========================================

    // Authentication failure (Bad username/password or invalid JWT)
    @ExceptionHandler({ InvalidCredentialsException.class, BadCredentialsException.class })
    public ProblemDetail handleUnauthorizedException(
            Exception exception,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        problem.setTitle("Unauthorized Access");
        problem.setDetail(exception.getMessage());

        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorKey", "INVALID_CREDENTIALS");
        problem.setProperty("userMessage", "Incorrect username, email, or password.");

        return problem;
    }

    // Authenticated but does not possess the correct roles/authorities
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problem.setTitle("Forbidden Access");
        problem.setDetail(exception.getMessage());

        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorKey", "ACCESS_DENIED");
        problem.setProperty("userMessage", "You do not have the required permissions to access this action.");

        return problem;
    }

    // ==========================================
    // 404 - NOT FOUND EXCEPTIONS
    // ==========================================

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(
            UserNotFoundException exception,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("Resource Not Found");
        problem.setDetail(exception.getMessage());

        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorKey", "USER_NOT_FOUND");
        problem.setProperty("userMessage", "The requested user profile could not be found.");

        return problem;
    }

    // ==========================================
    // 409 - CONFLICT EXCEPTIONS
    // ==========================================

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExistsException(
            EmailAlreadyExistsException exception,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problem.setTitle("Data Integrity Conflict");
        problem.setDetail(exception.getMessage());

        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorKey", "EMAIL_ALREADY_EXISTS");
        problem.setProperty("userMessage", "This email address is already registered in our system.");

        return problem;
    }

    // ==========================================
    // 500 - INTERNAL SERVER ERROR
    // ==========================================

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {

        log.error("Unhandled fallback exception occurred. Method: {}, Path: {}",
                request.getMethod(), request.getRequestURI(), exception);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected engine or database failure occurred.");

        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorKey", "INTERNAL_SERVER_ERROR");
        problem.setProperty("userMessage", "Something went wrong on our end. Please try again later.");

        return problem;
    }
}
