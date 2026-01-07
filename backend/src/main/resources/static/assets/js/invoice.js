document.addEventListener('DOMContentLoaded', () => {

    /* ==========================================================================
       INITIALIZATION & PARTIALS
       ========================================================================== */
    const loadPartial = (elementId, path) => {
        fetch(path)
            .then(res => {
                if (!res.ok) throw new Error('Failed to load partial ' + path);
                return res.text();
            })
            .then(html => {
                const el = document.getElementById(elementId);
                if (el) el.innerHTML = html;
            })
            .catch(err => console.error(err));
    };

    // Load Company Info from LocalStorage or Backend (Mock for now)
    const storedUser = JSON.parse(localStorage.getItem('facto_user'));
    if (storedUser) {
        document.getElementById('companyName').textContent = storedUser.razonSocial || storedUser.nombres + ' ' + storedUser.apellidos;
        document.getElementById('companyRuc').textContent = 'RUC: ' + (storedUser.ruc || '9999999999999');
        if (storedUser.logoPath && !storedUser.logoPath.includes('pngtree')) {
            // If absolute path, might not load due to security. Using mock URL if recognized.
            if (storedUser.logoPath.startsWith('http')) {
                document.getElementById('companyLogo').innerHTML = `<img src="${storedUser.logoPath}" style="width:100%; height:100%; object-fit:contain;">`;
            }
        }
    }

    /* ==========================================================================
       DYNAMIC LINES: Details, Payments, Info
       ========================================================================== */

    // --- DETAILS ---
    const detallesTableBody = document.querySelector('#detallesTable tbody');
    const addRowBtn = document.getElementById('addRowBtn');

    if (addRowBtn) {
        addRowBtn.addEventListener('click', () => {
            const row = document.createElement('tr');
            row.className = 'detalle-row';
            row.innerHTML = `
                <td><input type="text" class="codigo" value="NEW"></td>
                <td><input type="text" class="descripcion" value="Nuevo Item"></td>
                <td><input type="number" class="cantidad" value="1" min="1"></td>
                <td><input type="number" class="precio" value="0.00" step="0.01"></td>
                <td><input type="number" class="descuento" value="0.00" step="0.01"></td>
                <td class="total-linea" style="text-align: right; font-weight: 600;">0.00</td>
                <td><button type="button" class="btn-icon danger remove-row">×</button></td>
            `;
            detallesTableBody.appendChild(row);
            attachRowEvents(row);
        });
    }

    // --- ADDITIONAL INFO ---
    const infoContainer = document.getElementById('infoAdicionalContainer');
    const addInfoBtn = document.getElementById('addInfoBtn');

    if (addInfoBtn) {
        addInfoBtn.addEventListener('click', () => {
            const di = document.createElement('div');
            di.className = 'form-grid-2 info-row';
            di.style.marginBottom = '10px';
            di.innerHTML = `
                <input type="text" class="input-control info-nombre" placeholder="Nombre">
                <input type="text" class="input-control info-valor" placeholder="Valor">
            `;
            infoContainer.appendChild(di);
        });
    }

    /* ==========================================================================
       CALCULATIONS
       ========================================================================== */
    function attachRowEvents(row) {
        row.querySelector('.remove-row').addEventListener('click', () => {
            row.remove();
            calculateTotals();
        });
        const inputs = row.querySelectorAll('input');
        inputs.forEach(inp => {
            inp.addEventListener('input', () => {
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
        let totalDescuento = 0;
        document.querySelectorAll('#detallesTable .detalle-row').forEach(row => {
            subtotal += parseFloat(row.querySelector('.total-linea').textContent) || 0;
            totalDescuento += parseFloat(row.querySelector('.descuento').value) || 0;
        });

        // 15% IVA Mock Logic
        const base12 = subtotal;
        const base0 = 0;
        const iva = base12 * 0.15;
        const total = subtotal + iva;

        if (document.getElementById('subtotal15Display')) document.getElementById('subtotal15Display').textContent = base12.toFixed(2);
        if (document.getElementById('subtotal0Display')) document.getElementById('subtotal0Display').textContent = base0.toFixed(2);
        if (document.getElementById('ivaDisplay')) document.getElementById('ivaDisplay').textContent = iva.toFixed(2);
        if (document.getElementById('totalDisplay')) document.getElementById('totalDisplay').textContent = total.toFixed(2);
        if (document.getElementById('descuentoDisplay')) document.getElementById('descuentoDisplay').textContent = totalDescuento.toFixed(2);

        // Update Payment Total
        const payRow = document.querySelector('#pagosTable .totalPago');
        if (payRow) payRow.value = total.toFixed(2);
    }

    // Attach to initial rows
    document.querySelectorAll('#detallesTable .detalle-row').forEach(attachRowEvents);
    calculateTotals();


    /* ==========================================================================
       PAYLOAD BUILDER (Entity Structure)
       ========================================================================== */
    function buildInvoicePayload() {
        const fechaRaw = document.getElementById('fechaEmision').value; // yyyy-mm-dd

        const totalSinImpuestos = parseFloat(document.getElementById('subtotal15Display').textContent) +
            parseFloat(document.getElementById('subtotal0Display').textContent);
        const importeTotal = parseFloat(document.getElementById('totalDisplay').textContent);
        const totalDescuento = parseFloat(document.getElementById('descuentoDisplay').textContent);

        // Pagos
        const pagosList = [];
        document.querySelectorAll('#pagosTable tbody tr').forEach(tr => {
            pagosList.push({
                formaPago: tr.querySelector('.formaPago').value,
                total: parseFloat(tr.querySelector('.totalPago').value),
                plazo: parseFloat(tr.querySelector('.plazo').value) || 0,
                unidadTiempo: 'DIAS'
            });
        });

        // Info Adicional
        const infoAdicionalList = [];
        document.querySelectorAll('.info-row').forEach(div => {
            const n = div.querySelector('.info-nombre').value;
            const v = div.querySelector('.info-valor').value;
            if (n && v) infoAdicionalList.push({ nombre: n, valor: v });
        });

        // Detalles
        const detallesList = [];
        document.querySelectorAll('#detallesTable .detalle-row').forEach(row => {
            const qty = parseFloat(row.querySelector('.cantidad').value);
            const price = parseFloat(row.querySelector('.precio').value);
            const desc = parseFloat(row.querySelector('.descuento').value);
            const subtotal = (qty * price) - desc;

            const prodCode = row.querySelector('.codigo').value;
            const prodName = row.querySelector('.descripcion').value;

            detallesList.push({
                cantidad: qty,
                precioUnitario: price,
                descuento: desc,
                subtotal: subtotal,
                producto: {
                    codigoPrincipal: prodCode,
                    nombre: prodName,
                    precioUnitario: price,
                    // Default taxes (backend defaults if missing, but sending here helps clarity)
                    codigoImpuesto: "2",
                    codigoPorcentaje: "4", // 15%
                    tarifa: 15.0
                }
            });
        });

        // Construct Invoice Entity JSON
        return {
            fechaEmision: fechaRaw, // Backend expects ISO LocalDate yyyy-MM-dd
            clienteNombre: document.getElementById('razonSocialComprador').value,
            clienteIdentificacion: document.getElementById('identificacionComprador').value,
            tipoIdentificacionComprador: document.getElementById('tipoIdentificacionComprador').value,
            direccionComprador: document.getElementById('direccionComprador').value,
            // guiaRemision: document.getElementById('guiaRemision').value, // Not in Entity yet?

            total: importeTotal,
            totalSinImpuestos: totalSinImpuestos,
            totalDescuento: totalDescuento,
            propina: 0.0,
            moneda: "DOLAR",

            detalles: detallesList,
            pagos: pagosList,
            infoAdicional: infoAdicionalList
        };
    }


    /* ==========================================================================
       ACTIONS: Draft & Sign
       ========================================================================== */
    const signModal = document.getElementById('signModal');
    const btnOpenSignModal = document.getElementById('btnOpenSignModal');
    const btnConfirmSign = document.getElementById('btnConfirmSign');
    const modalClaveP12 = document.getElementById('modalClaveP12');

    // Draft Button - Assuming class btn-fab first child or added ID
    // Let's rely on text or add ID. The user HTML has '💾 Guardar Borrador' as first button in .action-bar
    const btnSaveDraft = document.querySelector('.action-bar button:first-child');

    if (btnSaveDraft) {
        btnSaveDraft.addEventListener('click', async () => {
            const payload = buildInvoicePayload();
            // Action "BORRADOR" is default
            try {
                btnSaveDraft.textContent = 'Guardando...';
                const resp = await fetch('/api/invoices?accion=BORRADOR', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (resp.ok) {
                    alert('✅ Borrador guardado correctamente');
                    window.location.href = '../pages/history.html';
                } else {
                    const txt = await resp.text();
                    alert('Error guardando borrador: ' + txt);
                }
            } catch (e) {
                alert('Error de conexión');
                console.error(e);
            } finally {
                btnSaveDraft.innerHTML = '💾 Guardar Borrador';
            }
        });
    }

    if (btnOpenSignModal) {
        btnOpenSignModal.addEventListener('click', (e) => {
            e.preventDefault();
            signModal.style.display = 'flex';
        });
    }

    if (btnConfirmSign) {
        btnConfirmSign.addEventListener('click', async () => {
            const clave = modalClaveP12.value;
            if (!clave) { alert('Ingrese contraseña'); return; }

            const payload = buildInvoicePayload();

            // Send with accion=ENVIAR and claveFirma
            try {
                btnConfirmSign.textContent = 'Enviando al SRI...';
                btnConfirmSign.disabled = true;

                // Params
                const params = new URLSearchParams({
                    accion: 'ENVIAR',
                    claveFirma: clave
                });

                const resp = await fetch(`/api/invoices?${params.toString()}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload) // Invoice Entity
                });

                const jsonResp = await resp.json(); // Expecting the updated Invoice object

                if (resp.ok) {
                    // Check status in response
                    if (jsonResp.estado === 'AUTORIZADO') {
                        alert('✅ FACTURA AUTORIZADA POR EL SRI\nClave: ' + (jsonResp.claveAcceso || ''));
                        window.location.href = '../pages/history.html';
                    } else if (jsonResp.estado === 'RECHAZADO') {
                        alert('❌ RECHAZADO POR SRI:\n' + (jsonResp.mensajeSri || 'Verifique errores'));
                    } else {
                        alert('⚠️ Estado SRI: ' + jsonResp.estado);
                    }
                } else {
                    alert('Error del Servidor: ' + (jsonResp.message || JSON.stringify(jsonResp)));
                }
            } catch (e) {
                console.error(e);
                alert('Error de conexión o proceso: ' + e.message);
            } finally {
                btnConfirmSign.textContent = 'Confirmar Emisión';
                btnConfirmSign.disabled = false;
                signModal.style.display = 'none';
            }
        });
    }
});