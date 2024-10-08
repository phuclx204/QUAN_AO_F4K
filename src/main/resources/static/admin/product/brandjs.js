let currentBrandId = null; // Lưu ID thương hiệu hiện tại
let currentBrandName = ''; // Lưu tên thương hiệu hiện tại
let currentCheckbox = null; // Lưu checkbox hiện tại

// Function to open a modal (either add or edit)
function openModal(modalId) {
    document.getElementById(modalId).style.display = "block";
}

// Function to close modals
window.closeModal = function () {
    document.getElementById("editModal").style.display = "none";
    document.getElementById("addModal").style.display = "none";
    document.getElementById("confirmStatusModal").style.display = "none"; // Đóng modal xác nhận trạng thái
};

// Open "add" modal when the plus icon is clicked
document.querySelector('.bx-plus').onclick = function() {
    openModal('addModal');
};

// Populate edit form and open modal
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".bx-edit");
    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const brandId = this.getAttribute("data-id");
            const brandName = this.getAttribute("data-name");

            // Cập nhật giá trị cho trường ẩn
            document.getElementById("editBrandId").value = brandId;
            document.getElementById("editBrandName").value = brandName;

            // Cập nhật action của form với ID
            const editForm = document.getElementById("editForm");
            editForm.setAttribute("action", `/admin/brand/update/${brandId}`);

            openModal('editModal');
        });
    });
});

// Function to submit the form to update the brand status
function submitStatusForm(checkbox) {
    currentCheckbox = checkbox; // Lưu checkbox hiện tại
    currentBrandId = checkbox.value; // Lấy ID của thương hiệu từ checkbox
    currentBrandName =  checkbox.closest('tr').querySelector('.bx-edit').getAttribute('data-name'); // Lấy tên thương hiệu

    // Hiển thị modal xác nhận với tên thương hiệu
    document.getElementById('confirmMessage').textContent = `Bạn có chắc chắn muốn ${checkbox.checked ? 'kích hoạt' : 'ngừng hoạt động'} thương hiệu "${currentBrandName}" không?`;
    openModal('confirmStatusModal');
}

function confirmStatusChange() {
    const status = currentCheckbox.checked ? 1 : 0; // Kiểm tra trạng thái
    const statusForm = document.getElementById('statusForm');

    // Gán giá trị vào các trường ẩn của form
    document.getElementById('statusInput').value = status; // Gán giá trị status vào input ẩn
    document.getElementById('brandIdInput').value = currentBrandId; // Gán ID thương hiệu vào input ẩn
    document.getElementById('nameInput').value = currentBrandName; // Gán name thương hiệu vào input ẩn

    // Gửi form cập nhật trạng thái
    statusForm.action = `/admin/brand/update-status/${currentBrandId}`; // Cập nhật action của form
    statusForm.submit(); // Tự động submit form
    closeModal(); // Đóng modal
}

function cancelStatusChange() {
    // Đặt checkbox về trạng thái ban đầu
    if (currentCheckbox) {
        currentCheckbox.checked = !currentCheckbox.checked; // Đảo ngược trạng thái
    }
    closeModal(); // Đóng modal
}
