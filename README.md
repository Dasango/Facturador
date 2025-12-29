# Facturador - Guía de Inicio

Esta guía te ayudará a encender la aplicación completa (Base de datos, Backend y Frontend) y solucionar los problemas de visualización.

## Requisitos Previos

Asegúrate de tener instalado lo siguiente:
1.  **Docker Desktop**: Para la base de datos MySQL.
2.  **Java JDK 17+**: Para el backend.
3.  **Node.js** (Opcional pero recomendado): Para correr el frontend con URLs limpias (sin `.html`).

---

## Paso 1: Encender la Base de Datos

La base de datos corre en un contenedor de Docker.

1.  Abre una terminal en la carpeta raíz del proyecto (`Facturador`).
2.  Ejecuta el siguiente comando:
    ```powershell
    docker-compose up -d
    ```
    *Esto descargará la imagen de MySQL e iniciará la base de datos en el puerto 3307.*

## Paso 2: Encender el Backend (API)

El backend está hecho en Spring Boot (Java).

1.  En la terminal, entra a la carpeta `backend`:
    ```powershell
    cd backend
    ```
2.  Ejecuta la aplicación usando el wrapper de Maven (no necesitas instalar Maven manualmente):
    ```powershell
    .\mvnw spring-boot:run
    ```
    *Espera hasta ver un mensaje que diga "Started BackendApplication in..."*. El backend correrá usualmente en `http://localhost:8080`.

## Paso 3: Encender el Frontend

Aquí tienes dos opciones. La **Opción A** es la que soluciona tu problema de las URLs feas (ej. `.html`).

### Opción A: Usar Node.js (Recomendada para URLs limpias)
Esta opción elimina el `.html` de la URL y se ve más profesional.

1.  Asegúrate de estar en la carpeta raíz (`Facturador`).
2.  Ejecuta este comando (si tienes Node.js instalado):
    ```powershell
    npx serve frontend
    ```
3.  Te dará una URL local (ej. `http://localhost:3000`). Ábrela en tu navegador.
    *Nota: `serve` maneja automáticamente las rutas, ocultando el .html visualmente.*

### Opción B: Usar Live Server (VS Code)
Si prefieres usar la extensión de VS Code:

1.  Abre el archivo `frontend/index.html`.
2.  Haz clic derecho y selecciona **"Open with Live Server"**.
3.  **Truco para la URL**: En lugar de entrar a `.../index.html`, borra el `index.html` de la barra de direcciones en el navegador para quedarte solo con la raíz (ej. `http://127.0.0.1:5500/frontend/`).
    *Sin embargo, para navegar a otras páginas sin ver `.html`, la Opción A es mejor.*

---

## Solución de Problemas Comunes

-   **Error de puerto ocupado**: Si el puerto 3307 o 8080 está ocupado, asegúrate de no tener otros servicios corriendo.
-   **No se conecta a la BD**: Verifica que Docker esté corriendo (`docker ps`).
