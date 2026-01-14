document.addEventListener('DOMContentLoaded', () => {
    console.log('Invoice Details Loaded');

    // Get ID from URL
    const params = new URLSearchParams(window.location.search);
    const invoiceId = params.get('id');

    if (invoiceId) {
        fetchInvoiceDetails(invoiceId);
    } else {
        document.querySelector('.page-container').innerHTML = '<p class="error-msg">No se especificó un ID de factura.</p>';
    }

    function fetchInvoiceDetails(id) {
        fetch(`/api/invoices/${id}`)
            .then(res => {
                if (!res.ok) throw new Error('Error al cargar la factura');
                return res.json();
            })
            .then(invoice => {
                renderDetails(invoice, id);
            })
            .catch(err => {
                console.error(err);
                document.querySelector('.page-container').innerHTML = `<p class="error-msg">Error: ${err.message}</p>`;
            });
    }

    function renderDetails(invoice, id) {
        const container = document.querySelector('.details-placeholder');
        if (!container) return; // Safety check

        // Clear initial text
        container.innerHTML = '';
        container.className = 'invoice-success-container';

        // FacturaDTO has infoFactura nested. 'razonSocialComprador' is inside infoFactura.
        const clientName = invoice.infoFactura ? invoice.infoFactura.razonSocialComprador : 'Cargando... mentira es un error';

        // Use the ID from URL (the numeric DB ID), not from the DTO (which is 'comprobante' string attribute)
        const xmlUrl = `/api/invoices/${id}/xml-data`;

        const html = `
            <div class="success-message">
                <h2>✅ Se envio el correo</h2>
                <p class="client-info">Cliente: <strong>${clientName}</strong></p>
                
                <div class="actions-row">
                    <button id="btnRide" class="btn-primary">
                        👁️ Ver RIDE
                    </button>
                    
                    <a href="${xmlUrl}" target="_blank" class="btn-secondary">
                        ⬇️ Ver XML
                    </a>
                </div>
            </div>
        `;

        container.innerHTML = html;

        // Attach event listener for RIDE button
        // Pass the numeric ID (id) explicitly, NOT invoice.id
        document.getElementById('btnRide').addEventListener('click', () => {
            generateAndDownloadPdf(id);
        });
    }

    function generateAndDownloadPdf(id) {
        // Use the numeric ID to fetch the JSON again (or could pass the invoice object directly if compatible)
        // Here we fetch again to be consistent with history.js pattern
        const jsonUrl = `/api/invoices/${id}`;

        const btn = document.getElementById('btnRide');
        const originalText = btn.textContent;
        btn.textContent = 'Generando...';
        btn.disabled = true;

        fetch(jsonUrl)
            .then(res => {
                if (!res.ok) throw new Error("Error fetching Invoice JSON");
                return res.json();
            })
            .then(invoiceJson => {
                return fetch('/api/pdf/generate', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(invoiceJson)
                });
            })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text();
                    throw new Error("Server Error: " + res.status + " " + text);
                }
                return res.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `factura_${id}.pdf`;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            })
            .catch(err => {
                console.error("Error in PDF flow:", err);
                alert("Error al generar PDF: " + err.message);
            })
            .finally(() => {
                btn.textContent = originalText;
                btn.disabled = false;
            });
    }
});
