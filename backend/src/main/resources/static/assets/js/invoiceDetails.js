document.addEventListener('DOMContentLoaded', () => {
    console.log('Invoice Details Loaded');
    // Get ID from URL
    const params = new URLSearchParams(window.location.search);
    const invoiceId = params.get('id');

    if (invoiceId) {
        const div = document.querySelector('.details-placeholder');
        if (div) {
            div.textContent = `hola soy un div y me pasaron el id:${invoiceId}`;
        }
    }
});
