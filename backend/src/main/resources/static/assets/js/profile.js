document.addEventListener('DOMContentLoaded', () => {

    // Config
    const userId = 1; // TODO: Get from session
    const apiBase = `/api/profile/${userId}`;

    // Elements
    const logoInput = document.getElementById('logoInput');
    const logoPreview = document.getElementById('logoPreview');
    const btnSaveInfo = document.getElementById('btnSaveInfo');

    const p12Input = document.getElementById('p12Input');
    const fileNameDisplay = document.getElementById('fileNameDisplay');
    const btnUploadP12 = document.getElementById('btnUploadP12');
    const p12PasswordHelper = document.getElementById('p12PasswordHelper');
    const p12Status = document.getElementById('p12Status');

    // Load Initial Data
    loadProfileData();

    // ================= LOGO UPLOAD =================
    if (logoInput) {
        logoInput.addEventListener('change', async (e) => {
            const file = e.target.files[0];
            if (!file) return;

            const formData = new FormData();
            formData.append('file', file);

            try {
                // Show updating state
                logoPreview.innerHTML = '<span style="font-size:24px">⏳</span>';

                const resp = await fetch(`${apiBase}/upload-logo`, {
                    method: 'POST',
                    body: formData
                });

                if (resp.ok) {
                    const path = await resp.text();
                    // In a real app we would serve the image back.
                    // For local filesystem path ref, browsers prevent loading local files.
                    // Ideally we should return a URL like /api/images/{userId}/logo
                    // For now, we will just show "Uploaded" or use FileReader for preview

                    // Show client-side preview for immediate feedback
                    const reader = new FileReader();
                    reader.onload = (ev) => {
                        logoPreview.innerHTML = `<img src="${ev.target.result}" style="width:100%; height:100%; object-fit:cover;">`;
                    };
                    reader.readAsDataURL(file);

                    alert('Logo actualizado correctamente');
                } else {
                    throw new Error('Error al subir logo');
                }
            } catch (err) {
                console.error(err);
                alert('No se pudo subir el logo');
                logoPreview.innerHTML = '<span style="font-size:40px; color:#ef4444;">❌</span>';
            }
        });
    }

    // ================= INFO UPDATE =================
    if (btnSaveInfo) {
        btnSaveInfo.addEventListener('click', async () => {
            const data = {
                ruc: document.getElementById('ruc').value,
                razonSocial: document.getElementById('razonSocial').value,
                nombres: document.getElementById('nombres').value // Using as Trade name usually, or actual name
            };

            try {
                const resp = await fetch(apiBase, { // PUT to /api/profile/{id}
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });

                if (resp.ok) {
                    alert('Información guardada');
                    // Update header name too if changed
                    const user = JSON.parse(localStorage.getItem('user')) || {};
                    user.nombre = data.nombres || user.nombre;
                    localStorage.setItem('user', JSON.stringify(user));
                    const headerName = document.getElementById('headerUserName');
                    if (headerName) headerName.textContent = user.nombre;
                } else {
                    alert('Error al guardar datos');
                }
            } catch (e) {
                console.error(e);
            }
        });
    }

    // ================= P12 UPLOAD =================
    if (p12Input) {
        p12Input.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                fileNameDisplay.textContent = file.name;
                fileNameDisplay.style.color = '#0f172a';
                checkUploadButton();
            }
        });
    }

    if (p12PasswordHelper) {
        p12PasswordHelper.addEventListener('input', checkUploadButton);
    }

    function checkUploadButton() {
        if (p12Input.files.length > 0 && p12PasswordHelper.value.length > 0) {
            btnUploadP12.disabled = false;
        } else {
            btnUploadP12.disabled = true;
        }
    }

    if (btnUploadP12) {
        btnUploadP12.addEventListener('click', async () => {
            const file = p12Input.files[0];
            const password = p12PasswordHelper.value;

            const formData = new FormData();
            formData.append('file', file);
            formData.append('password', password);

            try {
                btnUploadP12.textContent = 'Subiendo...';
                btnUploadP12.disabled = true;

                const resp = await fetch(`${apiBase}/upload-p12`, {
                    method: 'POST',
                    body: formData
                });

                if (resp.ok) {
                    alert('Firma guardada exitosamente');
                    p12Status.className = 'status-badge success';
                    p12Status.textContent = 'Configurada';
                    p12Status.style.background = '#dcfce7';
                    p12Status.style.color = '#15803d';
                    p12Input.value = '';
                    p12PasswordHelper.value = '';
                    fileNameDisplay.textContent = 'Seleccionar archivo...';
                } else {
                    throw new Error(await resp.text());
                }

            } catch (e) {
                alert('Error: ' + e.message);
            } finally {
                btnUploadP12.textContent = 'Subir Firma';
                btnUploadP12.disabled = false;
            }
        });
    }

    // Helper
    async function loadProfileData() {
        try {
            const resp = await fetch(apiBase);
            if (resp.ok) {
                const user = await resp.json();

                document.getElementById('ruc').value = user.ruc || '';
                document.getElementById('razonSocial').value = user.razonSocial || '';
                document.getElementById('nombres').value = user.nombres || '';

                if (user.firmaPath) {
                    p12Status.className = 'status-badge success';
                    p12Status.textContent = 'Configurada';
                    p12Status.style.background = '#dcfce7';
                    p12Status.style.color = '#15803d';
                }

                if (user.logoPath) {
                    // Logic to display existing logo would go here
                    // e.g., fetch blob
                    logoPreview.innerHTML = '<span style="font-size:40px">🏢</span>'; // Placeholder for now unless we add an image serving endpoint
                }
            }
        } catch (e) {
            console.error('Error loading profile', e);
        }
    }
});