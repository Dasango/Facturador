package com.uce.emprendimiento.backend.security;

import com.uce.emprendimiento.backend.entity.User; // Tu entidad real
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user; // Guardamos tu entidad completa aquí

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Este método es clave: nos permite recuperar la entidad real luego
    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getContrasena();
    }

    @Override
    public String getUsername() {
        return user.getCorreo(); // O el campo que uses para login
    }

    // Métodos obligatorios de la interfaz (puedes dejarlos así por ahora)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
}