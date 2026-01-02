document.addEventListener('DOMContentLoaded', () => {

    // Función helper para cargar partials (Reutilizada)
    const loadPartial = (elementId, path) => {
        fetch(path)
            .then(response => {
                if (!response.ok) throw new Error('Error loading partial');
                return response.text();
            })
            .then(html => {
                document.getElementById(elementId).innerHTML = html;

                // Si cargamos el sidebar, marcar 'Factura' como activo
                if (elementId === 'sidebar-container') {
                    const navItems = document.querySelectorAll('.nav-item');
                    navItems.forEach(item => item.classList.remove('active'));
                    // Asumiendo que el link de factura es el primero o buscar por texto
                    // Simple logic para este ejemplo:
                    const facturaLink = Array.from(navItems).find(el => el.textContent.includes('Factura'));
                    if (facturaLink) facturaLink.classList.add('active');
                }
            })
            .catch(error => console.error('Error:', error));
    };

    // 1. Cargar Layout
    loadPartial('sidebar-container', '/partials/sidebar.html');
    loadPartial('header-container', '/partials/header.html');

    // Aquí iría la lógica para añadir filas dinámicamente a las tablas,
    // calcular totales, etc. Por ahora, solo estructura visual.

    const invoiceForm = document.getElementById('invoiceForm');
    if (invoiceForm) {
        invoiceForm.addEventListener('submit', (e) => {
            e.preventDefault();
            alert('Enviando factura al backend...');
        });
    }
});