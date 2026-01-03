# Explicación SUPER DETALLADA del Backend (Para Principiantes)

¡Hola! Entiendo perfectamente. Vamos a olvidarnos de los términos técnicos complicados y vamos a explicar esto como si estuviéramos construyendo una casa o un restaurante. No daré por hecho nada.

Este documento explica **TODO** lo que hay en tu carpeta `backend`.

---

## 1. Conceptos Básicos (Antes de empezar)

Imagina un RESTAURANTE:
1.  **El Cliente (Frontend/Navegador)**: Eres tú sentado en la mesa. Tienes hambre (necesitas datos) o quieres pedir algo (enviar datos).
2.  **El Mesero (Controller)**: Es quien se acerca a tu mesa. Tú le dices "Quiero una hamburguesa" (Petición). Él anota la orden y se la lleva a la cocina. Él NO cocina, solo recibe pedidos y entrega platos.
3.  **El Chef (Service)**: Está en la cocina. Recibe la nota del mesero. Él sabe la receta, sabe cuánto tiempo cocinar la carne, sabe si falta sal. Es el "cerebro" que procesa todo.
4.  **La Despensa (Repository)**: Es el almacén donde están los ingredientes (Datos). El Chef no crea la carne de la nada, va a la despensa y la saca. La despensa es la conexión con la Base de Datos.
5.  **El Ingrediente (Entity)**: Es la definición de qué es una "Carne". ¿Es de res? ¿De pollo? ¿Cuánto pesa? En código, esto son tus datos (Usuario, Producto).

En tu código, todo está organizado siguiendo este ejemplo del restaurante.

---

## 2. El Mapa (Las Carpetas)

Dentro de `src/main/java/com/uce/emprendimiento/backend` verás varias carpetas. Aquí está qué es cada una:

*   **`config`**: Son las **Reglas de la Casa**. ¿A qué hora abrimos? ¿Quién puede entrar? (Aquí está la seguridad).
*   **`controller`**: Son los **Meseros**. Aquí están las clases que reciben las peticiones de tu página web.
*   **`entity`**: Son los **Moldes**. Aquí definimos cómo es un "Usuario", cómo es una "Factura".
*   **`repository`**: Es el **Almacenero**. El código que sabe hablar con la base de datos para guardar y sacar cosas.
*   **`service`**: Son los **Cocineros**. Aquí está la lógica real (ej: "Antes de registrar al usuario, revisa que la cédula sea válida").
*   **`dto`**: Son los **Platos**. A veces no quieres servir la carne cruda (Entidad completa con contraseña), sino cocinada y bonita en un plato. Los DTOs son paquetes de datos listos para enviar o recibir.

---

## 3. Análisis LÍNEA POR LÍNEA

Vamos a abrir los archivos más importantes y te explicaré qué hace cada línea rara.

### A. El Inicio: `BackendApplication.java`

Este es el interruptor de encendido.

```java
@SpringBootApplication // <- Hechizo mágico: Dice "Esto es una app de Spring Boot, configúrate sola".
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args); // <- ¡BOOM! Arranca el motor.
    }
}
```
*   Cuando le das "Run", Java busca este método `main` y empieza todo.

### B. Los Moldes: `entity/User.java`

Aquí definimos qué diablos es un "Usuario".

```java
@Entity // <- Dice: "Crea una TABLA en la base de datos para esto".
@Table(name = "usuarios") // <- La tabla se llamará "usuarios".
public class User {

    @Id // <- Esta es la Clave Única (como el número de ticket).
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <- Dice: "Base de datos, invéntate el número tú (1, 2, 3...)".
    private Long id;

    @Column(nullable = false, unique = true) // <- Dice: "No puede estar vacío y NO puede repetirse".
    private String cedula;

    private String contrasena; // <- Aquí guardaremos la clave (encriptada, espero).
    
    // ... más campos ...
}
```

### C. El Almacenero: `repository/UserRepository.java`

Esto es magia negra de Spring. Fíjate que está casi vacío.

```java
@Repository // <- Dice: "Soy un almacén".
public interface UserRepository extends JpaRepository<User, Long> {
    // ¡NO HAY CÓDIGO AQUÍ!
    
    Optional<User> findByCorreo(String correo); // <- Solo escribiendo esto, Spring crea AUTOMÁTICAMENTE el código SQL: "SELECT * FROM usuarios WHERE correo = ?"
}
```
*   `extends JpaRepository`: Significa "Hereda todos los poderes básicos". Automáticamente ya tienes métodos para `guardar`, `borrar`, `buscarTodos`. No tienes que escribirlos.
*   `findByCorreo`: Spring es tan inteligente que lee el nombre del método y sabe qué buscar.

