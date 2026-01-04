document.addEventListener('DOMContentLoaded', () => {
    loadLayout();
});

async function loadLayout() {
    // 1. Cargar Sidebar
    const sidebarContainer = document.getElementById('sidebar-container');
    if (sidebarContainer) {
        try {
            const resp = await fetch('/partials/sidebar.html');
            sidebarContainer.innerHTML = await resp.text();
            highlightSidebar(); // Marcar pestaña activa
        } catch (e) {
            console.error('Error loading sidebar', e);
        }
    }

    // 2. Cargar Header
    const headerContainer = document.getElementById('header-container');
    if (headerContainer) {
        try {
            const resp = await fetch('/partials/header.html');
            headerContainer.innerHTML = await resp.text();

            // UNA VEZ CARGADO EL HTML, BUSCAMOS LOS DATOS DEL USUARIO REAL
            loadUserProfile();

            // Activamos la lógica visual del menú desplegable
            initDropdown();
        } catch (e) {
            console.error('Error loading header', e);
        }
    }
}

// --- LÓGICA DE USUARIO (Conexión con Backend) ---
async function loadUserProfile() {
    try {
        // Llamamos al endpoint que acabamos de crear
        const response = await fetch('/api/user/profile');

        // Si el token expiró o no hay sesión, al Login
        if (response.status === 401) {
            window.location.href = '/';
            return;
        }

        if (response.ok) {
            const user = await response.json();

            // Buscamos el elemento donde va el nombre
            const headerNameSpan = document.getElementById('headerUserName');

            if (headerNameSpan) {
                // Pintamos Nombres y Apellidos reales de la BD
                headerNameSpan.textContent = `${user.nombres} ${user.apellidos}`;
            }
        }
    } catch (error) {
        console.error('Error cargando perfil:', error);
    }
}

// --- LÓGICA UI (Dropdown y Sidebar) ---

function initDropdown() {
    const userWidget = document.querySelector('.user-profile-widget');
    const dropdownMenu = document.getElementById('userDropdownMenu');

    if (userWidget && dropdownMenu) {
        userWidget.addEventListener('click', (e) => {
            e.stopPropagation();
            const isVisible = dropdownMenu.style.display === 'block';
            dropdownMenu.style.display = isVisible ? 'none' : 'block';
        });

        // Cerrar al hacer click fuera
        document.addEventListener('click', () => {
            dropdownMenu.style.display = 'none';
        });

        // Evitar cerrar si clickeas dentro del menú
        dropdownMenu.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }
}


function highlightSidebar() {
    // 1. Obtener la URL actual del navegador (ej: "/products" o "/pages/products.html")
    const currentPath = window.location.pathname;

    // 2. Recorrer todos los links del sidebar
    document.querySelectorAll('.nav-item').forEach(link => {
        // Quitamos la clase active primero por si acaso
        link.classList.remove('active');

        const href = link.getAttribute('href'); // ej: "/invoice"

        // 3. Validación Inteligente
        // Si el href existe Y la URL actual lo contiene...
        if (href && href !== '#') {
            // Truco: Comparamos sin el slash inicial por si las rutas relativas varían
            const cleanHref = href.replace(/^\//, ''); // "invoice"

            if (currentPath.includes(cleanHref)) {
                link.classList.add('active');
            }
        }
    });
}