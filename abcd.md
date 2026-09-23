# Spring Boot HTTP Request Types — Study & Revision Note

> **Purpose:** Come back to this note later to revise how production APIs receive data from URLs, query parameters, headers, bodies, files, and combinations of them.

---

# 1. The Big Picture

An HTTP request can carry information through several places:

```text
HTTP REQUEST
│
├── HTTP Method
│     ├── GET
│     ├── POST
│     ├── PUT
│     ├── PATCH
│     └── DELETE
│
├── URL Path
│     └── /users/25/orders/100
│
├── Query Parameters
│     └── ?page=0&size=20&sort=name,asc
│
├── Headers
│     ├── Authorization
│     ├── Content-Type
│     └── Accept
│
├── Body
│     ├── JSON
│     ├── XML
│     └── other formats
│
└── Multipart
      ├── Files
      └── Other form fields
```

Spring Boot provides different annotations to read each part.

---

# 2. Quick Annotation Reference

| Spring annotation/type | Reads from                      | Example                     |
| ---------------------- | ------------------------------- | --------------------------- |
| `@PathVariable`        | URL path                        | `/users/{id}`               |
| `@RequestParam`        | Query parameter                 | `?page=0`                   |
| `@RequestHeader`       | HTTP header                     | `Authorization: Bearer ...` |
| `@RequestBody`         | Request body                    | JSON                        |
| `@RequestPart`         | Multipart part                  | File + JSON                 |
| `MultipartFile`        | Uploaded file                   | `profile.jpg`               |
| `Pageable`             | Pagination/sorting query params | `?page=0&size=20`           |
| `@Valid`               | Validates DTO                   | Required/format rules       |

---

# 3. PATH VARIABLES

Path variables identify a particular resource.

## Required path variable

```java
@GetMapping("/users/{id}")
public UserResponse getUser(
    @PathVariable Long id) {

}
```

URL:

```text
GET /users/25
```

Here:

```text
{id} = 25
```

### Important

A path variable is normally **required**.

This:

```text
/users/25
```

matches:

```java
/users/{id}
```

But this:

```text
/users
```

does not.

---

## Optional path variable

Path variables themselves are generally not made optional.

Instead, define two mappings:

```java
@GetMapping({"/users", "/users/{id}"})
public Object getUsers(
    @PathVariable(required = false) Long id) {

}
```

Now both can work:

```text
GET /users
```

and:

```text
GET /users/25
```

### Production recommendation

Usually, prefer separate endpoints or query parameters instead of optional path variables.

For example:

```text
GET /users
GET /users/{id}
```

is clearer than:

```text
GET /users/{id?}
```

---

# 4. QUERY PARAMETERS

Query parameters are values after `?`.

Example:

```text
GET /users?page=0&size=20
```

---

## Required query parameter

```java
@GetMapping("/users")
public List<UserResponse> search(
    @RequestParam String name) {

}
```

Request:

```text
GET /users?name=John
```

If `name` is missing, Spring considers the request invalid.

---

## Explicit required query parameter

```java
@RequestParam(
    name = "name",
    required = true
)
String name
```

This is the default.

---

## Optional query parameter

```java
@RequestParam(
    name = "name",
    required = false
)
String name
```

Both work:

```text
GET /users
```

and:

```text
GET /users?name=John
```

---

## Optional query parameter with default value

```java
@RequestParam(
    name = "page",
    defaultValue = "0"
)
int page
```

If:

```text
?page=2
```

then:

```text
page = 2
```

If omitted:

```text
page = 0
```

---

# 5. QUERY PARAMETER TYPES

## String

```java
@RequestParam String name
```

```text
/users?name=John
```

---

## Integer

```java
@RequestParam Integer age
```

```text
/users?age=25
```

---

## Boolean

```java
@RequestParam Boolean active
```

```text
/users?active=true
```

or:

```text
/users?active=false
```

---

## Double

```java
@RequestParam Double minPrice
```

```text
/products?minPrice=100.50
```

---

## Date

```java
@RequestParam LocalDate date
```

```text
/orders?date=2026-09-22
```

---

## DateTime

```java
@RequestParam LocalDateTime timestamp
```

```text
/orders?timestamp=2026-09-22T10:30:00
```

---

## List

```java
@RequestParam List<Long> ids
```

Example:

```text
/users?ids=10,20,30
```

Can represent:

```java
[10, 20, 30]
```

Another common form:

```text
/users?ids=10&ids=20&ids=30
```

---

# 6. REQUIRED VS OPTIONAL QUERY PARAMETERS

A production search endpoint often looks like:

