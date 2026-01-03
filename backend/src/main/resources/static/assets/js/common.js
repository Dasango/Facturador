// Global User State
const currentUser = {
    id: 1, // Hardcoded for now, replace with session/localStorage
    nombre: 'Juan Perez', // Default
    logo: null
};

document.addEventListener('DOMContentLoaded', () => {
    loadLayout();
});

async function loadLayout() {
    // Load Sidebar
    const sidebarContainer = document.getElementById('sidebar-container');
    if (sidebarContainer) {
        try {
            const resp = await fetch('/partials/sidebar.html');
            sidebarContainer.innerHTML = await resp.text();
            highlightSidebar();
        } catch (e) {
            console.error('Error loading sidebar', e);
        }
    }

    // Load Header
    const headerContainer = document.getElementById('header-container');
    if (headerContainer) {
        try {
            const resp = await fetch('/partials/header.html');
            headerContainer.innerHTML = await resp.text();
            initHeaderLogic();
        } catch (e) {
            console.error('Error loading header', e);
        }
    }
}

function initHeaderLogic() {
    // 1. Remove Hamburger if present (User requested clean left side)
    const menuBtn = document.querySelector('.menu-btn');
    if (menuBtn) {
        menuBtn.style.display = 'none';
    }

    // 2. Set User Name from Storage/State
    const storedUser = JSON.parse(localStorage.getItem('user'));
    const userName = storedUser ? storedUser.nombre : currentUser.nombre;

    const headerNameSpan = document.getElementById('headerUserName');
    if (headerNameSpan) {
        headerNameSpan.textContent = userName;
    }

    // 3. Dropdown Logic
    const userWidget = document.querySelector('.user-profile-widget');
    const dropdownMenu = document.getElementById('userDropdownMenu');

    if (userWidget && dropdownMenu) {
        userWidget.addEventListener('click', (e) => {
            e.stopPropagation(); // Prevent immediate close
            const isVisible = dropdownMenu.style.display === 'block';
            dropdownMenu.style.display = isVisible ? 'none' : 'block';
        });

        // Close on click outside
        document.addEventListener('click', () => {
            dropdownMenu.style.display = 'none';
        });

        // Prevent closing when clicking inside menu
        dropdownMenu.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }
}

function highlightSidebar() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-item').forEach(link => {
        if (link.getAttribute('href') && currentPath.includes(link.getAttribute('href'))) {
            link.classList.add('active');
        }
    });
}