### D. El Mesero: `controller/AuthController.java`

Aquí recibimos a la gente que se quiere registrar o loguear.

```java
@RestController // <- Dice: "Soy un Mesero Inteligente (API)". Respondo con DATOS (JSON), no con páginas web.
@RequestMapping("/api/auth") // <- "Atiendo en la mesa /api/auth".
public class AuthController {

    @Autowired // <- IMPORTANTE: Dice "Spring, búscame al Cocinero (UserService) y dámelo listo para usar". No tengo que crearlo yo con 'new'.
    private UserService userService;

    @PostMapping("/register") // <- "Si alguien viene con una carta (POST) a /api/auth/register..."
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // @RequestBody: "Toma lo que viene dentro del sobre de la carta y conviértelo en un objeto Java 'request'".
        
        AuthResponse response = userService.register(request); // <- "Cocinero, toma este pedido".
        return ResponseEntity.ok(response); // <- "Aquí tiene su respuesta, cliente".
    }
}
```

### E. Las Reglas: `config/SecurityConfig.java`

El portero de la discoteca. Posiblemente el archivo más difícil de entender.

```java
@Bean // <- "Spring, guarda esto en tu caja de herramientas para usarlo luego".
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // <- Desactiva una protección antigua que molesta en las APIs modernas.
        .authorizeHttpRequests(auth -> auth
            // .requestMatchers(...): "A estas rutas..."
            .requestMatchers("/api/auth/**", "/index.html", "/login").permitAll() // <- "...DEJA PASAR A TODO EL MUNDO".
            .anyRequest().authenticated() // <- "Para CUALQUIER OTRA COSA, pídeles identificación (Loguearse)".
        );
    return http.build();
}
```
*   Si intentas entrar a `/api/products` (ver productos) sin loguearte, este portero te detendrá porque no está en la lista de `permitAll`.

---

## 4. La Historia Completa: ¿Qué pasa cuando te registras?

Imagina que Juan entra a tu web y llena el formulario de registro:

1.  **Navegador (Cliente)**: Empaqueta los datos de Juan (`juan@email.com`, `123456`) en un paquete JSON. Lo envía a la dirección `http://localhost:8080/api/auth/register`.
2.  **`SecurityConfig` (Portero)**: Ve la petición. Mira su lista. "¿La ruta `/api/auth/register` es pública?". ¡SÍ! Adelante, pasa.
3.  **`AuthController` (Mesero)**: Recibe el paquete.
    *   Usa `@RequestBody` para abrir el paquete y leer los datos.
    *   Llama al **Cocinero** (`UserService`) y le dice "Oye, regístrame a este tipo".
4.  **`UserServiceImpl` (Cocinero)**:
    *   Piensa: "¿Ya existe este correo?". Llama al **Almacenero** (`UserRepository`).
    *   **Almacenero**: "Déjame ver... no, no está en los estantes".
    *   **Cocinero**: "Ok. Ahora, la contraseña es '123456'. No puedo guardarla así, es peligroso. Voy a picarla (Encriptar)". La convierte en `$2a$10$f9...`.
    *   **Cocinero**: Crea una nueva ficha de Usuario.
    *   **Cocinero**: "Almacenero, guarda esto".
5.  **`UserRepository` (Almacenero)**: Escribe en la Base de Datos (PostgreSQL).
6.  **`AuthController` (Mesero)**: Recibe el "OK" del cocinero. Escribe una nota "Éxito" y se la entrega al navegador.
7.  **Navegador**: Recibe el mensaje y le muestra a Juan: "¡Registro Exitoso!".

---

## 5. Resumen de Anotaciones Mágicas

Guárdate esta lista, es tu diccionario:

*   `@SpringBootApplication`: "Aquí empieza todo".
*   `@Entity`: "Esto es una tabla en la BD".
*   `@Repository`: "Esto habla con la BD".
*   `@Service`: "Aquí están los cálculos y reglas".
*   `@RestController`: "Esto recibe pedidos de Internet".
*   `@Autowired`: "Conecta esto automáticamente por mí".
*   `@GetMapping` / `@PostMapping`: "Atiende peticiones de lectura / escritura".
*   `@RequestBody`: "Lee lo que viene en el mensaje".

¡Espero que esto sea mucho más claro! Es normal sentirse perdido al principio, Spring hace muchas cosas "mágicas" por detrás (la Inyección de Dependencias), pero una vez entiendes que son solo piezas de Lego conectándose solas, todo tiene sentido.
