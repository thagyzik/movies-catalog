# Movies Catalog API

**A RESTful API built with Spring Boot 2.7.18, designed to manage a catalog of movies. The system supports:**

- CRUD operations for movies

- Filtering and searching movies with dynamic queries

- JWT-based authentication and authorization

- Role-based access control (USER / ADMIN)

- Movie ratings by users

- File upload for movie images

- Swagger UI documentation with custom configuration

## Features

- Authentication & Authorization

- JWT token-based login

- Roles: USER and ADMIN

- Secured endpoints using @PreAuthorize

- Movie Management

  - Add, update, delete, and fetch movies

- Upload optional image for each movie (multipart/form-data)

- Movies can be rated by users

- Users can rate movies (1 per user per movie)

- Retrieve and delete personal ratings

- Search Functionality

  - Filter movies by name, category, synopsis, year

  - Sort by different fields, including rating (via custom logic)

  - Pagination and sorting handled via Pageable

  - Redis caching enabled for search results (TTL: 7 days)

- Swagger UI (/swagger-ui.html)

- Fully documented endpoints using SpringDoc OpenAPI

  - Grouped tags per controller

- JWT Authentication configured

- Custom schemas for request/response DTOs

- Descriptions and examples provided

### Postman Collection

[Link to download postman collection from Drive](https://drive.google.com/file/d/1tVa_lv6hZ9vMm878FsQwFv3RISagwuN9/view?usp=drive_link)

## Tech Stack

- Java 11

- Spring Boot 2.7.18

- Spring Security

- JWT (JSON Web Token)

- Spring Data JPA

- Hibernate Validator

- PostgreSQL (database)

- Redis (cache)

- Swagger/OpenAPI (documentation)

## Setup Instructions

Clone the repository:

```sh
git clone https://github.com/thagyzik/movies-catalog.git
```

Run Docker containers (It will start PostgreSQL and Redis):
```sh
docker-compose -f docker-compose.yml -f docker-compose.redis.yml up -d
```

Run the app:
```sh
mvc clean install
```

Once project is started, PostgreSQL will have tables populated with initial data from `data.sql`.

## Swagger Access

Once the app is running:

http://localhost:8080/swagger-ui.html

To authenticate:

Use /auth/login with valid credentials to get a JWT token.

Click "Authorize" in Swagger UI and paste the token as:

Bearer <your-token>

### Example Credentials

Admin:
Email: admin@gmail.com
Password: admin123!

User:
Email: user@gmail.com
Password: user123!

### Swagger Limitation

- Some endpoints such as addMovie or updateMovie expect multipart/form-data with both JSON and file upload.

- Swagger UI does not support this combination well.

## Endpoints Summary

`Auth`

POST /auth/login — Authenticate and get JWT

`Users`

POST /users/registerUser — Register a new user

PUT /users/updateRole/{username}/{newRole} — Change user role (ADMIN only)

`Movies`

POST /movies/addMovie — Add movie with optional image (ADMIN only)

PUT /movies/updateMovie/{id} — Update movie and/or image (ADMIN only)

DELETE /movies/deleteMovie/{id} — Delete movie (ADMIN only)

POST /movies/search — Search with filters (authenticated)

GET /movies/{id} — Get movie by ID (authenticated)

`Ratings`

POST /movies/ratings/ — Rate a movie (authenticated)

DELETE /movies/ratings/{movieId} — Delete rating for movie (authenticated)

GET /movies/ratings/byUser — Get logged-in user's ratings
