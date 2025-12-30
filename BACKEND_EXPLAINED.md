# Guía Completa del Backend (Spring Boot)

Este documento detalla cómo funciona el backend de tu aplicación Facturador, explicando carpeta por carpeta y clase por clase qué hace cada componente.

## 📁 Estructura del Proyecto

El código fuente de Java se encuentra en `backend/src/main/java/com/uce/emprendimiento/backend`. Aquí explicaremos cada paquete:

### 1. `config` (Configuración)
Aquí se configura el comportamiento global de la aplicación.
*   **`SecurityConfig.java`**: Es el **cerebro de la seguridad**.
    *   Define quién puede entrar a qué páginas (`authorizeHttpRequests`).
    *   Configura el inicio de sesión (`formLogin`) y el manejo de errores.
    *   Habilita CORS (Cross-Origin Resource Sharing) para que tu frontend (HTML/JS) pueda comunicarse con el backend sin bloqueo.
    *   **Importante**: Aquí configuramos `BCryptPasswordEncoder` para encriptar las contraseñas, asegurando que no se guarden como texto plano.

### 2. `controller` (Controladores)
Es la **puerta de entrada**. Reciben las peticiones del usuario (desde el navegador/frontend).
*   **`AuthController.java`**: Maneja el registro (`/register`) y el login.
    *   Recibe un JSON con los datos del usuario, llama al `UserService` para procesarlo y devuelve una respuesta.
*   **Anotaciones clave**:
    *   `@RestController`: Indica que esta clase maneja peticiones web y responde con datos (JSON), no con vistas HTML.
    *   `@RequestMapping`: Define la ruta base (ej. `/api/auth`).

### 3. `dto` (Data Transfer Objects)
Son "cajas" simples para transportar datos entre el frontend y el backend. No son tablas de base de datos.
*   `request.RegisterRequest`: Datos que envía el usuario al registrarse (cédula, correo, contraseña).
*   `response.AuthResponse`: Datos que devolvemos al usuario (mensaje de éxito/error, token si usáramos JWT).

### 4. `entity` (Entidades)
Representan las **tablas de tu base de datos**.
*   **`User.java`**: Mapea la tabla de usuarios. Cada campo de la clase (id, cedula, correo) es una columna en la base de datos.
*   **Anotaciones clave**:
    *   `@Entity`: Convierte la clase en una tabla.
    *   `@Id` y `@GeneratedValue`: Definen la llave primaria autogenerada.

### 5. `repository` (Repositorios)
Es la capa de **acceso a datos**. Spring Boot hace la magia aquí.
*   **`UserRepository.java`**: Interfaz que hereda de `JpaRepository`.
    *   ¡No tienes que escribir SQL! Al llamar a métodos como `findByCorreo` o `save`, Spring genera el SQL automáticamente tras bambalinas.

### 6. `service` (Servicios)
Aquí vive la **lógica de negocio**. Es el cerebro que piensa qué hacer con los datos.
*   **`UserService.java`** (Interfaz) y **`UserServiceImpl.java`** (Implementación):
    *   Verifica si el correo o cédula ya existen.
    *   **Encripta la contraseña** antes de guardarla (usando el cambio que acabamos de hacer).
    *   Llama al `UserRepository` para guardar el usuario en la BD.
*   **Anotaciones clave**:
    *   `@Service`: Indica a Spring que esta clase contiene lógica de negocio.

### 7. `security` (Seguridad Personalizada)
*   **`CustomUserDetailsService.java`**: Es el puente entre tu base de datos y Spring Security.
    *   Cuando alguien intenta loguearse, Spring llama a esta clase y le dice: "Búscame al usuario X".
    *   Esta clase busca en `UserRepository` y, si encuentra al usuario, se lo entrega a Spring Security para que verifique la contraseña.

---

## 🔄 Flujo de una Petición (Ejemplo: Registro)

1.  **Frontend**: El usuario llena el formulario y envía un POST a `/api/auth/register` con sus datos.
2.  **Controller (`AuthController`)**: Recibe la petición y pasa los datos al `UserService`.
3.  **Service (`UserServiceImpl`)**:
    *   Valida que no exista el usuario.
    *   Hashea la contraseña (ej. convierte "123456" en `$2a$10$XyZ...`).
    *   Llama a `UserRepository.save()`.
4.  **Repository (`UserRepository`)**: Inserta el registro en la base de datos.
5.  **Respuesta**: El controlador devuelve un "Usuario registrado exitosamente" al frontend.

## 🔑 Conceptos Clave de Spring Boot

*   **Inyección de Dependencias (`@Autowired`)**: En lugar de hacer `new Service()`, le pides a Spring que te "inyecte" una instancia lista para usar. Es como pedirle un taladro a un asistente en lugar de ir a comprar uno.
*   **Inversión de Control (IoC)**: Spring maneja el ciclo de vida de los objetos (Beans). Tú solo defines qué hacen, Spring decide cuándo crearlos y destruirlos.
