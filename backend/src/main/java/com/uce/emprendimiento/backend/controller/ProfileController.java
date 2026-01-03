package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    private static final String UPLOAD_DIR = "user_signatures/";

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setNombres(updatedUser.getNombres());
            user.setApellidos(updatedUser.getApellidos());
            user.setRuc(updatedUser.getRuc());
            user.setRazonSocial(updatedUser.getRazonSocial());
            userRepository.save(user);
            return ResponseEntity.ok("Perfil actualizado");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/upload-p12")
    public ResponseEntity<?> uploadP12(@PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password) {
        try {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Seleccione un archivo P12");
            }

            File dir = new File(UPLOAD_DIR);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = "user_" + userId + "_" + System.currentTimeMillis() + ".p12";
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            User user = userOpt.get();
            user.setFirmaPath(dest.getAbsolutePath());
            user.setFirmaPassword(password);
            userRepository.save(user);

            return ResponseEntity.ok("Firma P12 guardada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al guardar firma: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}/upload-logo")
    public ResponseEntity<?> uploadLogo(@PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            // MOCK BEHAVIOR: Use specific URL requested by user
            String mockUrl = "https://www.informador.mx/__export/1767450339821/sites/elinformador/img/2026/01/03/web_canva_-1-_version1767450310580.png_914869537.png";

            User user = userOpt.get();
            user.setLogoPath(mockUrl);
            userRepository.save(user);

            return ResponseEntity.ok(mockUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al subir logo: " + e.getMessage());
        }
    }
}
