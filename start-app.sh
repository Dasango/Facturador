@echo off
echo ============================================
echo      Iniciando Facturador (Dev Mode)
echo ============================================

echo [1/3] Iniciando Base de Datos (Docker)...
docker-compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo Error al iniciar Docker. Asegurate de que Docker Desktop este corriendo.
    pause
    exit /b
)

echo [2/3] Iniciando Backend en nueva ventana...
start "Facturador Backend" cmd /k "cd backend && mvnw spring-boot:run"

echo [3/3] Iniciando Frontend...
echo Intentando usar npx serve para URLs limpias...
call npx --version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Node.js detectado. Iniciando servidor frontend...
    echo La aplicacion se abrira en http://localhost:3000
    start "Facturador Frontend" cmd /k "npx serve frontend"
) else (
    echo Node.js no encontrado. 
    echo Por favor, usa Live Server en VS Code para el frontend o instala Node.js.
    echo Puedes abrir frontend/index.html manualmente.
    explorer "http://127.0.0.1:5500/frontend/index.html"
    pause
)

echo ============================================
echo      Todo se esta iniciando...
echo ============================================
