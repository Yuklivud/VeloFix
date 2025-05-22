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