```java
@GetMapping("/products")
public Page<ProductResponse> getProducts(

    @RequestParam(required = false)
    String search,

    @RequestParam(required = false)
    String category,

    @RequestParam(required = false)
    Double minPrice,

    @RequestParam(required = false)
    Double maxPrice,

    @RequestParam(defaultValue = "0")
    int page,

    @RequestParam(defaultValue = "20")
    int size
) {

}
```

Possible requests:

```text
/products
```

```text
/products?search=burger
```

```text
/products?category=food
```

```text
/products?minPrice=100&maxPrice=500
```

```text
/products?search=burger&category=food&page=0&size=20
```

This pattern is extremely common in production applications.

---

# 7. REQUEST HEADERS

Headers carry metadata about the request.

Example:

```http
Authorization: Bearer JWT_TOKEN
Content-Type: application/json
Accept: application/json
```

---

## Required header

```java
@RequestHeader("Authorization")
String authorization
```

If missing, the request fails.

---

## Optional header

```java
@RequestHeader(
    value = "X-Device-ID",
    required = false
)
String deviceId
```

The request can contain:

```http
X-Device-ID: abc123
```

or omit it.

---

## Header with default value

```java
@RequestHeader(
    value = "X-App-Version",
    defaultValue = "1.0.0"
)
String appVersion
```

---

# 8. COMMON PRODUCTION HEADERS

### Authentication

```http
Authorization: Bearer <JWT>
```

### Content type

```http
Content-Type: application/json
```

### Response type

```http
Accept: application/json
```

### Device ID

```http
X-Device-ID: abc123
```

### App version

```http
X-App-Version: 2.4.1
```

### Platform

```http
X-Platform: android
```

These custom headers depend on the application's requirements.

---

# 9. REQUEST BODY

The request body contains the actual payload.

Most modern REST APIs use JSON.

```java
@PostMapping("/users")
public UserResponse createUser(
    @RequestBody UserCreateRequest request) {

}
```

Request:

```http
POST /users
Content-Type: application/json
```

```json
{
  "name": "John",
  "email": "john@example.com",
  "age": 25
}
```

---

# 10. REQUIRED REQUEST BODY

Normally:

```java
@RequestBody UserCreateRequest request
```

means the body is expected.

You can explicitly write:

```java
@RequestBody(required = true)
UserCreateRequest request
```

---

# 11. OPTIONAL REQUEST BODY

```java
@RequestBody(required = false)
UserCreateRequest request
```

Now the request body may be absent.

Example:

```http
POST /something
```

with no JSON body.

---

# 12. JSON DATA TYPES

## String

```json
{
  "name": "John"
}
```

```java
private String name;
```

---

## Integer

```json
{
  "age": 25
}
```

```java
private Integer age;
```

---

## Decimal

```json
{
  "price": 29.99
}
```

```java
private BigDecimal price;
```

For money, `BigDecimal` is generally preferable to `double`.

---

## Boolean

```json
{
  "active": true
}
```

```java
private Boolean active;
```

---

## Null

```json
{
  "middleName": null
}
```

Java:

```java
private String middleName;
```

will receive:

```text
null
```

---

# 13. NESTED JSON OBJECT

Request:

```json
{
  "name": "John",
  "address": {
    "city": "Dhaka",
    "country": "Bangladesh"
  }
}
```

DTO:

```java
public class UserCreateRequest {

    private String name;

    private AddressRequest address;
}
```

---

# 14. JSON LIST

```json
{
  "name": "John",
  "hobbies": ["football", "reading", "gaming"]
}
```

Java:

```java
private List<String> hobbies;
```

---

# 15. LIST OF OBJECTS

```json
{
  "restaurantId": 10,
  "foods": [
    {
      "name": "Burger",
      "price": 250
    },
    {
      "name": "Pizza",
      "price": 500
    }
  ]
}
```

Java:

```java
private List<FoodRequest> foods;
```

Very common in order/cart APIs.

---

# 16. DATE/TIME IN JSON

Example:

```json
{
  "scheduledAt": "2026-09-22T18:30:00+06:00"
}
```

Java might use:

```java
private OffsetDateTime scheduledAt;
```

For an instant in time:

```java
private Instant createdAt;
```

For a date without time:

```java
private LocalDate date;
```

---

# 17. ENUM IN JSON

Request:

```json
{
  "status": "PENDING"
}
```

Java:

```java
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    DELIVERED,
    CANCELLED
}
```

DTO:

```java
private OrderStatus status;
```

---

# 18. REQUEST BODY VALIDATION

Required field:

```java
@NotNull
private String name;
```

String cannot be blank:

```java
@NotBlank
private String name;
```

Email:

