document.addEventListener('DOMContentLoaded', () => {

    // Load Company Info from Backend
    fetch('/api/user/profile')
        .then(res => res.json())
        .then(user => {
            console.log('Entrando a invoice.js');
            console.log(user);
            document.getElementById('companyName').textContent = user.razonSocial || user.nombres + ' ' + user.apellidos;
            document.getElementById('companyRuc').textContent = 'RUC: ' + (user.ruc || '...');
            document.getElementById('companyAddress').textContent = 'Dir: ' + (user.direccionMatriz || '...');

            if (user.logoPath) {
                const logoUrl = user.logoPath.startsWith('http') ? user.logoPath : user.logoPath;
                document.getElementById('companyLogo').innerHTML = `<img src="${logoUrl}" style="width:100%; height:100%; object-fit:contain;">`;
            }
        })
        .catch(err => console.error('Error loading profile:', err));

    // Load Products
    let availableProducts = [];
    const loadProducts = () => {
        fetch('/api/products')
            .then(res => res.json())
            .then(products => {
                availableProducts = products;
                renderProductModal();
            })
            .catch(err => console.error('Error loading products:', err));
    };
    loadProducts();

    const renderProductModal = () => {
        const tbody = document.getElementById('productModalBody');
        tbody.innerHTML = '';
        availableProducts.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${p.codigoPrincipal}</td>
                <td>${p.nombre}</td>
                <td>$${(p.valorUnitario || 0).toFixed(2)}</td>
                <td><button type="button" class="btn-link select-prod-btn" data-id="${p.id}">Seleccionar</button></td>
            `;
            tbody.appendChild(tr);
        });

        // Attach events
        tbody.querySelectorAll('.select-prod-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const prodId = e.target.getAttribute('data-id');
                const product = availableProducts.find(p => p.id == prodId);

                if (product) {
                    if (currentRowToFill) {
                        // Edit existing
                        fillRowWithProduct(currentRowToFill, product);
                    } else {
                        // Create New
                        createRow(product);
                    }
                    document.getElementById('productModal').style.display = 'none';
                    currentRowToFill = null;
                }
            });
        });
    };

    let currentRowToFill = null;
    const openProductModal = (row) => {
        currentRowToFill = row;
        document.getElementById('productModal').style.display = 'flex';
    };

    const fillRowWithProduct = (row, product) => {
        row.querySelector('.codigo').value = product.codigoPrincipal;
        row.querySelector('.descripcion').value = product.nombre;
        row.querySelector('.precio').value = (product.valorUnitario || 0).toFixed(2);
        // Trigger calc
        row.querySelector('.precio').dispatchEvent(new Event('input'));
    };

    /* ==========================================================================
       DYNAMIC LINES: Details, Payments, Info
       ========================================================================== */

    // --- DETAILS ---
    const detallesTableBody = document.querySelector('#detallesTable tbody');
    const addRowBtn = document.getElementById('addRowBtn');

    // Helper to create and append arrow (Used by Modal and potentially manual actions if needed)
    function createRow(productData = null) {
        const row = document.createElement('tr');
        row.className = 'detalle-row';

        const code = productData ? productData.codigoPrincipal : '';
        const name = productData ? productData.nombre : '';
        const price = productData ? (productData.valorUnitario || 0).toFixed(2) : '0.00';

        row.innerHTML = `
            <td>
                <div style="display: flex; gap: 2px;">
                    <input type="text" class="codigo" placeholder="Cod" value="${code}">
                    <button type="button" class="btn-icon open-products" title="Buscar Producto">🔍</button>
                </div>
            </td>
            <td><input type="text" class="descripcion" placeholder="Descripción" value="${name}"></td>
            <td><input type="number" class="cantidad" value="1" min="1"></td>
            <td><input type="number" class="precio" value="${price}" step="0.01"></td>
            <td><input type="number" class="descuento" value="0.00" step="0.01"></td>
            <td class="total-linea" style="text-align: right; font-weight: 600;">0.00</td>
            <td><button type="button" class="btn-icon danger remove-row">×</button></td>
        `;

        detallesTableBody.appendChild(row);
        attachRowEvents(row);
        if (productData) {
            // Calculate initial total
            row.querySelector('.precio').dispatchEvent(new Event('input'));
        }
        return row;
    }

    if (addRowBtn) {
        // Open Modal to Add New Product
        addRowBtn.addEventListener('click', () => {
            currentRowToFill = null; // Null means "Create New Row"
            document.getElementById('productModal').style.display = 'flex';
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

        // Attach Modal Event
        const searchBtn = row.querySelector('.open-products');
        if (searchBtn) {
            searchBtn.addEventListener('click', () => openProductModal(row));
        }
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

        // 1. Capture Fixed Fields (Email & Phone)
        const emailVal = document.getElementById('emailComprador').value;
        const phoneVal = document.getElementById('telefonoComprador').value;

        if (emailVal && emailVal.trim() !== '') {
            infoAdicionalList.push({ nombre: 'Email', valor: emailVal.trim() });
        }
        if (phoneVal && phoneVal.trim() !== '') {
            infoAdicionalList.push({ nombre: 'Telefono', valor: phoneVal.trim() });
        }

        // 2. Capture Dynamic Rows
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
                    valorUnitario: price,
                    codigoImpuesto: "2",
                    codigoPorcentaje: "4",
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
            numeroComprobante: `001-001-0000000${Math.floor(Math.random() * 100).toString().padStart(2, '0')}`,
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

    const btnSaveDraft = document.querySelector('.action-bar button:first-child');

    const handleSaveAndOpen = async (accion, simulateSri = false) => {
        const payload = buildInvoicePayload();

        try {
            const btn = accion === 'BORRADOR' && !simulateSri ? btnSaveDraft : btnConfirmSign;
            const originalText = btn.textContent;
            btn.textContent = 'Procesando...';
            btn.disabled = true;

            const resp = await fetch(`/api/invoices?accion=${accion}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const jsonResp = await resp.json();

            if (resp.ok) {
                alert('✅ Factura Procesada. Abriendo Resultados...');
                
                window.location.href = `/invoiceDetails?id=${jsonResp.id}`;

                if (simulateSri || accion === 'ENVIAR') {
                    document.getElementById('signModal').style.display = 'none';
                }
            } else {
                alert('Error: ' + (jsonResp.message || 'Error desconocido'));
            }

            btn.textContent = originalText;
            btn.disabled = false;

        } catch (e) {
            console.error(e);
            alert('Error de conexión');
        }
    };

    if (btnSaveDraft) {
        btnSaveDraft.addEventListener('click', () => handleSaveAndOpen('BORRADOR', false));
    }

    if (btnOpenSignModal) {
        btnOpenSignModal.addEventListener('click', (e) => {
            e.preventDefault();
            signModal.style.display = 'flex';
        });
    }

    if (btnConfirmSign) {
        // User requested "generate xml without signature for now" but show the FAKE SRI RESPONSE.
        // We save as BORRADOR to bypass backend signature requirement, but pass true to open the mock URL.
        btnConfirmSign.addEventListener('click', () => {
            // Close modal first
            document.getElementById('signModal').style.display = 'none';
            handleSaveAndOpen('BORRADOR', true);
        });
    }
});
