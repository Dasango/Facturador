package com.uce.emprendimiento.backend.config;

import com.uce.emprendimiento.backend.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 1. ZONA PÚBLICA AMPLIADA (Para evitar el bucle infinito)
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login",
                                "/signup",
                                "/pages/signup.html", // Permitir acceso al recurso estático forwarded
                                "/assets/**", // Tus estilos e imágenes
                                "/partials/**", // Tus fragmentos HTML
                                "/api/auth/**", // Endpoints de autenticación

                                // IMPORTANTE: Permitir archivos sueltos para que no bloquee y cause redirect
                                "/*.ico", // favicon
                                "/*.css", // css sueltos
                                "/*.js", // js sueltos
                                "/*.png",
                                "/*.json")
                        .permitAll()

                        // 2. ZONA VIP
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        // Cuando alguien intenta entrar a /profile sin permiso,
                        // Spring lo mandará a "/" que es tu Landing/Login.
                        .loginPage("/")

                        // Spring intercepta el POST que viene de tu formulario
                        .loginProcessingUrl("/login")

                        // Respuestas JSON para que tu JavaScript funcione bonito
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": true, \"message\":\"Login exitoso\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter()
                                    .write("{\"success\": false, \"message\":\"Credenciales incorrectas\"}");
                        })
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }
}