```java
@Email
private String email;
```

Minimum length:

```java
@Size(min = 8)
private String password;
```

Positive number:

```java
@Positive
private BigDecimal price;
```

Then:

```java
@PostMapping("/users")
public UserResponse create(
    @Valid @RequestBody UserCreateRequest request) {

}
```

Important distinction:

```text
@RequestBody
    ↓
Convert JSON → Java object

@Valid
    ↓
Validate Java object
```

---

# 19. FILE UPLOAD

For a file:

```java
@PostMapping(
    value = "/users/profile-image",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public String upload(
    @RequestParam("file") MultipartFile file) {

}
```

Request:

```text
POST /users/profile-image
Content-Type: multipart/form-data
```

File:

```text
profile.jpg
```

---

# 20. REQUIRED FILE

```java
@RequestParam("file")
MultipartFile file
```

The file is expected.

---

# 21. OPTIONAL FILE

```java
@RequestParam(
    value = "file",
    required = false
)
MultipartFile file
```

Now:

```text
file exists
```

or:

```text
file does not exist
```

are both valid.

---

# 22. MULTIPLE FILES

```java
@RequestParam("files")
List<MultipartFile> files
```

Request:

```text
files = image1.jpg
files = image2.jpg
files = image3.jpg
```

---

# 23. FILE + NORMAL FORM FIELDS

```java
@PostMapping(
    value = "/users/profile",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public String upload(
    @RequestParam String name,
    @RequestParam MultipartFile file) {

}
```

Request contains:

```text
name = John
file = profile.jpg
```

---

# 24. FILE + JSON

Very common in production.

```java
@PostMapping(
    value = "/products",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ProductResponse createProduct(
    @RequestPart("product")
    ProductCreateRequest request,

    @RequestPart("image")
    MultipartFile image) {

}
```

Request:

```text
product = {
    "name": "Burger",
    "price": 250
}

image = burger.jpg
```

---

# 25. MULTIPLE FILES + JSON

```java
@PostMapping(
    value = "/products",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ProductResponse createProduct(
    @RequestPart("product")
    ProductCreateRequest request,

    @RequestPart("images")
    List<MultipartFile> images) {

}
```

Useful for:

```text
Product
 ├── information
 ├── image 1
 ├── image 2
 └── image 3
```

---

# 26. PAGINATION

Your current endpoint already uses:

```java
Pageable pageable
```

Common request:

```text
GET /api/v1/all-users?page=0&size=20
```

Sorting:

```text
GET /api/v1/all-users?page=0&size=20&sort=name,asc
```

Multiple sorting:

```text
GET /api/v1/all-users
    ?page=0
    &size=20
    &sort=name,asc
    &sort=createdAt,desc
```

---

# 27. SEARCH + FILTER + PAGINATION

Production example:

```text
GET /api/v1/products
    ?search=burger
    &category=FAST_FOOD
    &minPrice=100
    &maxPrice=500
    &available=true
    &page=0
    &size=20
    &sort=price,asc
```

This is one of the most common API patterns.

---

# 28. PATH + QUERY + HEADER + BODY

A request can combine different types.

Example:

```text
PUT /api/v1/restaurants/25/products/100
    ?notifyCustomers=true
```

Headers:

```http
Authorization: Bearer JWT
Content-Type: application/json
```

Body:

```json
{
  "name": "Chicken Burger",
  "price": 300,
  "available": true
}
```

Spring can receive:

```java
@PathVariable Long restaurantId

@PathVariable Long productId

@RequestParam boolean notifyCustomers

@RequestHeader("Authorization") String authorization

@RequestBody ProductUpdateRequest request
```

This combination is extremely common in production systems.

---

# 29. PRODUCTION URL DESIGN

Good production APIs generally use **resources**, rather than action-heavy names.

Instead of:

```text
POST /create-user
```

a REST-style API commonly uses:

```text
POST /users
```

Instead of:

```text
POST /create-post
```

use:

```text
POST /posts
```

Instead of:

```text
GET /get-user/25
```

use:

```text
GET /users/25
```

Instead of:

```text
DELETE /delete-user/25
```

use:

```text
DELETE /users/25
```

The HTTP method already tells us the action.

---

# 30. COMMON PRODUCTION CRUD URLS

## Users

```text
POST   /api/v1/users
GET    /api/v1/users
GET    /api/v1/users/{userId}
PUT    /api/v1/users/{userId}
PATCH  /api/v1/users/{userId}
DELETE /api/v1/users/{userId}
```

---

