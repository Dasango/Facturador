document.addEventListener('DOMContentLoaded', () => {

    const API_URL = '/api/products';
    let currentProducts = [];

    // 1. CARGA DE PARTIALS
    const loadPartial = (elementId, path) => {
        const element = document.getElementById(elementId);
        if (!element) return;

        fetch(path)
            .then(res => res.text())
            .then(html => {
                element.innerHTML = html;
            });
    };

    // --- CORRECCIÓN AQUÍ ---
    // Eliminamos la carga de Sidebar y Header desde este archivo.
    // common.js ya se encarga de eso.

    // Solo cargamos los Modales, que son específicos de esta página
    loadPartial('modals-container', '/partials/modals.html');

    // 2. CARGA DE DATOS
    loadProducts();

    function loadProducts() {
        fetch(API_URL)
            .then(res => {
                if (res.status === 401) window.location.href = '/';
                return res.json();
            })
            .then(data => {
                currentProducts = data;
                renderTable(data);
            })
            .catch(err => console.error(err));
    }

    function renderTable(products) {
        const tableBody = document.getElementById('productsTableBody');
        const countLabel = document.getElementById('productCount');

        if (!tableBody) return;
        tableBody.innerHTML = '';

        if (products.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7" class="text-center">No hay productos registrados.</td></tr>';
            if (countLabel) countLabel.textContent = '(0)';
            return;
        }

        products.forEach(p => {
            const ivaDisplay = (p.tarifa === 15) ? '15%' : '0%';
            const row = `
                <tr>
                    <td>${p.codigoPrincipal}</td>
                    <td>${p.codigoAuxiliar || ''}</td>
                    <td>${p.nombre}</td>
                    <td class="text-right">$${p.valorUnitario?.toFixed(2)}</td>
                    <td>${ivaDisplay}</td>
                    <td>${p.ice || '-'}</td>
                    <td class="text-right">
                        <button class="action-icon-btn" title="Editar" onclick="window.openEditModal(${p.id})">✏️</button>
                        <button class="action-icon-btn" title="Eliminar" onclick="window.openDeleteModal(${p.id})">🗑️</button>
                    </td>
                </tr>`;
            tableBody.innerHTML += row;
        });

        if (countLabel) countLabel.textContent = `(${products.length} productos)`;
    }

    // --- 3. FUNCIONES DE MODALES ---

    // ABRIR PARA CREAR (NUEVO)
    window.openCreateModal = () => {
        document.getElementById('editId').value = '';
        document.getElementById('editForm').reset();

        const title = document.querySelector('#modalEdit .text-h2');
        if (title) title.textContent = 'Nuevo Producto';

        document.getElementById('modalEdit').classList.add('open');
    };

    // ABRIR PARA EDITAR
    window.openEditModal = (id) => {
        const p = currentProducts.find(x => x.id === id);
        if (!p) return;

        document.getElementById('editId').value = p.id;
        document.getElementById('editCodigoP').value = p.codigoPrincipal;
        document.getElementById('editCodigoA').value = p.codigoAuxiliar || '';
        document.getElementById('editNombre').value = p.nombre;
        document.getElementById('editPrecio').value = p.valorUnitario;
        const ivaSelect = document.getElementById('editIva');
        if (ivaSelect) {
            ivaSelect.value = (p.tarifa === 15) ? '15%' : '0%';
        } 

        const title = document.querySelector('#modalEdit .text-h2');
        if (title) title.textContent = 'Editar Producto';

        document.getElementById('modalEdit').classList.add('open');
    };

    // GUARDAR
    window.saveProduct = () => {
        const id = document.getElementById('editId').value;
        const ivaVal = document.getElementById('editIva').value;
        
        let tarifa = 0.0;
        let codigoPorcentaje = "0";
        if (ivaVal === '15%') {
            tarifa = 15.0;
            codigoPorcentaje = "4";
        }

        const data = {
            codigoPrincipal: document.getElementById('editCodigoP').value,
            codigoAuxiliar: document.getElementById('editCodigoA').value,
            nombre: document.getElementById('editNombre').value,
            valorUnitario: parseFloat(document.getElementById('editPrecio').value),
            codigoImpuesto: "2",
            codigoPorcentaje: codigoPorcentaje,
            tarifa: tarifa
        };

        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_URL}/${id}` : API_URL;

        fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(res => {
                if (res.ok) {
                    window.closeModal('modalEdit');
                    loadProducts();
                } else {
                    alert('Error al guardar');
                }
            });
    };

    window.openDeleteModal = (id) => {
        document.getElementById('deleteId').value = id;
        document.getElementById('modalDelete').classList.add('open');
    };

    window.confirmDelete = () => {
        const id = document.getElementById('deleteId').value;
        fetch(`${API_URL}/${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok || res.status === 204) {
                    window.closeModal('modalDelete');
                    loadProducts();
                } else {
                    alert('Error al eliminar');
                }
            });
    };

    window.closeModal = (id) => {
        const modal = document.getElementById(id);
        if (modal) modal.classList.remove('open');
    };
});