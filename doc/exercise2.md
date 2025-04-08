# Technical Documentation for ToDo List Application (Version 1.1.0)

## 📘 Overview

This documentation outlines the technical progress of the ToDo List web application during the development of version 1.1.0. Built using Spring Boot and Thymeleaf, this application now includes several improvements and new features. The purpose of this document is to assist the development team by detailing the architectural evolution, new features, implemented classes and templates, as well as the relevant testing strategies.

---

## ✅ Features Implemented

### 🔒 Mandatory Features

- **🔗 Menu Bar**: Responsive Bootstrap-based navigation bar, present on all pages (except login and registration). It dynamically adapts based on user login status and includes links to:
    - `About` page
    - `Tasks` (user's to-do list)
    - Dropdown with user account options (Account and Logout)

- **📋 User Listing Page**:
    - Available at `/registered`
    - Lists all registered users (ID and email)
    - Accessible only to admin users
    - Includes functionality to block/unblock users, excluding the admin themselves

### ⭐ Optional Features

- **👤 User Description Page**:
    - URL: `/registered/{id}`
    - Displays all user data except for the password

- **🔧 Admin User Registration**:
    - Admin checkbox is shown only if no admin exists
    - After the first admin registers, only regular users can be registered

- **🚫 User Blocking by Admin**:
    - Admin can enable/disable user access
    - Blocked users are prevented from logging in

---

## 🧱️ Backend Architecture

### 📂 Controllers

- **`HomeController`**
    - `getHomePage()` → Maps the root path (`/`) to return the `home.html` page. It serves as a welcome or landing page and doesn't involve business logic.

- **`LoginController`**
    - `getLoginPage()` → Renders the login form page and displays any error messages
    - `loginUser()` → Validates credentials, manages user sessions, redirects accordingly

- **`TareaController`**
    - `createTarea()` → Accepts task form data and persists it using `TareaService`
    - `getTareaList()` → Retrieves and displays tasks for the logged-in user

- **`UsuarioController`**
    - `getUserProfile()` → Shows profile details for the authenticated user
    - `updateUser()` → Updates user data

- **`AdminController`**
    - `getUserList()` → Retrieves all users for display in admin panel
    - `blockUser(Long id)` → Toggles user status (active/inactive)

### ⚙️ Service Classes

- **`UsuarioService`**
    - `registerUser()` → Validates inputs and handles unique admin creation logic
    - `findUserById()` → Retrieves a user by ID
    - `blockUser()` → Toggles a user's enabled state

- **`TareaService`**
    - `addTarea()` → Adds a new task to the repository
    - `getAllTareas()` → Retrieves all tasks linked to a specific user

---

## 📦 DTOs (Data Transfer Objects)

| DTO            | Purpose                                                  |
|----------------|----------------------------------------------------------|
| `LoginData`    | Transfers login credentials from client to backend       |
| `RegistroData` | Used during user registration (email, name, password)    |
| `TareaData`    | Encapsulates task info (ID, title, description, deadline)|
| `UsuarioData`  | Holds user information (ID, email, name, password*)      |

> Note: Password is excluded from responses for security.

---

## 📄 Thymeleaf Templates

| Template              | Description                                                      |
|-----------------------|------------------------------------------------------------------|
| `formLogin.html`      | Login form UI and error display                                 |
| `formRegistro.html`   | User registration UI with optional admin checkbox               |
| `tasksList.html`      | Lists tasks with edit/delete capabilities                       |
| `about.html`          | Project information and credits                                 |
| `menuBar.html`        | Reusable navigation component                                   |
| `userList.html`       | Admin panel with user info and block/unblock options            |
| `userDescription.html`| Shows user detail excluding sensitive data                      |

---

## 🧪 Testing Strategy

### 🔍 Unit Tests

| Test Class                      | Functionality                                                                 |
|--------------------------------|------------------------------------------------------------------------------|
| `AboutPageTest`                | Ensures correct rendering of About page                                      |
| `AdminLoginTest`               | Admin login logic and redirection                                            |
| `UsuarioServiceTest`           | Validates registration, login, blocking, and edge cases                     |
| `TareaServiceTest`             | Verifies task creation, retrieval, editing, and deletion                     |
| `AdminUserBlockControllerTest` | Admin block logic, with checks against self-blocking                         |
| `NavbarTests`                  | Navbar visibility based on authentication status                             |
| `UserDescriptionValidationTest`| User profile detail validation                                               |
| `UserListWebTest`              | Tests rendering of admin user list                                           |

### ⚖️ Integration Tests

| Test Class                        | Functionality                                                                  |
|----------------------------------|---------------------------------------------------------------------------------|
| `AdminUserBlockIntegrationTest`   | Validates DB consistency when toggling user status                            |
| `UserRegistrationIntegrationTest` | End-to-end check for new user registration and login                          |
| `TareaIntegrationTest`            | Task lifecycle validation: create, update, retrieve                           |

---

## 📌 Highlighted Code Example

```java
// Modified registration to validate single administrator
@Transactional // Indicates that this method runs within a database transaction
public UsuarioData registrar(UsuarioData usuario) {
    // Searches the database for a user with the same email
    Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());

    // If the user already exists, throws an exception indicating the email is already registered
    if (usuarioBD.isPresent()) {
        throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
    }
    // If the user's email is null, throws an exception indicating the email is required
    else if (usuario.getEmail() == null) {
        throw new UsuarioServiceException("El usuario no tiene email");
    }
    // If the user's password is null, throws an exception indicating the password is required
    else if (usuario.getPassword() == null) {
        throw new UsuarioServiceException("El usuario no tiene password");
    }
    else {
        // Converts the UsuarioData object to a Usuario entity using ModelMapper
        Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);

        // Sets the "enabled" status of the user to true (active)
        usuarioNuevo.setEnabled(true);

        // 💡 Highlight: this block prevents registering more than one admin
        // Checks if the new user is an admin and if an admin already exists in the database
        if (usuarioNuevo.isAdmin() && usuarioRepository.existsByAdmin(true)) {
            // If an admin already exists, throws an exception indicating another cannot be registered
            throw new UsuarioServiceException("Ya existe un administrador registrado");
        }

        // Saves the new user in the database
        usuarioNuevo = usuarioRepository.save(usuarioNuevo);

        // Converts the saved Usuario entity back to UsuarioData and returns it
        return modelMapper.map(usuarioNuevo, UsuarioData.class);
    }
}
```
> 🔎 **Note**: This code is located in the file `UsuarioService.java`.

### 🔍 Explanation
- Ensures **email uniqueness**, **non-null password**, and **prevents multiple admin accounts**.
- If conditions are met, the user is saved and returned.
- Encapsulated in a `@Transactional` context to ensure DB integrity.
  
> The highlighted block ensures that **only one administrator** can exist in the system, avoiding duplicate admin registrations.
---

## 🔗 External Resources

- **GitHub Repository**: [https://github.com/acoves/p2-todolist-app-ATSD](https://github.com/acoves/p2-todolist-app-ATSD)
- **DockerHub Image**: [https://hub.docker.com/r/acoves/p2-todolistapp](https://hub.docker.com/r/acoves/p2-todolistapp)
- **Trello**: [https://trello.com/invite/b/67e275084f990f292deb22ad/ATTId1d9bb29fd24e1f08359a3d9bf56dde546F9F226/p2-to-do-list-app](https://trello.com/invite/b/67e275084f990f292deb22ad/ATTId1d9bb29fd24e1f08359a3d9bf56dde546F9F226/p2-to-do-list-app)
---

## ✅ Summary

Version 1.1.0 introduces all core and optional requirements while ensuring best practices through clear architectural separation and full test coverage. The use of Thymeleaf, DTOs, and RESTful controllers streamlines development and improves maintainability. This documentation equips team members with a comprehensive guide to understand and extend the project with confidence.

