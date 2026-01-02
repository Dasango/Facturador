document.addEventListener('DOMContentLoaded', () => {

    // Función helper para cargar partials
    const loadPartial = (elementId, path, callback) => {
        fetch(path)
            .then(response => {
                if (!response.ok) throw new Error('Error loading partial');
                return response.text();
            })
            .then(html => {
                document.getElementById(elementId).innerHTML = html;
                if (callback) callback();
            })
            .catch(error => console.error('Error:', error));
    };

    // 1. Cargar Sidebar
    loadPartial('sidebar-container', '/partials/sidebar.html');

    // 2. Cargar Header y Llenar Datos Usuario
    loadPartial('header-container', '/partials/header.html', () => {
        // Callback executed after header matches
        fetch('/api/auth/me')
            .then(res => {
                if (res.ok) return res.json();
                console.log('User not logged in');
                return null;
            })
            .then(user => {
                if (user) {
                    const profileWidget = document.querySelector('.user-profile-widget .text-body');
                    if (profileWidget) {
                        // Display Name or Cedula/RUC
                        profileWidget.textContent = (user.nombres && user.apellidos)
                            ? `${user.nombres.split(' ')[0]} ${user.apellidos.split(' ')[0]}`
                            : user.cedula || user.correo || 'Usuario';
                    }

                    // Also fill profile form inputs if they exist
                    const formName = document.querySelector('input[value="Juan Perez"]');
                    if (formName) formName.value = `${user.nombres || ''} ${user.apellidos || ''}`;
                    const formCedula = document.querySelector('.form-group input[type="text"]:not([value])'); // Heuristic
                    // For better precision, I'd need to ID the inputs in profile.html
                }
            });
    });

    // Lógica del formulario
    const profileForm = document.getElementById('profileForm');
    if (profileForm) {
        profileForm.addEventListener('submit', (e) => {
            e.preventDefault();
            // Aquí iría tu lógica para enviar los datos a Spring Boot
            console.log('Guardando perfil...');
            alert('Datos guardados (Simulación)');
        });
    }
});