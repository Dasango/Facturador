document.addEventListener('DOMContentLoaded', () => {

    // RUTAS API (Sin IDs harcodeados)
    const API_BASE = '/api/user';

    // Referencias DOM
    const logoInput = document.getElementById('logoInput');
    const logoUploadArea = document.getElementById('logoUploadArea');
    const logoPreview = document.getElementById('logoPreview');
    const logoPlaceholder = document.getElementById('logoPlaceholder');

    const p12Input = document.getElementById('p12Input');
    const p12Password = document.getElementById('p12Password');
    const fileNameDisplay = document.getElementById('fileNameDisplay');
    const btnUploadP12 = document.getElementById('btnUploadP12');
    const p12Status = document.getElementById('p12Status');

    const btnSaveInfo = document.getElementById('btnSaveInfo');

    // 1. CARGAR DATOS AL INICIO
    loadProfile();

    async function loadProfile() {
        try {
            const resp = await fetch(`${API_BASE}/profile`);

            if (resp.status === 401) {
                window.location.href = '/';
                return;
            }

            if (resp.ok) {
                const user = await resp.json();

                // Llenar inputs con datos del JSON
                document.getElementById('cedula').value = user.cedula || '';
                document.getElementById('correo').value = user.correo || '';
                document.getElementById('nombres').value = user.nombres || '';
                document.getElementById('apellidos').value = user.apellidos || '';
                document.getElementById('ruc').value = user.ruc || '';
                document.getElementById('razonSocial').value = user.razonSocial || '';

                // Mostrar Logo si existe
                if (user.logoPath && user.logoPath.startsWith('http')) {
                    logoPreview.src = user.logoPath;
                    logoPreview.style.display = 'block';
                    logoPlaceholder.style.display = 'none';
                }

                // Estado de la firma (Si firmaPath existe en el JSON, asumimos que está configurada)
                // NOTA: Como el backend no devuelve firmaPath por seguridad, 
                // podríamos necesitar un campo booleano extra en el backend "hasSignature".
                // Por ahora, asumiremos "No configurada" a menos que guardes una nueva.
            }
        } catch (error) {
            console.error('Error cargando perfil:', error);
        }
    }

    // 2. GUARDAR DATOS TEXTO (PUT)
    btnSaveInfo.addEventListener('click', async () => {
        const data = {
            nombres: document.getElementById('nombres').value,
            apellidos: document.getElementById('apellidos').value,
            ruc: document.getElementById('ruc').value,
            razonSocial: document.getElementById('razonSocial').value
        };

        try {
            const resp = await fetch(`${API_BASE}/profile`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (resp.ok) {
                alert('Datos actualizados correctamente');
                // Actualizar nombre en el header sin recargar
                const headerName = document.getElementById('headerUserName');
                if (headerName) headerName.textContent = `${data.nombres} ${data.apellidos}`;
            } else {
                alert('Error al guardar');
            }
        } catch (error) {
            console.error(error);
            alert('Error de conexión');
        }
    });

    // 3. SUBIR LOGO (POST)
    logoUploadArea.addEventListener('click', () => logoInput.click());

    logoInput.addEventListener('change', async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        // Previsualización inmediata
        const reader = new FileReader();
        reader.onload = (ev) => {
            logoPreview.src = ev.target.result;
            logoPreview.style.display = 'block';
            logoPlaceholder.style.display = 'none';
        };
        reader.readAsDataURL(file);

        // Subir al backend
        try {
            const resp = await fetch(`${API_BASE}/upload-logo`, {
                method: 'POST',
                body: formData
            });

            if (resp.ok) {
                console.log('Logo subido');
            } else {
                alert('Error al subir el logo');
            }
        } catch (error) {
            console.error(error);
        }
    });

    // 4. SUBIR FIRMA P12 (POST)
    // Lógica visual
    p12Input.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            fileNameDisplay.textContent = file.name;
            fileNameDisplay.style.color = '#000';
            p12Password.disabled = false; // Habilitar campo contraseña
            checkP12Button();
        }
    });

    p12Password.addEventListener('input', checkP12Button);

    function checkP12Button() {
        // Solo habilitar botón "Subir" si hay archivo Y contraseña
        if (p12Input.files.length > 0 && p12Password.value.trim().length > 0) {
            btnUploadP12.disabled = false;
            btnUploadP12.classList.remove('btn-secondary');
            btnUploadP12.classList.add('btn-primary');
        } else {
            btnUploadP12.disabled = true;
            btnUploadP12.classList.remove('btn-primary');
            btnUploadP12.classList.add('btn-secondary');
        }
    }

    // Acción de subir firma
    btnUploadP12.addEventListener('click', async () => {
        const file = p12Input.files[0];
        const password = p12Password.value;

        const formData = new FormData();
        formData.append('file', file);
        formData.append('password', password);

        try {
            btnUploadP12.textContent = 'Subiendo...';

            const resp = await fetch(`${API_BASE}/upload-p12`, {
                method: 'POST',
                body: formData
            });

            if (resp.ok) {
                alert('Firma electrónica configurada correctamente');
                p12Status.textContent = 'Configurada';
                p12Status.className = 'status-badge success';

                // Limpiar campos por seguridad
                p12Input.value = '';
                p12Password.value = '';
                fileNameDisplay.textContent = 'Archivo subido exitosamente';
                btnUploadP12.disabled = true;
                btnUploadP12.textContent = 'Subir Firma';
            } else {
                const err = await resp.text();
                alert('Error: ' + err);
                btnUploadP12.textContent = 'Subir Firma';
            }
        } catch (error) {
            console.error(error);
            alert('Error de conexión');
            btnUploadP12.textContent = 'Subir Firma';
        }
    });

});