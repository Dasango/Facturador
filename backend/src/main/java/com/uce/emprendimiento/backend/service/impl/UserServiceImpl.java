package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.dto.request.RegisterRequest;
import com.uce.emprendimiento.backend.dto.response.AuthResponse;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.UserRepository;
import com.uce.emprendimiento.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private static final String BUCKET_NAME = "facto-uploads";

    private String uploadToSupabase(MultipartFile file, String folder) throws IOException {
        String fileName = folder + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("Content-Type", file.getContentType());

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        // Supabase devuelve el objeto creado. Enviamos POST.
        restTemplate.postForEntity(uploadUrl, entity, String.class);

        // Retornamos la URL pública construida
        return supabaseUrl + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        if (userRepository.existsByRuc(request.getRuc())) {
            throw new IllegalArgumentException("El RUC ya está registrado");
        }

        try {
            User user = new User();
            // Datos Personales
            user.setNombres(request.getNombres());
            user.setApellidos(request.getApellidos());
            user.setCorreo(request.getCorreo());
            user.setContrasena(passwordEncoder.encode(request.getContrasena()));

            // Datos SRI
            user.setRuc(request.getRuc());
            user.setRazonSocial(request.getRazonSocial());
            user.setNombreComercial(request.getNombreComercial());
            user.setDireccionMatriz(request.getDireccionMatriz());
            user.setCodigoEstablecimiento(request.getCodigoEstablecimiento());
            user.setCodigoPuntoEmision(request.getCodigoPuntoEmision());
            user.setObligadoContabilidad(request.getObligadoContabilidad());
            user.setNroContribuyenteEspecial(request.getNroContribuyenteEspecial());

            // Archivos (Subida a Supabase desde Backend)
            if (request.getFirma() != null && !request.getFirma().isEmpty()) {
                String firmaUrl = uploadToSupabase(request.getFirma(), "firmas");
                user.setFirmaPath(firmaUrl);
            }

            if (request.getLogo() != null && !request.getLogo().isEmpty()) {
                String logoUrl = uploadToSupabase(request.getLogo(), "logos");
                user.setLogoPath(logoUrl);
            }

            user.setFirmaPassword(request.getFirmaPassword());

            userRepository.save(user);

            return new AuthResponse("Usuario registrado exitosamente", true);

        } catch (Exception e) {
            throw new RuntimeException("Error en el registro: " + e.getMessage(), e);
        }
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

        // Actualizamos SOLAMENTE datos permitidos (Correo y Dirección Matriz)
        // Los demás datos (RUC, Razón Social, Nombres, etc.) son fijos.
        if (updatedUser.getCorreo() != null && !updatedUser.getCorreo().isEmpty()) {
            user.setCorreo(updatedUser.getCorreo());
        }
        if (updatedUser.getDireccionMatriz() != null && !updatedUser.getDireccionMatriz().isEmpty()) {
            user.setDireccionMatriz(updatedUser.getDireccionMatriz());
        }

        // NO actualizamos Nombres, Apellidos, RUC, Razón Social aquí.
        // Se mantienen los originales de la base de datos.

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void uploadSignature(Long userId, MultipartFile file, String password) {
        if (file.isEmpty())
            throw new RuntimeException("El archivo está vacío");

        try {
            // Reutilizamos la lógica de Supabase (guardar en carpeta "firmas")
            String signatureUrl = uploadToSupabase(file, "firmas");

            User user = getUserById(userId);
            user.setFirmaPath(signatureUrl);
            user.setFirmaPassword(password);
            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la firma P12 en Supabase", e);
        }
    }

    @Override
    @Transactional
    public String uploadLogo(Long userId, MultipartFile file) {
        if (file.isEmpty())
            throw new RuntimeException("El archivo está vacío");

        try {
            // Reutilizamos la lógica de Supabase (guardar en carpeta "logos")
            String logoUrl = uploadToSupabase(file, "logos");

            User user = getUserById(userId);
            user.setLogoPath(logoUrl);
            userRepository.save(user);

            return logoUrl;

        } catch (IOException e) {
            throw new RuntimeException("Error al subir el logo a Supabase", e);
        }
    }
}