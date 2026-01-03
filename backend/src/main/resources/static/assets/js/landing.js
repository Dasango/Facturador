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
    fetch('partials/footer.html')
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
            // User asked for "native alerts" (setCustomValidity)
            userInput.setCustomValidity(" "); // Just to mark invalid if needed, or specific msg
            passwordInput.setCustomValidity(data.message || "Credenciales incorrectas");

            // Trigger the bubble
            passwordInput.reportValidity();

            // Clear validity on input so user can type again without error immediately popping up
            userInput.addEventListener('input', () => userInput.setCustomValidity(''), { once: true });
            passwordInput.addEventListener('input', () => passwordInput.setCustomValidity(''), { once: true });
        }

    } catch (error) {
        console.error('Login error:', error);
        // Network error handling
        passwordInput.setCustomValidity("Error de conexión. Intente más tarde.");
        passwordInput.reportValidity();
    }
}
