# FACTO 🧾

> Facturador electrónico funcional con el SRI.

Este proyecto es una solución de facturación diseñada para cumplir con los requisitos del SRI, simplificando el proceso de emisión de comprobantes.

---

## 👥 Equipo de Desarrollo

**⚠️ TAREA:** Poner bien sus nombres aquí abajo.

* Cristian Baraja
* David Sango
* Jhon Córdova
* Jonathan Suárez

---
## 🌐 Acceder a la Aplicación

Una vez que la consola muestre el mensaje de éxito, abran su navegador y vayan a:

[http://localhost:8080](http://localhost:8080)

Deberían ver la siguiente pantalla:

<img width="1599" height="836" alt="Captura de pantalla de Facto" src="https://github.com/user-attachments/assets/ad14c5dd-4eba-45e9-b665-3d55c7b0f1c9" />


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


