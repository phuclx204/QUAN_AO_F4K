$(document).ready(function () {

    loadComboBoxFilter();
    loadProductDetails();


    function loadProductDetails(page = 1, size = 5, search = '') {
        const productId = $('#productId').val();
        $.ajax({
            url: '/admin/products/product-detail/' + productId + '/list',
            method: 'GET',
            data: {page: page, size: size, search: search},
            beforeSend: function () {
                $('#totalData').text('Đang tải dữ liệu...');
            },
            success: function (response) {
                renderProductDetails(response.content);
                setupPagination(response.totalPages, page);
                $('#totalData').text(`${response.totalElements} lọai sản phẩm`);
            },
            error: function (error) {
                console.error('Không thể tải dữ liệu:', error);
                $('#totalData').text('Không thể tải dữ liệu!');
            }
        });
    }

    function setupPagination(totalPages, currentPage) {
        const pagination = $('#pagination');
        pagination.empty();

        pagination.append(`
        <button class="page-button" ${currentPage === 1 ? 'disabled' : ''} data-page="${currentPage - 1}">
            Trước
        </button>
        `);

        pagination.append(`
        <input type="text" id="pageInput" value="${currentPage}" style="width: 50px; text-align: center;" />
        <span> / ${totalPages}</span>
        `);

        pagination.append(`
        <button class="page-button" ${currentPage === totalPages ? 'disabled' : ''} data-page="${currentPage + 1}">
            Tiếp theo
        </button>
        `);

        $('.page-button').on('click', function () {
            const page = $(this).data('page');
            loadProductDetails(page);
        });

        $('#pageInput').on('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
        });

        $('#pageInput').on('keypress', function (e) {
            if (e.key === 'Enter') {
                let inputPage = parseInt($(this).val());

                if (isNaN(inputPage) || inputPage < 1) {
                    inputPage = 1;
                } else if (inputPage > totalPages) {
                    inputPage = totalPages;
                }
                loadProductDetails(inputPage);
            }
        });
    }

    function renderProductDetails(datas) {
        const tbody = $('tbody');
        tbody.empty();

        if (datas.length === 0) {
            tbody.append(`
            <tr>
                <td colspan="8" style="text-align: center; color: red;">Không có dữ liệu!</td>
            </tr>
        `);
            $('#pagination').hide();
            return;
        } else {
            $('#pagination').show();
        }

        datas.forEach((item) => {
            tbody.append(`
        <tr>
            <td>${item.product.name}</td>
            <td>${item.product.category.name}</td>
            <td>${item.product.brand.name}</td>
            <td>${item.size.name}</td>
            <td>${item.color.name}</td>
            <td>${item.product.description}</td>
            <td>${item.price}</td>
            <td>${item.quantity}</td>
            <td>${formatDate(item.createdAt)}</td>
            <td>
                <p style="cursor: pointer; display: inline-block; margin-right: 15px;" title="Chỉnh sửa">
                    <i class='bx bx-edit' onclick="openEditModal(${item.id},${item.product.id},${item.color.id},
                    ${item.size.id},${item.price},${item.quantity})"></i>
                </p>
                <p style="cursor: pointer; display: inline-block;" title="Xóa">
                    <i class='bx bx-trash' onclick="deleteProductDetail(${item.id})"></i>
                </p>
            </td>
        </tr>
    `);
        });
    }


    window.openAddModal = function () {
        $('#addModal').show();
        $('#addForm')[0].reset();
        loadComboBoxFilter();
    }
    window.openEditModal = function (id, productId, colorId, sizesId, price, quantity) {
        $('#editProductDetailId').val(id);
        $('#editProductId').val(productId);
        loadComboBoxFilter(colorId, sizesId);
        $('#editPrice').val(price);
        $('#editQuantity').val(quantity);

        $('#editModal').show();
    }


    // Hàm đóng modal
    window.closeModal = function (modalId) {
        $('#' + modalId).hide();
    }

    // Hàm tải các danh sách combo box
    function loadComboBoxFilter(colorId = null, sizeId = null) {
        $.when(
            $.get('/admin/color/active'),
            $.get('/admin/size/active')
        ).done(function (colors, sizes) {
            fillSelectBox('#selectColor, #selectAddColor, #selectEditColor', colors[0], colorId, '-- Màu sắc --');
            fillSelectBox('#selectSize, #selectAddSize, #selectEditSize', sizes[0], sizeId, '-- Kích thước --');
        });
    }

    // Hàm điền dữ liệu vào combo box
    function fillSelectBox(selector, data, selectedId, placeholder) {
        const select = $(selector);
        select.empty();
        select.append(`<option value="">${placeholder}</option>`);
        data.forEach(item => {
            const selected = item.id === selectedId ? 'selected' : '';
            select.append(`<option value="${item.id}" ${selected}>${item.name}</option>`);
        });
    }


    // Hàm thêm chi tiết sản phẩm
    $('#addForm').on('submit', function (e) {
        e.preventDefault();
        const productId = $('#productId').val();
        const data = {
            productId: productId,
            sizeId: $('#selectAddSize').val(),
            colorId: $('#selectAddColor').val(),
            price: $('#addPrice').val(),
            quantity: $('#addQuantity').val()
        };

        $.ajax({
            url: '/admin/products/product-detail/' + productId + '/add',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function () {
                closeModal('addModal');
                Swal.fire('Success', 'Lưu thành công!', 'success');
                loadProductDetails();
            },
            error: function (error) {
                Swal.fire('Error', 'Lưu thất bại!', 'error');
            }
        });
    });

    // Hàm cập nhật chi tiết sản phẩm
    $('#editForm').on('submit', function (e) {
        e.preventDefault();

        const productId = $('#editProductId').val();
        const id = $('#editProductDetailId').val();
        const data = {
            sizeId: $('#selectEditSize').val(),
            colorId: $('#selectEditColor').val(),
            price: $('#editPrice').val(),
            quantity: $('#editQuantity').val(),
            status: 1
        };

        $.ajax({
            url: `/admin/products/product-detail/${productId}/update/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function () {
                closeModal('editModal');
                Swal.fire('Success', 'Cập nhật thành công!', 'success');
                loadProductDetails();
            },
            error: function (error) {
                Swal.fire('Error', 'Cập nhật thất bại!', 'error');
            }
        });
    });
//     end cập nhật
});

function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${hours}:${minutes}:${seconds} ${day}-${month}-${year}`;
}
