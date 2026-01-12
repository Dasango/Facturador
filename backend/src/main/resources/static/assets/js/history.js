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
            // 1. Definir clase del badge según estado
            let badgeClass = '';
            if (item.estado === 'AUTORIZADO') badgeClass = 'status-authorized'; // Ojo: asegurate si viene en Mayusculas o Minusculas desde el back
            else if (item.estado === 'PENDIENTE') badgeClass = 'status-pending';
            else badgeClass = 'status-rejected';

            // 2. Crear la URL dinámica usando el ID del item actual
            // Esto generará algo como: /api/invoices/15/ride
            const rideUrl = `/api/invoices/${item.id}/xml-data`;

            // 3. Crear el HTML
            // Agregamos el onclick="window.open(...)" a los botones
            // Safe accessors
            const dateStr = formatDate(item.fechaEmision);
            const numComp = item.numeroComprobante || '---';
            const clientName = item.clienteNombre || 'Consumidor Final';
            const clientId = item.clienteIdentificacion || '9999999999999';
            const totalVal = (item.total || 0).toFixed(2);

            const row = `
                <tr>
                    <td>${dateStr}</td>
                    <td>Factura</td> 
                    <td>${numComp}</td>
                    <td>${clientId}</td>
                    <td>${clientName}</td>
                    <td style="font-weight:600;">$${totalVal}</td>
                    <td><span class="status-badge ${badgeClass}">${item.estado || 'PENDIENTE'}</span></td>
                    <td>
                        <button class="action-btn" title="Ver RIDE" onclick="generateAndDownloadPdf(${item.id})">
                            👁️
                        </button>
                        <button class="action-btn" title="Ver XML" onclick="window.open('${rideUrl}', '_blank')">
                            ⬇️
                        </button>
                    </td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });

        // Actualizar texto footer
        showingText.textContent = `Mostrando ${data.length} de ${data.length} comprobantes.`;
    }

    // Expose function to window so the inline onclick works
    window.generateAndDownloadPdf = (id) => {
        const jsonUrl = `/api/invoices/${id}`; // Endpoint correctly targeting JSON
        console.log("Fetching JSON from:", jsonUrl);

        fetch(jsonUrl)
            .then(res => {
                if (!res.ok) throw new Error("Error fetching Invoice JSON");
                return res.json();
            })
            .then(invoiceJson => {
                console.log("JSON received, sending to PDF generator...");
                return fetch('/api/pdf/generate', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(invoiceJson)
                });
            })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text(); // Get error details if any
                    throw new Error("Server Error: " + res.status + " " + text);
                }
                return res.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                // Try to get filename from content-disposition if exposed, or default
                a.download = `factura_${id}.pdf`;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            })
            .catch(err => {
                console.error("Error in PDF flow:", err);
                alert("Error: " + err.message);
            });
    };

    function updateStats(data) {
        // Calcular contadores simples
        const authorized = data.filter(i => i.estado === 'AUTORIZADO').length;
        const pending = data.filter(i => i.estado === 'PENDIENTE').length;
        const rejected = data.filter(i => i.estado === 'RECHAZADO').length;

        // Sumar total (reduce)
        const totalVal = data.reduce((sum, item) => sum + (item.total || 0), 0);

        // Actualizar DOM
        statAuth.textContent = `${authorized} Comprobantes`;
        statPend.textContent = `${pending} Comprobantes`;
        statRej.textContent = `${rejected} Comprobantes`;
        statTotal.textContent = `$${totalVal.toFixed(2)}`;
    }

    // Helper simple para formatear fecha (YYYY-MM-DD a DD/MM/YYYY)
    function formatDate(dateString) {
        if (!dateString) return '--/--/----';
        // Handle array format [yyyy, mm, dd] which Jackson might produce for LocalDate sometimes
        if (Array.isArray(dateString)) {
            const [year, month, day] = dateString;
            // Pad with 0
            const d = day.toString().padStart(2, '0');
            const m = month.toString().padStart(2, '0');
            return `${d}/${m}/${year}`;
        }
        // Handle string
        try {
            const [year, month, day] = dateString.split('-');
            return `${day}/${month}/${year}`;
        } catch (e) {
            return dateString;
        }
    }
});