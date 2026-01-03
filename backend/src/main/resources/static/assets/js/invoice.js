document.addEventListener('DOMContentLoaded', () => {

    /* ==========================================================================
       INITIALIZATION & HELPERS
       ========================================================================== */
    const loadPartial = (elementId, path) => {
        fetch(path)
            .then(response => {
                if (!response.ok) throw new Error('Error loading partial');
                return response.text();
            })
            .then(html => {
                document.getElementById(elementId).innerHTML = html;
                if (elementId === 'sidebar-container') {
                    const navItems = document.querySelectorAll('.nav-item');
                    navItems.forEach(item => item.classList.remove('active'));
                    const facturaLink = Array.from(navItems).find(el => el.textContent.includes('Factura'));
                    if (facturaLink) facturaLink.classList.add('active');
                }
            })
            .catch(error => console.error('Error:', error));
    };

    loadPartial('sidebar-container', '/partials/sidebar.html');
    loadPartial('header-container', '/partials/header.html');

    /* ==========================================================================
       TABLE LOGIC & CALCULATIONS
       ========================================================================== */
    const tableBody = document.querySelector('#detallesTable tbody');
    const addRowBtn = document.getElementById('addRowBtn');

    // Add Row
    addRowBtn.addEventListener('click', () => {
        const row = document.createElement('tr');
        row.className = 'detalle-row';
        row.innerHTML = `
            <td><input type="text" class="input-control sm-input codigo" value="NUEVO"></td>
            <td><input type="text" class="input-control sm-input descripcion" value="Nuevo Producto"></td>
            <td><input type="number" class="input-control sm-input cantidad" value="1" min="1"></td>
            <td><input type="number" class="input-control sm-input precio" value="0.00" step="0.01"></td>
            <td><input type="number" class="input-control sm-input descuento" value="0.00" step="0.01"></td>
            <td class="total-linea">0.00</td>
            <td><button type="button" class="btn-icon danger remove-row">🗑</button></td>
        `;
        tableBody.appendChild(row);
        attachRowEvents(row);
        calculateTotals();
    });

    // Attach events to existing rows
    document.querySelectorAll('.detalle-row').forEach(attachRowEvents);

    function attachRowEvents(row) {
        // Remove button
        row.querySelector('.remove-row').addEventListener('click', () => {
            row.remove();
            calculateTotals();
        });

        // Inputs change
        const inputs = row.querySelectorAll('input');
        inputs.forEach(input => {
            input.addEventListener('input', () => {
                calculateLineTotal(row);
                calculateTotals();
            });
        });
    }

    function calculateLineTotal(row) {
        const qty = parseFloat(row.querySelector('.cantidad').value) || 0;
        const price = parseFloat(row.querySelector('.precio').value) || 0;
        const discount = parseFloat(row.querySelector('.descuento').value) || 0;

        let total = (qty * price) - discount;
        if (total < 0) total = 0;

        row.querySelector('.total-linea').textContent = total.toFixed(2);
    }

    function calculateTotals() {
        let subtotal = 0;
        document.querySelectorAll('.detalle-row').forEach(row => {
            const lineTotal = parseFloat(row.querySelector('.total-linea').textContent) || 0;
            subtotal += lineTotal;
        });

        const iva = subtotal * 0.15; // 15% fixed for now
        const total = subtotal + iva;

        document.getElementById('subtotalDisplay').textContent = subtotal.toFixed(2);
        document.getElementById('ivaDisplay').textContent = iva.toFixed(2);
        document.getElementById('totalDisplay').textContent = total.toFixed(2);
    }

    /* ==========================================================================
       SUBMIT & API INTEGRATION
       ========================================================================== */
    const invoiceForm = document.getElementById('invoiceForm');

    invoiceForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 1. Gather Data
        const pathP12 = document.getElementById('pathP12').value;
        const claveP12 = document.getElementById('claveP12').value;
        const fechaEmisionRaw = document.getElementById('fechaEmision').value; // yyyy-mm-dd

        // Format Date to dd/MM/yyyy
        const [year, month, day] = fechaEmisionRaw.split('-');
        const fechaEmisionFormatted = `${day}/${month}/${year}`;

        // 2. Build JSON
        const factura = {
            id: "comprobante",
            version: "1.0.0",
            infoTributaria: {
                ambiente: "1", // Pruebas
                tipoEmision: "1", // Normal
                razonSocial: "EMPRESA DE PRUEBA",
                nombreComercial: document.getElementById('nombreComercial').value,
                ruc: "1790000000001", // Hardcoded issuer RUC for testing
                codDoc: "01", // Factura
                estab: document.getElementById('estab').value,
                ptoEmi: document.getElementById('ptoEmi').value,
                secuencial: "000000001", // TODO: Auto-increment or input
                dirMatriz: "Quito, Av. Amazonas"
            },
            infoFactura: {
                fechaEmision: fechaEmisionFormatted,
                dirEstablecimiento: "Quito, Av. Amazonas",
                obligadoContabilidad: "NO",
                tipoIdentificacionComprador: document.getElementById('tipoIdentificacionComprador').value,
                razonSocialComprador: document.getElementById('razonSocialComprador').value,
                identificacionComprador: document.getElementById('identificacionComprador').value,
                direccionComprador: document.getElementById('direccionComprador').value,
                totalSinImpuestos: parseFloat(document.getElementById('subtotalDisplay').textContent),
                totalDescuento: 0.00,
                propina: 0.00,
                importeTotal: parseFloat(document.getElementById('totalDisplay').textContent),
                moneda: "DOLAR",
                pagos: [{ formaPago: "01", total: parseFloat(document.getElementById('totalDisplay').textContent) }], // Efectivo default
                totalConImpuestos: [
                    {
                        codigo: "2", // IVA
                        codigoPorcentaje: "4", // 15% (Adjust based on catalog)
                        baseImponible: parseFloat(document.getElementById('subtotalDisplay').textContent),
                        valor: parseFloat(document.getElementById('ivaDisplay').textContent)
                    }
                ]
            },
            detalles: []
        };

        // Gather Details
        document.querySelectorAll('.detalle-row').forEach(row => {
            const qty = parseFloat(row.querySelector('.cantidad').value);
            const price = parseFloat(row.querySelector('.precio').value);
            const discount = parseFloat(row.querySelector('.descuento').value);
            const lineTotal = parseFloat(row.querySelector('.total-linea').textContent);

            factura.detalles.push({
                codigoPrincipal: row.querySelector('.codigo').value,
                descripcion: row.querySelector('.descripcion').value,
                cantidad: qty,
                precioUnitario: price,
                descuento: discount,
                precioTotalSinImpuesto: lineTotal,
                impuestos: [
                    {
                        codigo: "2", // IVA
                        codigoPorcentaje: "4", // 15%
                        tarifa: 15.00,
                        baseImponible: lineTotal,
                        valor: lineTotal * 0.15
                    }
                ]
            });
        });

        // 3. Send API Request
        try {
            const submitBtn = invoiceForm.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span>⏳</span> Procesando...';

            const queryParams = new URLSearchParams({
                pathP12: pathP12,
                claveP12: claveP12
            });

            const response = await fetch(`/api/sri/emitir?${queryParams}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(factura)
            });

            const result = await response.text();

            if (response.ok) {
                alert('✅ Respuesta SRI: ' + result);
            } else {
                throw new Error(result);
            }

        } catch (error) {
            alert('❌ Error: ' + error.message);
        } finally {
            const submitBtn = invoiceForm.querySelector('button[type="submit"]');
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<span>🚀</span> Firmar y Enviar'; // Restore button
        }
    });

    // Initial calcs
    calculateTotals();
});