document.addEventListener('DOMContentLoaded', () => {
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }

    // Cargar Footer
    fetch('../partials/footer.html')
        .then(response => response.text())
        .then(data => {
            const footer = document.getElementById('footer-container');
            if (footer) footer.innerHTML = data;
        })
        .catch(e => console.log("Footer skipped"));
});

// Navegación del Wizard
function nextStep(step) {
    let currentStepId = step - 1; // El paso actual es el anterior al que voy
    let inputs = document.querySelectorAll(`#step${currentStepId} input[required]`);
    let valid = true;

    // Validación genérica de campos requeridos en el paso actual
    inputs.forEach(i => {
        if (!i.value.trim()) {
            valid = false;
            i.style.borderColor = '#e63946'; // Rojo error
        } else {
            i.style.borderColor = '#ddd';
        }
    });

    // Validaciones específicas del Paso 1 (Contraseñas)
    if (currentStepId === 1) {
        const pass = document.getElementById('password').value;
        const confirm = document.getElementById('confirm_password').value;

        if (pass && confirm && pass !== confirm) {
            alert("Las contraseñas no coinciden");
            return;
        }
    }

    if (!valid) {
        alert("Por favor completa los campos marcados en rojo.");
        return;
    }

    // Cambiar de paso visualmente
    document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
    document.getElementById(`step${step}`).classList.add('active');

    // Actualizar indicador de texto
    const titles = { 1: "Identificación", 2: "Dirección Matriz", 3: "Archivos" };
    document.getElementById('stepIndicator').innerText = `Paso ${step} de 3: ${titles[step]}`;
}

function prevStep(step) {
    document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
    document.getElementById(`step${step}`).classList.add('active');

    const titles = { 1: "Identificación", 2: "Dirección Matriz", 3: "Archivos" };
    document.getElementById('stepIndicator').innerText = `Paso ${step} de 3: ${titles[step]}`;
}

/**
 * Manejo del Registro
 */
async function handleSignup(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData.entries());

    // 1. CONSTRUCCIÓN DE LA DIRECCIÓN MATRIZ
    // Formato: Barrio: X Calle: Y Numero: Z Interseccion: W
    let direccionCompleta = `Calle: ${data.calle_principal.toUpperCase()} Numero: ${data.numero_casa.toUpperCase()} Interseccion: ${data.interseccion.toUpperCase()}`;

    if (data.barrio && data.barrio.trim() !== "") {
        direccionCompleta = `Barrio: ${data.barrio.toUpperCase()} ` + direccionCompleta;
    }

    // 2. INFERENCIA DE RAZÓN SOCIAL
    const razonSocialInferida = `${data.nombres} ${data.apellidos}`.toUpperCase();

    // 3. ARCHIVOS
    const firmaFile = document.getElementById('firma_archivo').files[0];
    const logoFile = document.getElementById('logo_archivo').files[0];

    // Validar que subió firma
    if (!firmaFile) {
        alert("Debes seleccionar tu archivo de Firma Electrónica (.p12)");
        return;
    }

    // 4. PREPARAR PAYLOAD (FormData para enviar archivos)
    // Usamos FormData directamente para soportar MultipartFile en el backend
    const payload = new FormData();

    // Datos Personales
    payload.append('nombres', data.nombres);
    payload.append('apellidos', data.apellidos);
    payload.append('correo', data.email);
    payload.append('contrasena', data.password);

    // Datos SRI
    payload.append('ruc', data.ruc);
    payload.append('razonSocial', razonSocialInferida);
    payload.append('nombreComercial', razonSocialInferida);
    payload.append('direccionMatriz', direccionCompleta);

    // Defaults
    payload.append('codigoEstablecimiento', "001");
    payload.append('codigoPuntoEmision', "001");
    payload.append('obligadoContabilidad', "NO");
    // payload.append('nroContribuyenteEspecial', ""); 

    // Archivos (Importante: los nombres deben coincidir con los campos en RegisterRequest.java: 'firma' y 'logo')
    payload.append('firma', firmaFile);
    if (logoFile) {
        payload.append('logo', logoFile);
    }
    // firmaPassword se asume null si no se pide

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            // NO establecemos Content-Type a mano. fetch lo hace automáticamente para FormData incluyendo el boundary.
            body: payload
        });

        // Intentamos parsear la respuesta
        let result = {};
        try {
            result = await response.json();
        } catch (e) {
            console.warn("No JSON response", e);
        }

        if (response.ok) {
            alert('¡Cuenta creada exitosamente! Bienvenido a Facto.');
            window.location.href = '../index.html';
        } else {
            alert('Error al registrar: ' + (result.message || response.statusText));
        }

    } catch (error) {
        console.error('Error:', error);
        alert('Error conectando con el servidor: ' + error.message);
    }
}