Your `GlobalExceptionHandler` handles 5 types of errors. Let's go through real-life Postman examples, including the HTTP method, URL, request body, expected status code, and example JSON response.

Assume your Spring Boot application runs at `http://localhost:8080` and has these endpoints:

- `POST /api/v1/users` — create a user
- `GET /api/v1/users/{id}` — find a user by ID

The examples below assume your controller and DTO validation are configured accordingly.

## 1. `MethodArgumentNotValidException` — 400 Bad Request

Real-life scenario: A user submits the signup form with an invalid email or missing required fields.

POST

`http://localhost:8080/api/v1/users`

Postman → Body → raw → JSON

```
{
  "name": "",
  "email": "wasima-invalid-email",
  "password": "123"
}
```

400 Bad Request

Expected response

```
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields are invalid.",
  "timestamp": "2026-10-17T08:00:00Z",
  "path": "/api/v1/users",
  "fieldErrors": {
    "name": "Name is required",
    "email": "must be a well-formed email address",
    "password": "Password must be at least 8 characters"
  }
}
```

Why does this exception occur?

Spring validates a request DTO annotated with `@Valid`. If a field violates a constraint such as `@NotBlank`, `@Email`, or `@Size`, Spring throws `MethodArgumentNotValidException`.

Example DTO:

```
public record CreateUserRequest(
    @NotBlank(message = "Name is required")
    String name,

    @Email
    @NotBlank
    String email,

    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {}
```

Your controller needs `@Valid`:

```
@PostMapping
public ResponseEntity<?> createUser(
    @Valid @RequestBody CreateUserRequest request) {
  // Create user
}
```

Note: the sample response assumes your DTO constraints produce those exact messages. Also, the `LinkedHashMap` retains the last error if multiple errors occur for the same field.

## 2. `UserNotFoundException` — 404 Not Found

Real-life scenario: A user requests a profile that doesn't exist.

GET

`http://localhost:8080/api/v1/users/9999`

Postman → Body

No request body is needed.

404 Not Found

Expected response

```
{
  "type": "about:blank",
  "title": "User not found",
  "status": 404,
  "detail": "User not found with id: 9999",
  "timestamp": "2026-10-17T08:00:00Z",
  "path": "/api/v1/users/9999"
}
```

Why does this exception occur?

Your service throws `UserNotFoundException` when the requested ID isn't found in the database.

```
User user = userRepository.findById(id)
    .orElseThrow(() -> new UserNotFoundException(id));
```

For example, your custom exception could be:

```
public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long id) {
    super("User not found with id: " + id);
  }
}
```

The handler catches it and returns HTTP `404`, meaning the requested resource does not exist.

## 3. `ConstraintViolationException` — 400 Bad Request

Real-life scenario: A request contains an invalid query parameter or path variable, such as a page number below zero.

GET

`http://localhost:8080/api/v1/users?page=-1`

Example controller

```
@GetMapping
public List<UserResponse> getUsers(
    @RequestParam @Min(0) int page) {
  // Fetch users
}
```

400 Bad Request

Expected response

```
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "detail": "getUsers.page: must be greater than or equal to 0",
  "timestamp": "2026-10-17T08:00:00Z",
  "path": "/api/v1/users"
}
```

Important: For this handler to catch method parameter validation, you generally need method validation enabled, for example with `@Validated` on the controller in the traditional Spring method-validation setup.

```
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    // ...
}
```

With modern Spring Framework versions, method-validation behavior can vary depending on how validation is triggered. Some MVC controller method validation can raise `HandlerMethodValidationException` instead, so your current handler won't catch every possible method-validation error.

Difference from the first exception:

- `MethodArgumentNotValidException`: typically invalid fields in a request DTO.
- `ConstraintViolationException`: constraints violated during method-level validation, such as `@Min` on a parameter, depending on the validation setup.

## 4. `EmailAlreadyExistsException` — 409 Conflict

Real-life scenario: A person tries to register with an email address already registered in your database.

POST

`http://localhost:8080/api/v1/users`

Postman → Body → raw → JSON

```
{
  "name": "Wasima Akhter",
  "email": "existing@example.com",
  "password": "SecurePassword123"
}
```

Assumption: `existing@example.com` is already registered, and the service checks for duplicate emails.

409 Conflict

Expected response

```
{
  "type": "about:blank",
  "title": "Email already exists",
  "status": 409,
  "detail": "Email already registered: existing@example.com",
  "timestamp": "2026-10-17T08:00:00Z",
  "path": "/api/v1/users"
}
```

Why does this exception occur?

Your service detects that the email is already in use and throws the custom exception.

```
if (userRepository.existsByEmail(request.email())) {
  throw new EmailAlreadyExistsException(
      "Email already registered: " + request.email()
  );
}
```

Your handler returns `409 Conflict` because the request conflicts with the current state of your application's data.

For production applications, also enforce email uniqueness at the database level to prevent duplicate registrations caused by simultaneous requests.

## 5. `Exception` — 500 Internal Server Error

Real-life scenario: An unexpected bug occurs, such as a `NullPointerException`, or an unhandled runtime error happens while processing a request.

GET

`http://localhost:8080/api/v1/users/1`

Postman → Body

No request body is needed.

Assumption: the request triggers an unexpected exception not handled by a more specific handler.

500 Internal Server Error

Expected response

```
{
  "type": "about:blank",
  "title": "Internal server error",
  "status": 500,
  "detail": "An unexpected error occurred.",
  "timestamp": "2026-10-17T08:00:00Z",
  "path": "/api/v1/users/1"
}
```

Why does this exception occur?

For example, an unexpected null value causes:

```
String name = null;
name.toUpperCase(); // Throws NullPointerException
```

Your global handler catches the unhandled exception, logs the exception details on the server, and returns a generic message to the client.

Your logging code is useful:

```
log.error(
    "Unhandled exception occurred. Method: {}, Path: {}",
    request.getMethod(),
    request.getRequestURI(),
    exception
);
```

Production rule: Don't return stack traces or internal exception details to clients. Keep those details in server logs.

## Summary: Which status code should you expect?

| Exception                         | HTTP status | Meaning                                          |
| --------------------------------- | ----------- | ------------------------------------------------ |
| `MethodArgumentNotValidException` | `400`       | Request body fields are invalid                  |
| `UserNotFoundException`           | `404`       | Requested user doesn't exist                     |
| `ConstraintViolationException`    | `400`       | Method parameter or constraint validation failed |
| `EmailAlreadyExistsException`     | `409`       | Email conflicts with existing data               |
| Generic `Exception`               | `500`       | Unexpected server-side error                     |

One key point: your exception handler defines the response format, but your DTO constraints, controller configuration, service logic, and database state determine whether each exception actually occurs. The JSON examples are illustrative; exact validation messages and timestamps will depend on your implementation and runtime.
