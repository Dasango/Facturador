package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.security.CustomUserDetails;
import com.uce.emprendimiento.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user") // Cambiado a /api/user para ser consistente
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET: Obtener MI perfil
    @GetMapping("/profile")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        User user = userService.getUserById(userDetails.getUser().getId());

        // Seguridad: Limpiamos secretos antes de enviar al frontend
        user.setContrasena(null);
        user.setFirmaPassword(null);

        return ResponseEntity.ok(user);
    }

    // PUT: Actualizar MI perfil
    @PutMapping("/profile")
    public ResponseEntity<User> updateMyProfile(@RequestBody User updatedUser,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        User savedUser = userService.updateUser(userDetails.getUser().getId(), updatedUser);
        return ResponseEntity.ok(savedUser);
    }

    // POST: Subir Firma
    @PostMapping("/upload-p12")
    public ResponseEntity<?> uploadP12(@RequestParam("file") MultipartFile file,
            @RequestParam("password") String password,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        try {
            userService.uploadSignature(userDetails.getUser().getId(), file, password);
            return ResponseEntity.ok("{\"message\": \"Firma guardada correctamente\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // POST: Subir Logo
    @PostMapping("/upload-logo")
    public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        try {
            String url = userService.uploadLogo(userDetails.getUser().getId(), file);
            return ResponseEntity.ok("{\"url\": \"" + url + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}