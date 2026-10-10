/**
 * Student Management System - Main Vanilla JavaScript
 * Handles Theme Toggling, Mobile Sidebar, Confirm Delete Modal, Toasts, and Client-side Validations
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Dark Mode Toggle
    initThemeToggle();

    // 2. Mobile Sidebar Toggle
    initSidebarToggle();

    // 3. Auto-dismiss Toast Notifications
    initToasts();

    // 4. Confirm Delete Modal Dialog
    initDeleteModal();

    // 5. Password Visibility Toggle
    initPasswordToggle();

    // 6. Interactive Marks Auto-Totaling
    initMarksCalculation();
});

function initThemeToggle() {
    const themeBtn = document.getElementById('themeToggleBtn');
    const savedTheme = localStorage.getItem('sms_theme') || 'light';

    if (savedTheme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
        updateThemeIcon(true);
    }

    if (themeBtn) {
        themeBtn.addEventListener('click', () => {
            const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
            if (isDark) {
                document.documentElement.removeAttribute('data-theme');
                localStorage.setItem('sms_theme', 'light');
                updateThemeIcon(false);
            } else {
                document.documentElement.setAttribute('data-theme', 'dark');
                localStorage.setItem('sms_theme', 'dark');
                updateThemeIcon(true);
            }
        });
    }
}

function updateThemeIcon(isDark) {
    const themeBtn = document.getElementById('themeToggleBtn');
    if (!themeBtn) return;
    const icon = themeBtn.querySelector('i');
    if (icon) {
        icon.className = isDark ? 'bi bi-sun-fill' : 'bi bi-moon-stars-fill';
    }
}

function initSidebarToggle() {
    const toggleBtn = document.getElementById('sidebarToggleBtn');
    const sidebar = document.getElementById('appSidebar');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            sidebar.classList.toggle('open');
        });

        document.addEventListener('click', (e) => {
            if (window.innerWidth <= 768 && sidebar.classList.contains('open') && !sidebar.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        });
    }
}

function initToasts() {
    const toasts = document.querySelectorAll('.toast');
    toasts.forEach((toast) => {
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => toast.remove(), 300);
        }, 3200);
    });
}

function initDeleteModal() {
    const modal = document.getElementById('confirmDeleteModal');
    const confirmBtn = document.getElementById('modalConfirmDeleteBtn');
    const cancelBtn = document.getElementById('modalCancelDeleteBtn');
    const modalItemName = document.getElementById('modalItemName');

    if (!modal) return;

    let targetDeleteUrl = '';

    document.querySelectorAll('[data-confirm-delete]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            targetDeleteUrl = btn.getAttribute('href') || btn.getAttribute('data-delete-url');
            const itemName = btn.getAttribute('data-item-name') || 'this record';
            if (modalItemName) {
                modalItemName.textContent = itemName;
            }
            modal.classList.add('active');
        });
    });

    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            modal.classList.remove('active');
            targetDeleteUrl = '';
        });
    }

    if (confirmBtn) {
        confirmBtn.addEventListener('click', () => {
            if (targetDeleteUrl) {
                window.location.href = targetDeleteUrl;
            }
        });
    }

    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });
}

function initPasswordToggle() {
    const toggleBtns = document.querySelectorAll('.toggle-pw-btn');
    toggleBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const input = btn.closest('.input-icon-group').querySelector('input');
            const icon = btn.querySelector('i');
            if (input.type === 'password') {
                input.type = 'text';
                icon.className = 'bi bi-eye-slash';
            } else {
                input.type = 'password';
                icon.className = 'bi bi-eye';
            }
        });
    });
}

function initMarksCalculation() {
    const internalInput = document.getElementById('internalMarksInput');
    const midInput = document.getElementById('midTermMarksInput');
    const endInput = document.getElementById('endTermMarksInput');
    const totalDisplay = document.getElementById('calculatedTotalDisplay');
    const gradeDisplay = document.getElementById('calculatedGradeDisplay');

    if (!internalInput || !midInput || !endInput) return;

    function calculate() {
        const internal = parseFloat(internalInput.value) || 0;
        const mid = parseFloat(midInput.value) || 0;
        const end = parseFloat(endInput.value) || 0;
        const total = Math.min(100, Math.max(0, internal + mid + end));

        let grade = 'F';
        if (total >= 90) grade = 'A+';
        else if (total >= 80) grade = 'A';
        else if (total >= 70) grade = 'B+';
        else if (total >= 60) grade = 'B';
        else if (total >= 50) grade = 'C';

        if (totalDisplay) totalDisplay.textContent = total.toFixed(2);
        if (gradeDisplay) {
            gradeDisplay.textContent = grade;
            gradeDisplay.className = 'badge badge--grade-' + grade.toLowerCase().replace('+', 'plus');
        }
    }

    internalInput.addEventListener('input', calculate);
    midInput.addEventListener('input', calculate);
    endInput.addEventListener('input', calculate);
}
