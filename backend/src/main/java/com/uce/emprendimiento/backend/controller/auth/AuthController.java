package com.uce.emprendimiento.backend.controller.auth;

import com.uce.emprendimiento.backend.dto.request.RegisterRequest;
import com.uce.emprendimiento.backend.dto.response.AuthResponse; // Asumo que tienes esto
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.security.CustomUserDetails; // Importa tu clase wrapper
import com.uce.emprendimiento.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@ModelAttribute RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("No estás autenticado");
        }

        User user = userDetails.getUser();

        user.setContrasena(null);

        return ResponseEntity.ok(user);
    }
}