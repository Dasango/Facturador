document.addEventListener('DOMContentLoaded', () => {
    console.log('Landing page loaded');

    // Smooth scrolling
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                targetElement.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // Load Footer Partial
    fetch('/partials/footer.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('footer-container').innerHTML = data;
        });

    // Animations
    const observerOptions = { threshold: 0.1 };
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);

    document.querySelectorAll('.feature-card, .pricing-card, .testimonial-card').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(el);
    });

    // --- LOGIN FORM HANDLING ---
    const loginForm = document.querySelector('.hero-login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // --- LÓGICA DE PASARELA DE PAGOS (NUEVO) ---
    const modal = document.getElementById('paymentModal');
    const contractBtns = document.querySelectorAll('.btn-contract');
    const closeBtn = document.getElementById('closePayment');
    const planTitle = document.getElementById('planTitleDisplay');
    const fakePaymentForm = document.getElementById('fakePaymentForm');
    const loadingArea = document.getElementById('paymentLoading');
    const successArea = document.getElementById('paymentSuccess');

    if (modal && contractBtns.length > 0) {
        // Abrir Modal y configurar el plan
        contractBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const planName = btn.getAttribute('data-plan');
                planTitle.innerText = planName;
                
                // Resetear estado del modal
                modal.style.display = 'flex';
                fakePaymentForm.classList.remove('hidden');
                loadingArea.style.display = 'none';
                successArea.style.display = 'none';
            });
        });

        // Cerrar Modal (Botón X)
        if (closeBtn) {
            closeBtn.onclick = () => { modal.style.display = 'none'; };
        }

        // Cerrar al hacer clic fuera del contenido
        window.onclick = (event) => {
            if (event.target == modal) modal.style.display = 'none';
        };

        // Simular Proceso de Pago
        if (fakePaymentForm) {
            fakePaymentForm.addEventListener('submit', (e) => {
                e.preventDefault();
                
                // Mostrar estado de carga
                fakePaymentForm.classList.add('hidden');
                loadingArea.style.display = 'block';

                // Simular validación bancaria de 2.5 segundos
                setTimeout(() => {
                    loadingArea.style.display = 'none';
                    successArea.style.display = 'block';
                }, 2500);
            });
        }
    }
});

async function handleLogin(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    // Convert to URLSearchParams for Spring Security Form Login
    const params = new URLSearchParams();
    formData.forEach((value, key) => {
        params.append(key, value);
    });

    const userInput = form.querySelector('input[name="username"]');
    const passwordInput = form.querySelector('input[name="password"]');

    try {
        const response = await fetch('/login', {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });

        // Spring Security success/failure handlers return JSON now
        const data = await response.json();

        if (response.ok && data.success) {
            // Success: clear custom validity and redirect
            userInput.setCustomValidity('');
            passwordInput.setCustomValidity('');
            window.location.href = '/home'; // Clean URL
        } else {
            // Failure: Show native alert
            console.warn('Login failed:', data.message);

            // Set message on the specific input (or both)
            userInput.setCustomValidity(" "); 
            passwordInput.setCustomValidity(data.message || "Credenciales incorrectas");

            // Trigger the bubble
            passwordInput.reportValidity();

            // Clear validity on input
            userInput.addEventListener('input', () => userInput.setCustomValidity(''), { once: true });
            passwordInput.addEventListener('input', () => passwordInput.setCustomValidity(''), { once: true });
        }

    } catch (error) {
        console.error('Login error:', error);
        passwordInput.setCustomValidity("Error de conexión. Intente más tarde.");
        passwordInput.reportValidity();
    }
}
