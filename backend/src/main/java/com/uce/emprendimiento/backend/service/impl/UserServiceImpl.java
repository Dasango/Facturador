package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.dto.request.RegisterRequest;
import com.uce.emprendimiento.backend.dto.response.AuthResponse;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.UserRepository;
import com.uce.emprendimiento.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "user_signatures/";

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        if (userRepository.existsByCedula(request.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        User user = new User();
        user.setCedula(request.getCedula());
        user.setNombres(request.getNombres());
        user.setApellidos(request.getApellidos());
        user.setCorreo(request.getCorreo());
        user.setContrasena(passwordEncoder.encode(request.getContrasena()));

        userRepository.save(user);

        return new AuthResponse("Usuario registrado exitosamente", true);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        User user = getUserById(userId);

        // Actualizamos datos fiscales y personales
        user.setNombres(updatedUser.getNombres());
        user.setApellidos(updatedUser.getApellidos());
        user.setRuc(updatedUser.getRuc());
        user.setRazonSocial(updatedUser.getRazonSocial());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void uploadSignature(Long userId, MultipartFile file, String password) {
        if (file.isEmpty())
            throw new RuntimeException("El archivo está vacío");

        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = "user_" + userId + "_" + System.currentTimeMillis() + ".p12";
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            User user = getUserById(userId);
            user.setFirmaPath(dest.getAbsolutePath());
            user.setFirmaPassword(password);
            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la firma P12", e);
        }
    }

    @Override
    @Transactional
    public String uploadLogo(Long userId, MultipartFile file) {
        // Mantenemos tu Mock URL por ahora
        String mockUrl = "https://www.informador.mx/__export/1767450339821/sites/elinformador/img/2026/01/03/web_canva_-1-_version1767450310580.png_914869537.png";

        User user = getUserById(userId);
        user.setLogoPath(mockUrl);
        userRepository.save(user);

        return mockUrl;
    }
}