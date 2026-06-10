package com.example.userlistapp.api;

import com.example.userlistapp.model.User;
import com.example.userlistapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserRepository userRepository;

    public UserApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /api/users — все пользователи
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET /api/users/{id} — пользователь по ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users/filter?role=Менеджер&keyword=Иван
    @GetMapping("/filter")
    public List<User> getUsersByFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {

        if (keyword != null && !keyword.isBlank()) {
            return userRepository.findByUsernameContainingIgnoreCase(keyword);
        } else if (role != null && !role.isBlank()) {
            return userRepository.findByRole(role);
        } else {
            return userRepository.findAll();
        }
    }

    // POST /api/users — создать пользователя
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userRepository.save(user);
        return ResponseEntity.status(201).body(saved);
    }

    // PUT /api/users/{id} — полное обновление пользователя
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User user) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        return ResponseEntity.ok(userRepository.save(user));
    }

    // PATCH /api/users/{id}/role — обновить только роль
    @PatchMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id,
                                               @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            String newRole = body.get("role");
            if (newRole != null && !newRole.isBlank()) {
                user.setRole(newRole);
                userRepository.save(user);
            }
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/users/{id} — удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}