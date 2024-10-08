// Function to open a modal (either add or edit)
function openModal(modalId) {
    document.getElementById(modalId).style.display = "block";
}

// Function to close both modals
window.closeModal = function () {
    document.getElementById("editModal").style.display = "none";
    document.getElementById("addModal").style.display = "none";
};

// Open "add" modal when the plus icon is clicked
document.querySelector('.bx-plus').onclick = function() {
    openModal('addModal');
};

document.addEventListener("DOMContentLoaded", function () {
    // Open edit modal and populate form
    const editButtons = document.querySelectorAll(".bx-edit");
    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const brandId = this.getAttribute("data-id");
            const brandName = this.getAttribute("data-name");

            document.getElementById("editBrandId").value = brandId;
            document.getElementById("editBrandName").value = brandName;

            openModal('editModal');
        });
    });
});
