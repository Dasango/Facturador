package com.uce.emprendimiento.backend.service;

import com.uce.emprendimiento.backend.dto.request.RegisterRequest;
import com.uce.emprendimiento.backend.dto.response.AuthResponse;
import com.uce.emprendimiento.backend.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    User getUserById(Long userId);

    User updateUser(Long userId, User updatedUser);

    void uploadSignature(Long userId, MultipartFile file, String password);

    String uploadLogo(Long userId, MultipartFile file);
}