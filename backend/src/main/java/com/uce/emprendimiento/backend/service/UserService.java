package com.uce.emprendimiento.backend.service;

import com.uce.emprendimiento.backend.dto.request.RegisterRequest;
import com.uce.emprendimiento.backend.dto.response.AuthResponse;

public interface UserService {
    AuthResponse register(RegisterRequest request);
}
