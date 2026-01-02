document.addEventListener('DOMContentLoaded', () => {

    // Función helper para cargar partials
    const loadPartial = (elementId, path) => {
        fetch(path)
            .then(response => {
                if (!response.ok) throw new Error('Error loading partial');
                return response.text();
            })
            .then(html => {
                document.getElementById(elementId).innerHTML = html;
            })
            .catch(error => console.error('Error:', error));
    };

    // 1. Cargar Sidebar
    loadPartial('sidebar-container', 'partials/sidebar.html');

    // 2. Cargar Header
    loadPartial('header-container', 'partials/header.html');

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