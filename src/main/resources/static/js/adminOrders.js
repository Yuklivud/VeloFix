function toggleDetails(element) {
    const details = element.nextElementSibling;
    if (details.style.display === "none" || details.style.display === "") {
        details.style.display = "block";
    } else {
        details.style.display = "none";
    }
}

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

const deletedParts = new Set();

function removePart(button) {
    const partDiv = button.parentElement;
    const hiddenInput = partDiv.querySelector('input[type="hidden"][name*="partId"]');
    if (hiddenInput) {
        const partId = hiddenInput.value;
        if (partId) {
            deletedParts.add(partId);
            updateDeletedPartsInput();
        }
    }
    partDiv.remove();
}

function updateDeletedPartsInput() {
    const deletedPartsInput = document.getElementById('deletedParts');
    deletedPartsInput.value = Array.from(deletedParts).join(',');
}


function addPart() {
    const select = document.getElementById('newPartSelect');
    const qtyInput = document.getElementById('newPartQty');
    const partId = select.value;
    const qty = qtyInput.value;

    if (!partId) {
        alert('Select a part!');
        return;
    }
    if (!qty || qty < 1) {
        alert('Quantity must be at least 1!');
        return;
    }

    const partsContainer = document.getElementById('partsContainer');
    const index = partsContainer.children.length;

    // Создаем div с полями для новой запчасти
    const div = document.createElement('div');

    div.innerHTML = `
      <input type="hidden" name="repairParts[${index}].partId" value="${partId}" />
      <label>${select.options[select.selectedIndex].text}</label>
      <input type="number" name="repairParts[${index}].quantityUsed" min="1" value="${qty}" />
      <button type="button" onclick="removePart(this)">Remove</button>
    `;

    partsContainer.appendChild(div);

    // Сбрасываем выбор
    select.value = '';
    qtyInput.value = '';
}