## Authentication

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/logout
POST /api/v1/auth/refresh
POST /api/v1/auth/forgot-password
POST /api/v1/auth/reset-password
POST /api/v1/auth/verify-email
POST /api/v1/auth/verify-otp
```

---

# 31. USER-RELATED RESOURCES

```text
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/profile

GET /api/v1/users/{userId}/posts
POST /api/v1/users/{userId}/posts

GET /api/v1/users/{userId}/orders
GET /api/v1/users/{userId}/addresses
POST /api/v1/users/{userId}/addresses

GET /api/v1/users/{userId}/notifications
```

---

# 32. FOOD MARKETPLACE EXAMPLE

For a production-style food marketplace:

```text
GET  /api/v1/restaurants
POST /api/v1/restaurants

GET  /api/v1/restaurants/{restaurantId}
PUT  /api/v1/restaurants/{restaurantId}
DELETE /api/v1/restaurants/{restaurantId}
```

Restaurant foods:

```text
GET  /api/v1/restaurants/{restaurantId}/foods
POST /api/v1/restaurants/{restaurantId}/foods
```

Specific food:

```text
GET    /api/v1/foods/{foodId}
PUT    /api/v1/foods/{foodId}
PATCH  /api/v1/foods/{foodId}
DELETE /api/v1/foods/{foodId}
```

---

# 33. ORDERS

```text
POST /api/v1/orders
GET  /api/v1/orders
GET  /api/v1/orders/{orderId}
PATCH /api/v1/orders/{orderId}
```

Customer orders:

```text
GET /api/v1/users/{userId}/orders
```

Restaurant orders:

```text
GET /api/v1/restaurants/{restaurantId}/orders
```

Delivery orders:

```text
GET /api/v1/delivery-persons/{deliveryPersonId}/orders
```

---

# 34. ORDER STATUS

Instead of creating endpoints like:

```text
POST /complete-order
POST /cancel-order
POST /accept-order
```

a production API might use:

```text
PATCH /api/v1/orders/{orderId}
```

with:

```json
{
  "status": "CONFIRMED"
}
```

Or use specific action endpoints where the action has meaningful business semantics:

```text
POST /api/v1/orders/{orderId}/cancel
POST /api/v1/orders/{orderId}/confirm
POST /api/v1/orders/{orderId}/assign-delivery
```

The appropriate style depends on the business operation.

---

# 35. PAYMENTS

Common patterns:

```text
POST /api/v1/payments
GET  /api/v1/payments/{paymentId}
GET  /api/v1/orders/{orderId}/payment
POST /api/v1/payments/{paymentId}/refund
```

Payment history:

```text
GET /api/v1/users/{userId}/payments
```

---

# 36. DELIVERY

```text
GET  /api/v1/deliveries/{deliveryId}
POST /api/v1/orders/{orderId}/assign-delivery
PATCH /api/v1/deliveries/{deliveryId}/status
```

Location:

```text
POST /api/v1/deliveries/{deliveryId}/location
```

Example body:

```json
{
  "latitude": 23.8103,
  "longitude": 90.4125,
  "timestamp": "2026-09-22T10:30:00Z"
}
```

---

# 37. REVIEWS

```text
POST /api/v1/orders/{orderId}/reviews
GET  /api/v1/restaurants/{restaurantId}/reviews
PUT  /api/v1/reviews/{reviewId}
DELETE /api/v1/reviews/{reviewId}
```

---

# 38. ADMIN APIs

Common examples:

```text
GET /api/v1/admin/users
GET /api/v1/admin/restaurants
GET /api/v1/admin/orders
GET /api/v1/admin/payments
```

Approval:

```text
POST /api/v1/admin/restaurants/{restaurantId}/approve
POST /api/v1/admin/restaurants/{restaurantId}/reject
```

Analytics:

```text
GET /api/v1/admin/dashboard
GET /api/v1/admin/reports/sales
GET /api/v1/admin/reports/orders
```

---

# 39. THE MOST IMPORTANT URL PATTERNS TO REMEMBER

### Collection

```text
GET /users
```

Get many users.

### Create

```text
POST /users
```

Create user.

### Single resource

```text
GET /users/25
```

Get user 25.

### Replace/update

```text
PUT /users/25
```

Update user 25.

### Partial update

```text
PATCH /users/25
```

Partially update user 25.

### Delete

```text
DELETE /users/25
```

Delete user 25.

### Nested resource

```text
GET /users/25/orders
```

Get user's orders.

### Search/filter

```text
GET /products?category=food&available=true
```

### Pagination

```text
GET /products?page=0&size=20
```

### Sorting

```text
GET /products?sort=price,asc
```

### File

```text
POST /users/25/profile-image
```

with multipart data.

---

# 40. PUT vs PATCH

This is important for CRUD.

### PUT

Usually represents replacing/updating the resource representation.

```text
PUT /users/25
```

```json
{
  "name": "John",
  "email": "john@example.com",
  "phone": "123456789"
}
```

### PATCH

Usually represents a partial update.

```text
PATCH /users/25
```

```json
{
  "phone": "999999999"
}
```

You don't necessarily need to send every field.

---

# 41. Required vs Optional — Master Table

| Request type   | Required version                      | Optional version                              |
| -------------- | ------------------------------------- | --------------------------------------------- |
| Path variable  | `@PathVariable Long id`               | Usually use separate mapping                  |
| Query param    | `@RequestParam String name`           | `@RequestParam(required=false)`               |
| Query default  | —                                     | `@RequestParam(defaultValue="...")`           |
| Header         | `@RequestHeader("X")`                 | `@RequestHeader(value="X", required=false)`   |
| JSON body      | `@RequestBody DTO`                    | `@RequestBody(required=false) DTO`            |
| File           | `@RequestParam("file") MultipartFile` | `@RequestParam(value="file", required=false)` |
| Multipart part | `@RequestPart("file") MultipartFile`  | `@RequestPart(value="file", required=false)`  |
| DTO field      | `@NotNull`, `@NotBlank`, etc.         | No required validation annotation             |

---

# 42. One Production Request Can Look Like This

```text
PATCH
/api/v1/restaurants/25/orders/1001
?notifyCustomer=true
&page=0
```

Headers:

```http
Authorization: Bearer JWT
Content-Type: application/json
Accept: application/json
X-Device-ID: abc123
X-App-Version: 2.1.0
```

Body:

```json
{
  "status": "PREPARING",
  "estimatedReadyAt": "2026-09-22T12:30:00+06:00",
  "items": [
    {
      "foodId": 10,
      "quantity": 2
    }
  ]
}
```

This single request demonstrates:

```text
HTTP METHOD
    ↓
