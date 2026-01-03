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
                    // Highlight logic if needed
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
    if (addRowBtn) {
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
    }

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
       SUBMIT & API INTEGRATION (VIA POPUP)
       ========================================================================== */
    const signModal = document.getElementById('signModal');
    const btnOpenSignModal = document.getElementById('btnOpenSignModal');
    const btnConfirmSign = document.getElementById('btnConfirmSign');
    const modalClaveP12 = document.getElementById('modalClaveP12');

    // Open Modal
    if (btnOpenSignModal) {
        btnOpenSignModal.addEventListener('click', (e) => {
            e.preventDefault();
            // Basic Validation before opening modal
            if (document.querySelectorAll('.detalle-row').length === 0) {
                alert('Debe agregar al menos un producto.');
                return;
            }
            signModal.style.display = 'flex';
        });
    }

    // Confirm Sign
    if (btnConfirmSign) {
        btnConfirmSign.addEventListener('click', async () => {
            const claveFirma = modalClaveP12.value;
            if (!claveFirma) {
                alert('Por favor ingrese la contraseña de su firma.');
                return;
            }

            // 1. Gather Data
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
                btnConfirmSign.disabled = true;
                btnConfirmSign.textContent = 'Procesando...';

                // Usamos userId 1 hardcoded por ahora
                const userId = 1;

                const queryParams = new URLSearchParams({
                    userId: userId,
                    claveFirma: claveFirma
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
                    signModal.style.display = 'none';
                } else {
                    throw new Error(result);
                }

            } catch (error) {
                alert('❌ Error: ' + error.message);
            } finally {
                btnConfirmSign.disabled = false;
                btnConfirmSign.textContent = 'Confirmar y Emitir';
            }
        });
    }

    // Initial calcs
    calculateTotals();
});