#!/bin/bash

# ==========================================
# CONFIGURACIÓN DE COLORES Y ESTILO
# ==========================================
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}"
echo "========================================"
echo "      FACTO 🧾 - Start Up Script      "
echo "========================================"
echo -e "${NC}"

# ==========================================
# 1. VERIFICACIÓN DE DOCKER (Opción A)
# ==========================================
echo -e "${YELLOW}🐳 Verificando estado de Docker...${NC}"

if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Error: Docker no está corriendo.${NC}"
    echo "   Por favor, abre Docker Desktop y vuelve a ejecutar este script."
    exit 1
fi

echo -e "${GREEN}✅ Docker está activo.${NC}"
echo -e "${YELLOW}--> Levantando base de datos con docker-compose...${NC}"

# Intenta levantar el contenedor. Si falla, detiene el script.
docker-compose up -d
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error al ejecutar docker-compose.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Base de datos levantada correctamente.${NC}"
echo ""

# ==========================================
# 2. EJECUTAR EL BACKEND
# ==========================================
echo -e "${YELLOW}💻 Preparando Backend...${NC}"

if [ -d "backend" ]; then
    cd backend || exit
else
    echo -e "${RED}❌ Error: No se encuentra la carpeta 'backend'.${NC}"
    echo "   Asegúrate de estar en la raíz del proyecto."
    exit 1
fi

# Verificar y dar permisos al wrapper de Maven
if [ -f "mvnw" ]; then
    chmod +x mvnw
else
    echo -e "${RED}❌ Error: No se encontró el archivo 'mvnw' en la carpeta backend.${NC}"
    exit 1
fi

echo -e "${BLUE}🚀 Iniciando Spring Boot Application...${NC}"
echo -e "   Espera a ver el mensaje: ${GREEN}Started BackendApplication${NC}"
echo -e "   Luego accede a: ${BLUE}http://localhost:8080${NC}"
echo "--------------------------------------------------------"

# Ejecutar la aplicación
./mvnw spring-boot:run