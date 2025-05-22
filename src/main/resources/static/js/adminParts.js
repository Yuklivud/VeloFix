function toggleDetails(element) {
    const details = element.nextElementSibling;
    if (details.style.display === "none" || details.style.display === "") {
        details.style.display = "block";
    } else {
        details.style.display = "none";
    }
}
function addCategory() {
    const input = document.getElementById('newCategoryName');
    const select = document.getElementById('categoryId');
    const newName = input.value.trim();

    if (!newName) {
        alert('Please enter a category name');
        return;
    }

    // Создаём новую опцию и выбираем её
    const newOption = document.createElement('option');
    newOption.value = 'new_' + newName.toLowerCase().replace(/\s+/g, '_');
    newOption.text = newName;
    newOption.selected = true;

    select.appendChild(newOption);
    input.value = '';
}