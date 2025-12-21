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
function handleSignup(event) {
    event.preventDefault();

    // 1. Capture Data
    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData.entries());

    // 2. Validate (Simple check)
    if (data.password !== data.confirm_password) {
        alert("Las contraseñas no coinciden");
        return;
    }

    // 3. Consume Data (Boilerplate log)
    console.group('Signup Attempt');
    console.log('RUC/Cedula:', data.ruc);
    console.log('Email:', data.email);
    console.log('Password:', '******'); // Should not log actual password in prod
    console.log('Full Data Object:', data);
    console.groupEnd();

    // 4. Mimic API Call
    // fetch('/api/signup', { method: 'POST', body: JSON.stringify(data) }) ...
    alert('Datos capturados. Revisa la consola (F12) para ver el objeto JSON.');


}
