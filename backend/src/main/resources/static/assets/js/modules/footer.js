export function loadFooter() {
    const footerContainer = document.getElementById('footer-container');

    if (!footerContainer) {
        console.warn('Footer container not found on this page');
        return;
    }

    fetch('partials/footer.html')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to load footer: ${response.status}`);
            }
            return response.text();
        })
        .then(html => {
            footerContainer.innerHTML = html;
            console.log('Footer loaded successfully');
        })
        .catch(error => {
            console.error('Error loading footer:', error);
            // Mostrar un footer por defecto o mensaje de error
            footerContainer.innerHTML = `
                <footer style="padding: 20px; text-align: center; background: #f5f5f5;">
                    <p>© ${new Date().getFullYear()} Facto. Todos los derechos reservados.</p>
                </footer>
            `;
        });
}