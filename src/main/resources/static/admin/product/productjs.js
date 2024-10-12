// Làm điều hướng url của tải pdf excel
const exportSelect = document.getElementById('exportSelect');

exportSelect.addEventListener('change', function () {
    const selectedValue = this.value;
    if (selectedValue) {
        window.location.href = selectedValue;
    }
});


// Hàm tải danh sách danh mục và thương hiệu vào combobox
function loadCategoriesAndBrands(categoryId = null,brandId = null) {
    $.get('/admin/category/active', function (categories) {
        const categorySelect = $('#addCategory, #editCategory'); // Lấy cả hai select
        categorySelect.empty(); // Xóa các tùy chọn cũ
        categorySelect.append('<option value="">-- Chọn danh mục --</option>');
        categories.forEach(function (category) {
            const selected = category.id == categoryId ? 'selected' : '';
            categorySelect.append(`<option value="${category.id}" ${selected}>${category.name}</option>`);
        });
    });

    $.get('/admin/brand/active', function (brands) {
        const brandSelect = $('#addBrand, #editBrand'); // Lấy cả hai select
        brandSelect.empty(); // Xóa các tùy chọn cũ
        brandSelect.append('<option value="">-- Chọn thương hiệu --</option>');
        brands.forEach(function (brand) {
            const selected = brand.id == brandId ? 'selected' : '';
            brandSelect.append(`<option value="${brand.id}" ${selected}>${brand.name}</option>`);
        });
    });
}


// Đóng tất cả các modal
function closeModal() {
    $('.modal').hide();
}

// Mở modal thêm
function openAddModal() {
    loadCategoriesAndBrands();
    $('#addModal').show();
}

// Hiển thị modal chỉnh sửa và điền dữ liệu
function showEditModal(id, name, categoryId, brandId, thumbnail, description) {
    $('#editId').val(id);
    $('#editName').val(name);
    $('#editThumbnail').val(thumbnail);
    $('#editDescription').val(description);
    loadCategoriesAndBrands(categoryId, brandId);

    $('#editModal').show();
}

// Load danh sách và tìm kiếm
$(document).ready(function () {
    loadDatas();

    $('.search-form').on('submit', function (event) {
        event.preventDefault();
        const search = $('input[name="search"]').val();
        loadDatas(1, 10, 'id,dsc', search);
    });

    function loadDatas(page = 1, size = 10, sort = 'id,dsc', search = '') {
        $.ajax({
            url: '/admin/products/list',
            method: 'GET',
            data: {page: page, size: size, sort: sort, search: search},
            success: function (response) {
                renderDatas(response.content); // Hiển thị danh sách
                setupPagination(response.totalPages, page);

                $('#totalData').text(`Tổng ${response.totalElements} bản ghi`);
            },
            error: function (error) {
                console.error('Không thể tải dữ liệu:', error);
            }
        });
    }

    // Hiển thị dữ liệu trong bảng
    function renderDatas(datas) {
        const tbody = $('tbody');
        tbody.empty(); // Xóa nội dung cũ

        if (datas.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="8" style="text-align: center;color:red">Không có dữ liệu!</td>
                </tr>
            `);
            return;
        }
        //load danh sách ra bảng
        datas.forEach((i, index) => {
            tbody.append(`
                <tr>
                    <td>${index + 1}</td>
                    <td>${i.name}</td>
                    <td>${i.category.name}</td>
                    <td>${i.brand.name}</td>
                    <td>${i.thumbnail}</td>
                    <td>${i.description}</td>
                    <td>
                        <i class='bx bx-edit' onclick="showEditModal(${i.id}, '${i.name}', '${i.category.id}', '${i.brand.id}', '${i.thumbnail}', '${i.description}')"></i>
                    </td>
                    <td>
                        <input type="checkbox" value="${i.id}" ${i.status === 1 ? 'checked' : ''} onchange="submitStatusForm(this)">
                    </td>
                </tr>
            `);
        });
    }

    // Thiết lập phân trang
    function setupPagination(totalPages, currentPage) {
        const pagination = $('#pagination');
        pagination.empty(); // Xóa nội dung cũ

        // Nút Previous
        if (currentPage > 1) {
            pagination.append(`
                <button class="page-button" data-page="${currentPage - 1}">Previous</button>
            `);
        } else {
            pagination.append(`
                <button class="page-button disabled">Previous</button>
            `);
        }

        // Các nút trang
        for (let i = 1; i <= totalPages; i++) {
            pagination.append(`
                <button class="page-button ${i === currentPage ? 'active' : ''}" data-page="${i}">${i}</button>
            `);
        }

        // Nút Next
        if (currentPage < totalPages) {
            pagination.append(`
                <button class="page-button" data-page="${currentPage + 1}">Next</button>
            `);
        } else {
            pagination.append(`
                <button class="page-button disabled">Next</button>
            `);
        }

        // Thêm sự kiện cho các nút trang
        $('.page-button:not(.disabled)').on('click', function () {
            const page = $(this).data('page');
            loadDatas(page, 10, 'id,dsc', '');
        });
    }
// Xử lý thêm sản phẩm
    $('#addForm').on('submit', function (event) {
        event.preventDefault();

        const data = {
            name: $('#addName').val(),
            categoryId: $('#addCategory').val(),
            brandId: $('#addBrand').val(),
            thumbnail: $('#addThumbnail').val(),
            description: $('#addDescription').val()
        };

        $.ajax({
            url: '/admin/products',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function () {
                $('#addModal').hide();
                loadDatas();
                Swal.fire('Success', 'Thêm sản phẩm thành công!', 'success');
            },
            error: function () {
                Swal.fire('Error', 'Thêm sản phẩm thất bại', 'error');
            }
        });
    });
    // Cập nhật thông tin sản phẩm
    $('#editForm').on('submit', function (event) {
        event.preventDefault();

        const id = $('#editId').val();
        const dataName = $('#editName').val();
        const dataCategory = $('#editCategory').val();
        const dataBrand = $('#editBrand').val();
        const dataThumbnail = $('#editThumbnail').val();
        const dataDescription = $('#editDescription').val();

        $.ajax({
            url: `/admin/products/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                name: dataName,
                categoryId: dataCategory,
                brandId: dataBrand,
                thumbnail: dataThumbnail,
                description: dataDescription
            }), // Chuyển đổi dữ liệu thành JSON
            success: function (response) {
                $('#editModal').hide();
                loadDatas();
                Swal.fire('Success', 'Cập nhật thành công!', 'success');
            },
            error: function (xhr) {
                Swal.fire('Error', 'Cập nhật thất bại', 'error');
            }
        });
    });
});
// Cập nhật trạng thái
function submitStatusForm(checkbox) {
    const id = checkbox.value;
    const status = checkbox.checked ? 1 : 0;

    $.ajax({
        url: `/admin/products/${id}`,
        method: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({status: status}),
        success: function () {
            Swal.fire('Success', 'Cập nhật trạng thái thành công', 'success');
        },
        error: function () {
            Swal.fire('Error', 'Cập nhật trạng thái thất bại', 'error');
        }
    });
}
