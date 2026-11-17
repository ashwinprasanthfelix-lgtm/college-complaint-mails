# Campus Complaint Management System

Java (Spring Boot) + Thymeleaf + JDBC. Uses H2 (Oracle mode) by default, Oracle-ready.

## Run (needs only JDK 17 + Maven)
    mvn spring-boot:run
Open http://localhost:8080

- Student: register at /register, then log in at /login
- Admin: http://localhost:8080/admin/login  ->  username `admin`, password `admin123`
- DB viewer: http://localhost:8080/h2-console
  (JDBC URL `jdbc:h2:file:./data/complaintdb;MODE=Oracle`, user `sa`, empty password)

## Features (per problem statement)
Student: register, login, submit complaint (category + description), view own complaints,
edit / delete while status is Pending, update profile.
Admin: login, view all complaints, update status + priority, search by ID / category / status,
view and delete students.

## Switching to Oracle
Run `sql/schema.sql` in Oracle and use the commented Oracle lines at the bottom of
`src/main/resources/application.properties` (also set `spring.sql.init.mode=never`).
