package com.uce.emprendimiento.backend.controller.auth;

import com.uce.emprendimiento.backend.dto.request.RegisterRequest;
import com.uce.emprendimiento.backend.dto.response.AuthResponse;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.UserRepository;
import com.uce.emprendimiento.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        // Principal is usually the username (cedula or correo)
        String username = authentication.getName();

        // Try finding by cedula or correo
        Optional<User> userOpt = userRepository.findByCedula(username);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByCorreo(username);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Clear password just in case, though DTO is better
            user.setContrasena(null);
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.notFound().build();
    }
}
