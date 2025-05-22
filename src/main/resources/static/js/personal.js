document.querySelectorAll('.btn-close').forEach(button => {
    button.addEventListener('click', () => {
        const alert = button.closest('.alert-dismissible');
        if (alert) {
            alert.classList.add('hide');
            setTimeout(() => alert.remove(), 300);
        }
    });
});
document.addEventListener('DOMContentLoaded', function () {
    const btn = document.getElementById('adminBtn');
    const dropdown = document.getElementById('adminDropdown');

    btn?.addEventListener('click', (e) => {
        e.preventDefault();
        dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
    });

    window.addEventListener('click', function (e) {
        if (!e.target.closest('.dropdown')) {
            dropdown.style.display = 'none';
        }
    });
});