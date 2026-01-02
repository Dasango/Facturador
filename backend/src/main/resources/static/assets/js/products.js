document.addEventListener('DOMContentLoaded', () => {

    // 1. CARGAR PARTIALS
    const loadPartial = (elementId, path) => {
        fetch(path)
            .then(response => response.text())
            .then(html => {
                document.getElementById(elementId).innerHTML = html;

                // Activar 'Productos' en sidebar
                if (elementId === 'sidebar-container') {
                    const navItems = document.querySelectorAll('.nav-item');
                    navItems.forEach(item => {
                        item.classList.remove('active');
                        // Buscar coincidencia con "Productos"
                        if (item.textContent.trim() === 'Productos') item.classList.add('active');
                    });
                }
            });
    };

    loadPartial('sidebar-container', '/partials/sidebar.html');
    loadPartial('header-container', '/partials/header.html');

    // 2. CARGAR DATOS DE PRODUCTOS
    const tableBody = document.getElementById('productsTableBody');
    const productCountLabel = document.getElementById('productCount');

    fetch('/assets/data/products.json')
        .then(response => response.json())
        .then(data => {
            renderTable(data);
        })
        .catch(error => {
            console.error('Error cargando productos:', error);
            tableBody.innerHTML = '<tr><td colspan="7" style="text-align:center;">Error al cargar datos.</td></tr>';
        });

    function renderTable(products) {
        tableBody.innerHTML = ''; // Limpiar

        if (products.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7" style="text-align:center;">No hay productos registrados.</td></tr>';
            productCountLabel.textContent = '(0 productos mostrados)';
            return;
        }

        products.forEach(p => {
            const row = `
                <tr>
                    <td>${p.codigo}</td>
                    <td>${p.auxiliar}</td>
                    <td>${p.nombre}</td>
                    <td class="text-right">$${p.valor.toFixed(2)}</td>
                    <td>${p.iva}</td>
                    <td>${p.ice}</td>
                    <td class="text-right">
                        <button class="action-icon-btn" title="Editar">✏️</button>
                        <button class="action-icon-btn" title="Eliminar">🗑️</button>
                    </td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });

        // Actualizar contador
        productCountLabel.textContent = `(${products.length} productos mostrados)`;
    }
});