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
function toggleDetails(element) {
    const details = element.nextElementSibling;
    if (details.style.display === "none") {
        details.style.display = "block";
    } else {
        details.style.display = "none";
    }
}
let cancelOrderId = null;
let cancelOrderElement = null;

document.querySelectorAll('.cancel-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const orderItem = btn.closest('.order-item');
        cancelOrderId = orderItem.getAttribute('data-order-id');
        cancelOrderElement = orderItem;
        document.getElementById('confirmModal').style.display = 'flex';
    });
});

document.getElementById('confirmYes').addEventListener('click', () => {
    if (!cancelOrderId) return;
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    fetch(`/orders/${cancelOrderId}/cancel`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        }
    })
        .then(res => {
            if (res.ok) {
                cancelOrderElement.remove();
            } else {
                console.error('Ошибка при отмене заказа:', res.status);
                alert('Ошибка при отмене заказа');
            }
        })
        .catch(err => {
            console.error('Fetch error:', err);
            alert('Ошибка сети');
        })
        .finally(() => {
            document.getElementById('confirmModal').style.display = 'none';
            cancelOrderId = null;
            cancelOrderElement = null;
        });
});


document.getElementById('confirmNo').addEventListener('click', () => {
    document.getElementById('confirmModal').style.display = 'none';
    cancelOrderId = null;
    cancelOrderElement = null;
});


