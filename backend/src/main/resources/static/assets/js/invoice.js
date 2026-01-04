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
        document.querySelectorAll('#detallesTable .detalle-row').forEach(row => {
            subtotal += parseFloat(row.querySelector('.total-linea').textContent) || 0;
        });

        // 15% IVA Mock Logic
        // In real app, check product tax code.
        const base12 = subtotal;
        const base0 = 0;
        const iva = base12 * 0.15;
        const total = subtotal + iva;

        if (document.getElementById('subtotal15Display')) document.getElementById('subtotal15Display').textContent = base12.toFixed(2);
        if (document.getElementById('subtotal0Display')) document.getElementById('subtotal0Display').textContent = base0.toFixed(2);
        if (document.getElementById('ivaDisplay')) document.getElementById('ivaDisplay').textContent = iva.toFixed(2);
        if (document.getElementById('totalDisplay')) document.getElementById('totalDisplay').textContent = total.toFixed(2);

        // Update Payment Total
        const payRow = document.querySelector('#pagosTable .totalPago');
        if (payRow) payRow.value = total.toFixed(2);
    }

    // Attach to initial rows
    document.querySelectorAll('#detallesTable .detalle-row').forEach(attachRowEvents);
    calculateTotals();


    /* ==========================================================================
       SIGN & EMIT
       ========================================================================== */
    const signModal = document.getElementById('signModal');
    const btnOpenSignModal = document.getElementById('btnOpenSignModal');
    const btnConfirmSign = document.getElementById('btnConfirmSign');
    const modalClaveP12 = document.getElementById('modalClaveP12');

    if (btnOpenSignModal) {
        btnOpenSignModal.addEventListener('click', (e) => {
            e.preventDefault();
            signModal.style.display = 'flex';
        });
    }

    if (btnConfirmSign) {
        btnConfirmSign.addEventListener('click', async () => {
            const clave = modalClaveP12.value;
            // Validar clave
            if (!clave) { alert('Ingrese contraseña'); return; }

            const userId = storedUser ? storedUser.id : 1;

            // Build JSON
            const fechaRaw = document.getElementById('fechaEmision').value; // yyyy-mm-dd
            const [y, m, d] = fechaRaw.split('-');
            const fechaFmt = `${d}/${m}/${y}`;

            const totalSinImpuestos = parseFloat(document.getElementById('subtotal15Display').textContent) + parseFloat(document.getElementById('subtotal0Display').textContent);
            const importeTotal = parseFloat(document.getElementById('totalDisplay').textContent);

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
                if (n && v) infoAdicionalList.push({ nombre: n, value: v });
            });

            // Detalles
            const detallesList = [];
            document.querySelectorAll('#detallesTable .detalle-row').forEach(row => {
                const lt = parseFloat(row.querySelector('.total-linea').textContent);
                detallesList.push({
                    codigoPrincipal: row.querySelector('.codigo').value,
                    descripcion: row.querySelector('.descripcion').value,
                    cantidad: parseFloat(row.querySelector('.cantidad').value),
                    precioUnitario: parseFloat(row.querySelector('.precio').value),
                    descuento: parseFloat(row.querySelector('.descuento').value),
                    precioTotalSinImpuesto: lt,
                    impuestos: [{
                        codigo: "2",
                        codigoPorcentaje: "4", // 15%
                        tarifa: 15,
                        baseImponible: lt,
                        valor: lt * 0.15
                    }]
                });
            });

            const facturaPayload = {
                id: "comprobante",
                version: "1.0.0",
                infoTributaria: {
                    ambiente: "1",
                    tipoEmision: "1",
                    razonSocial: document.getElementById('companyName').textContent,
                    nombreComercial: document.getElementById('companyName').textContent, // Using same
                    ruc: document.getElementById('companyRuc').textContent.replace('RUC: ', '').trim(),
                    codDoc: "01",
                    estab: document.getElementById('estab').value,
                    ptoEmi: document.getElementById('ptoEmi').value,
                    secuencial: document.getElementById('secuencial').value || "000000001",
                    dirMatriz: document.getElementById('companyAddress').textContent.replace('Dir: ', '')
                },
                infoFactura: {
                    fechaEmision: fechaFmt,
                    dirEstablecimiento: document.getElementById('companyAddress').textContent.replace('Dir: ', ''),
                    obligadoContabilidad: "NO",
                    tipoIdentificacionComprador: document.getElementById('tipoIdentificacionComprador').value,
                    razonSocialComprador: document.getElementById('razonSocialComprador').value,
                    identificacionComprador: document.getElementById('identificacionComprador').value,
                    direccionComprador: document.getElementById('direccionComprador').value,
                    totalSinImpuestos: totalSinImpuestos,
                    totalDescuento: 0.00,
                    propina: 0.00,
                    importeTotal: importeTotal,
                    moneda: "DOLAR",
                    pagos: pagosList,
                    totalConImpuestos: [{
                        codigo: "2",
                        codigoPorcentaje: "4",
                        baseImponible: totalSinImpuestos,
                        valor: parseFloat(document.getElementById('ivaDisplay').textContent)
                    }]
                },
                detalles: detallesList,
                infoAdicional: infoAdicionalList
            };

            // Send
            try {
                btnConfirmSign.textContent = 'Enviando...';
                btnConfirmSign.disabled = true;

                const resp = await fetch(`/api/sri/emitir?userId=${userId}&claveFirma=${clave}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(facturaPayload)
                });

                const txt = await resp.text();
                if (resp.ok) {
                    alert('✅ ' + txt);
                    signModal.style.display = 'none';
                } else {
                    alert('❌ ' + txt);
                }
            } catch (e) {
                alert('Connection Error');
            } finally {
                btnConfirmSign.textContent = 'Confirmar Emisión';
                btnConfirmSign.disabled = false;
            }
        });
    }
});