const nameInput = document.getElementById('nameInput');
const submitBtn = document.getElementById('submitBtn');
const responseContainer = document.getElementById('responseContainer');
const responseMessage = document.getElementById('responseMessage');
const errorContainer = document.getElementById('errorContainer');
const errorMessage = document.getElementById('errorMessage');

// Enviar al presionar Enter
nameInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        submitBtn.click();
    }
});

// Botón de envío
submitBtn.addEventListener('click', async () => {
    const name = nameInput.value.trim();

    // Limpiar mensajes anteriores
    responseContainer.classList.add('hidden');
    errorContainer.classList.add('hidden');

    // Validación básica en el frontend
    if (!name) {
        showError('Por favor, ingresa un nombre');
        return;
    }

    if (name.length < 2) {
        showError('El nombre debe tener al menos 2 caracteres');
        return;
    }

    if (/\s/.test(name)) {
        showError('El nombre debe ser una sola palabra (sin espacios)');
        return;
    }

    try {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Enviando...';

        // Enviar al backend
        const response = await fetch('http://localhost:8080/api/nombre', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ nombre: name })
        });

        const data = await response.json();

        if (response.ok) {
            showSuccess(`¡Hola ${data.nombre}!`);
            nameInput.value = '';
        } else {
            showError(data.error || 'Error al procesar el nombre');
        }
    } catch (error) {
        console.error('Error:', error);
        showError('Error al conectar con el servidor. Asegúrate de que el backend está corriendo en http://localhost:8080');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Enviar';
    }
});

function showSuccess(message) {
    responseMessage.textContent = message;
    responseContainer.classList.remove('hidden');
    errorContainer.classList.add('hidden');
}

function showError(message) {
    errorMessage.textContent = message;
    errorContainer.classList.remove('hidden');
    responseContainer.classList.add('hidden');
}
