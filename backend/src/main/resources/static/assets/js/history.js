document.addEventListener('DOMContentLoaded', () => {

    // 1. CARGAR PARTIALS (Reutilizado)
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


    loadPartial('sidebar-container', '/partials/sidebar.html');
    loadPartial('header-container', '/partials/header.html');

    // 2. FETCH Y RENDER DE DATOS
    const tableBody = document.getElementById('tableBody');
    const showingText = document.getElementById('showingText');

    // Referencias a los contadores
    const statAuth = document.getElementById('stat-authorized');
    const statPend = document.getElementById('stat-pending');
    const statRej = document.getElementById('stat-rejected');
    const statTotal = document.getElementById('stat-total');

    fetch('/api/invoices')
        .then(response => response.json())
        .then(data => {
            renderTable(data);
            updateStats(data);
        })
        .catch(error => {
            console.error('Error cargando datos:', error);
            tableBody.innerHTML = '<tr><td colspan="8" style="text-align:center; color:red;">Error cargando datos simulados</td></tr>';
        });

    function renderTable(data) {
        tableBody.innerHTML = ''; // Limpiar loader

        data.forEach(item => {
            // Definir clase del badge según estado
            let badgeClass = '';
            if (item.estado === 'Autorizado') badgeClass = 'status-authorized';
            else if (item.estado === 'Pendiente') badgeClass = 'status-pending';
            else badgeClass = 'status-rejected';

            const row = `
                <tr>
                    <td>${formatDate(item.fechaEmision)}</td>
                    <td>Factura</td> 
                    <td>${item.numeroComprobante}</td>
                    <td>${item.clienteIdentificacion}</td>
                    <td>${item.clienteNombre}</td>
                    <td style="font-weight:600;">$${item.total.toFixed(2)}</td>
                    <td><span class="status-badge ${badgeClass}">${item.estado}</span></td>
                    <td>
                        <button class="action-btn" title="Ver">👁️</button>
                        <button class="action-btn" title="Descargar">⬇️</button>
                    </td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });

        // Actualizar texto footer
        showingText.textContent = `Mostrando ${data.length} de ${data.length} comprobantes.`;
    }

    function updateStats(data) {
        // Calcular contadores simples
        const authorized = data.filter(i => i.estado === 'Autorizado').length;
        const pending = data.filter(i => i.estado === 'Pendiente').length;
        const rejected = data.filter(i => i.estado === 'Rechazado').length;

        // Sumar total (reduce)
        const totalVal = data.reduce((sum, item) => sum + item.total, 0);

        // Actualizar DOM
        statAuth.textContent = `${authorized} Comprobantes`;
        statPend.textContent = `${pending} Comprobantes`;
        statRej.textContent = `${rejected} Comprobantes`;
        statTotal.textContent = `$${totalVal.toFixed(2)}`;
    }

    // Helper simple para formatear fecha (YYYY-MM-DD a DD/MM/YYYY)
    function formatDate(dateString) {
        const [year, month, day] = dateString.split('-');
        return `${day}/${month}/${year}`;
    }
});