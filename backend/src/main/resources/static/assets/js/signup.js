document.addEventListener('DOMContentLoaded', () => {
    const signupForm = document.getElementById('signupForm');

    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }

    fetch('../partials/footer.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('footer-container').innerHTML = data;
        });

});

/**
 * Handles the signup form submission
 * @param {Event} event 
 */
/**
 * Handles the signup form submission
 * @param {Event} event 
 */
async function handleSignup(event) {
    event.preventDefault();

    // 1. Capture Data
    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData.entries());

    // 2. Validate
    if (data.password !== data.confirm_password) {
        alert("Las contraseñas no coinciden");
        return;
    }

    // 3. Prepare Payload (Mapping fields to Backend DTO)
    const payload = {
        cedula: data.ruc,
        correo: data.email,
        contrasena: data.password
    };

    try {
        const response = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (response.ok && result.success) {
            alert('Registro exitoso: ' + result.message);
            window.location.href = '../index.html'; // Redirect to landing or login
        } else {
            alert('Error: ' + (result.message || 'Error desconocido'));
            console.error('Signup Validation Error:', result);
        }

    } catch (error) {
        console.error('Network Error:', error);
        alert('Error de conexión con el servidor. Asegúrate de que el backend esté corriendo.');
    }
}
