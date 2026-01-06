# FACTO 🧾

> Facturador electrónico funcional con el SRI.

Este proyecto es una solución de facturación diseñada para cumplir con los requisitos del SRI, simplificando el proceso de emisión de comprobantes.

---

## 👥 Equipo de Desarrollo

**⚠️ TAREA:** Poner bien sus nombres aquí abajo.

* Cristian Baraja
* David Sango
* Jhon Córdova
* Jonathan Suarez

---

## 🚀 Pasos para comenzar a trabajar

Sigue esta guía para levantar el entorno de desarrollo localmente.

### 1. Clonar el repositorio

Abre tu terminal y ejecuta:

    git clone <URL_DEL_REPOSITORIO>

### 2. Base de Datos

Tienen dos opciones para levantar la base de datos.

#### Opción A: Con Docker (Recomendado) 🐳
1. Abran **Docker Desktop** y asegúrense de que esté corriendo.
2. En la terminal, dentro de la carpeta del proyecto, ejecuten:

        docker-compose up -d

#### Opción B: Sin Docker (Manual) 🛠️
> **Nota:** Si no quieren usar Docker, ahí ven ustedes cómo le hacen para que funcione.
> La base de datos (script) se encuentra en la carpeta `/db`. Tendrán que configurarla manualmente en su motor de base de datos local.

---

## 💻 Ejecutar el Backend

1. Vayan a la carpeta `backend`.
2. Busquen la clase `BackendApplication.java`.
3. Ejecútenlo con el botón **Play** de su IDE (IntelliJ / VSCode) o vía terminal:

        ./mvnw spring-boot:run

### ✅ Verificación de éxito

Si todo salió bien (y no se olvidaron de abrir el Docker Desktop), les saldrá al final de la consola algo como esto:

    .BackendApplication      : Started BackendApplication in 5.787 seconds (process running for 6.286)

*Si no sale eso, es que en algo fallaron.*

---

## 🌐 Acceder a la Aplicación

Una vez que la consola muestre el mensaje de éxito, abran su navegador y vayan a:

[http://localhost:8080](http://localhost:8080)

Deberían ver la siguiente pantalla:

<img width="1599" height="836" alt="Captura de pantalla de Facto" src="https://github.com/user-attachments/assets/ad14c5dd-4eba-45e9-b665-3d55c7b0f1c9" />

**LISTO** 🎉

## 😲 start-app.sh

También pueden usar el script `start-app.sh` para levantar el entorno de desarrollo.

Pero eso es muy épico asi que no lo usen.

**Base de datos**

Es necesario crear un archivo .env en la raiz de la carpeta backend y configurarla con: 

DB_URL=url
DB_USERNAME=usuario
DB_PASSWORD=contraseña

si les sale un error al correr puede ser por que spring no reconoce el .env, entonces hay
agregar esta linea en launch.json 
"envFile": "${workspaceFolder}/backend/.env"

quedaría así: 
{
    "type": "java",
    "name": "BackendApplication",
    "request": "launch",
    "mainClass": "com.uce.emprendimiento.backend.BackendApplication",
    "projectName": "backend",
    "envFile": "${workspaceFolder}/backend/.env"
}


