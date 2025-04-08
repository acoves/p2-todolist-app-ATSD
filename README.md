# 📝 ToDo List App (v1.1.0)

Welcome to the **ToDo List App**, a task management application built using **Spring Boot**, **Thymeleaf**, and **Bootstrap**, following a layered architecture and test-driven methodology.

---

## 🚀 Features

### Main Features
- Secure login and user registration.
- Dynamic navigation bar depending on user session.
- Create, view, edit, and delete personal tasks.
- Admin-only access to user listing and control panel.

### Advanced Features
- User detail page (excluding password).
- Admin-controlled user blocking/unblocking.
- Unique admin policy (only one admin can exist).

---

## 📁 Project Architecture

- **Controllers**: Handle routing and HTTP logic (`Home`, `Login`, `Tarea`, `Usuario`, `Admin`).
- **Services**: Encapsulate business logic (`UsuarioService`, `TareaService`).
- **DTOs**: Transfer data between layers cleanly and securely.
- **Templates**: Thymeleaf templates with Bootstrap styling.
- **Persistence**: JPA + Hibernate with H2 or MySQL.

---

## 🔧 Running the Application

### Requirements
- Java 8 SDK

### Execute from Maven:
```bash
$ ./mvn spring-boot:run
```

### Or build and run the JAR:
```bash
$ ./mvn package
$ java -jar target/todolist-inicial-0.0.1-SNAPSHOT.jar
```

Once running, open your browser at:
[http://localhost:8080/login](http://localhost:8080/login)

---


## 🔮 Testing

Includes unit and integration tests for:
- User registration and login
- Task lifecycle
- Admin permissions and blocking

---

## 📄 Documentation

Detailed documentation available at `/doc/exercise2.md` including class lists, test coverage, templates, and service explanation.

---

## 🌐 Useful Links

- GitHub: [github.com/acoves/p2-todolist-app-ATSD](https://github.com/acoves/p2-todolist-app-ATSD)
- DockerHub: [hub.docker.com/r/acoves/p2-todolistapp](https://hub.docker.com/r/acoves/p2-todolistapp)
- Trello: [Project Board](https://trello.com/invite/b/67e275084f990f292deb22ad/ATTId1d9bb29fd24e1f08359a3d9bf56dde546F9F226/p2-to-do-list-app)

---

**Version:** 1.1.0  
**Author:** Alejandro Coves Bolaños  
**Course:** Técnicas Ágiles de Desarrollo de Software (ATSD)