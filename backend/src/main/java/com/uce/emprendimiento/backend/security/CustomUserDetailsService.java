package com.uce.emprendimiento.backend.security;

import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Log setup
        System.out.println("Attempting to load user: " + username);

        // Try finding by correo first
        // If not found, try by cedula
        User user = userRepository.findByCorreo(username)
                .or(() -> userRepository.findByCedula(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email or cedula: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getCorreo(), // Use unique identifier for session if needed, or keep correo
                user.getContrasena(),
                new ArrayList<>() // Authorities
        );
    }
}
