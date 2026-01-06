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

---

## 📡 API Controllers y Endpoints

A continuación se detallan los controladores disponibles y sus funcionalidades principales tras las últimas actualizaciones.

### 1. InvoiceController
**Base URL**: `/api/invoices`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **POST** | `/` | Crea una nueva factura. Soporta detalles anidados (pagos, detalles de productos, info adicional) en el mismo JSON. |
| **GET** | `/` | Obtiene todas las facturas del usuario autenticado. |
| **GET** | `/{id}/ride` | Obtiene una factura específica y los datos del emisor para generar el RIDE (PDF). |
| **GET** | `/{id}/xml-data` | **[NUEVO]** Obtiene la representación `FacturaDTO` (JSON) que se usará para generar el XML. Útil para verificar datos antes de la generación real. |

### 2. InvoiceXmlController
**Base URL**: `/api/xml`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **POST** | `/generate` | **[NUEVO]** Acepta un `FacturaDTO` JSON y retorna el XML sin firmar (String). |
| **POST** | `/sign` | **[NUEVO]** Acepta JSON `{ "xml": "...", "password": "..." }`. Retorna el XML firmado digitalmente (XAdES-BES) usando la firma subida por el usuario. |
| **POST** | `/create-signed` | **[NUEVO]** Flujo completo seguro. Acepta `{ "invoiceId": 123, "password": "..." }`. <br> 1. Busca los datos. <br> 2. Genera el XML. <br> 3. Lo firma con la clave. <br> 4. Retorna el XML final firmado. |
| **GET** | `/factura` | Endpoint de prueba (Mock deprecado). |

### 3. ProductController
**Base URL**: `/api/products`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **GET** | `/` | Listar productos. |
| **POST** | `/` | Crear producto. |
| **GET** | `/{id}` | Obtener producto. |
| **PUT** | `/{id}` | Actualizar producto. |
| **DELETE** | `/{id}` | Eliminar producto. |

### 4. UserController
**Base URL**: `/api/user`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **GET** | `/profile` | Obtener perfil actual. |
| **PUT** | `/profile` | Actualizar perfil. |
| **POST** | `/upload-p12` | Subir firma electrónica (.p12). |
| **POST** | `/upload-logo` | Subir logo. |
| **GET** | `/signature-path` | **[Interno]** Devuelve la ruta del archivo de firma (debug). |