PATCH

PATH VARIABLES
    ↓
restaurantId = 25
orderId = 1001

QUERY PARAMETERS
    ↓
notifyCustomer = true
page = 0

HEADERS
    ↓
Authorization
Content-Type
Accept
Device ID
App Version

BODY
    ↓
JSON
 ├── String
 ├── Enum
 ├── Timestamp
 └── List<Object>
```

---

# 43. Mental Model To Memorize

Whenever you receive an API requirement, ask:

```text
1. What HTTP METHOD?
       ↓
2. Is there a RESOURCE ID?
       ↓
   @PathVariable

3. Is there filtering/search/pagination?
       ↓
   @RequestParam / Pageable

4. Does the server need metadata/auth information?
       ↓
   @RequestHeader

5. Is there structured data?
       ↓
   @RequestBody

6. Is there a file?
       ↓
   MultipartFile / @RequestPart

7. Which fields are required?
       ↓
   @Valid + validation annotations

8. Which fields are optional?
       ↓
   required=false / nullable DTO fields
```

---

# 44. Final Revision Cheat Sheet

```text
PATH
/users/25
       ↑
@PathVariable

QUERY
/users?page=0&size=20
      ↑
@RequestParam / Pageable

HEADER
Authorization: Bearer TOKEN
↑
@RequestHeader

BODY
{
  "name": "John"
}
↑
@RequestBody

FILE
profile.jpg
↑
MultipartFile

MULTIPART
file + JSON + fields
↑
@RequestPart / @RequestParam

VALIDATION
@NotNull
@NotBlank
@Email
@Size
@Positive
       ↑
@Valid
```

### Production CRUD pattern

```text
POST   /api/v1/users
GET    /api/v1/users
GET    /api/v1/users/{id}
PUT    /api/v1/users/{id}
PATCH  /api/v1/users/{id}
DELETE /api/v1/users/{id}
```

### Production nested-resource pattern

```text
GET  /api/v1/users/{userId}/orders
POST /api/v1/users/{userId}/orders
GET  /api/v1/restaurants/{restaurantId}/foods
POST /api/v1/restaurants/{restaurantId}/foods
```

### Production search pattern

```text
GET /api/v1/products
    ?search=burger
    &category=food
    &minPrice=100
    &maxPrice=500
    &available=true
    &page=0
    &size=20
    &sort=price,asc
```

**Core idea:**

> `@PathVariable` = **which resource?**
> `@RequestParam` = **how/filter?**
> `@RequestHeader` = **request metadata/auth?**
> `@RequestBody` = **what data?**
> `MultipartFile/@RequestPart` = **what file/part?**
> `@Valid` = **is the supplied data valid?**
