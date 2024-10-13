// Làm điều hướng url của tải pdf excel
const exportSelect = document.getElementById('exportSelect');

exportSelect.addEventListener('change', function () {
    const selectedValue = this.value;
    if (selectedValue) {
        window.location.href = selectedValue;
    }
});


// Hàm tải danh sách danh mục và thương hiệu vào combobox
function loadCategoriesAndBrands(categoryId = null, brandId = null) {
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
    loadCategoriesAndBrands(categoryId, brandId);
    $('#editDescription').val(description);

    // Hiển thị ảnh đã tồn tại
    const thumbnailPreview = $('#editThumbnailPreview');
    if (thumbnail) {
        thumbnailPreview.attr('src', '/admin/img/' + thumbnail).show();
    } else {
        thumbnailPreview.hide();
    }

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

// Load danh sách ra bảng
        datas.forEach((i, index) => {
            tbody.append(`
        <tr>
            <td>${index + 1}</td>
            <td><img src="/admin/img/${i.thumbnail}"></td>
            <td>${i.name}</td>
            <td>${i.category.name}</td>
            <td>${i.brand.name}</td>
            <td>${i.description}</td>
            <td>${formatDate(i.createdAt)}</td>
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

    $('#addForm').on('submit', function (event) {
        event.preventDefault();

        const formData = new FormData(this); // Lấy toàn bộ dữ liệu từ form

        $.ajax({
            url: '/admin/products',
            method: 'POST',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData,
            cache: false,
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

    // Cập nhật sản phẩm
    $('#editForm').on('submit', function (event) {
        event.preventDefault();

        const formData = new FormData(this);
        const id = $('#editId').val();  // Lấy ID từ input
        console.log('ID:', id);  // Log ID để kiểm tra

        $.ajax({
            url: '/admin/products/' + id,  // Đường dẫn đúng với ID
            method: 'PUT',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData,
            success: function () {
                $('#editModal').hide();
                loadDatas();
                Swal.fire('Success', 'Cập nhật sản phẩm thành công!', 'success');
            },
            error: function (xhr) {
                console.error('Error response:', xhr);
                Swal.fire('Error', 'Cập nhật sản phẩm thất bại', 'error');
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
// Hàm định dạng ngày giờ
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
// Preview ảnh khi người dùng chọn file mới
function previewImage(event) {
    const preview = document.getElementById('editThumbnailPreview');
    const file = event.target.files[0];

    if (file) {
        preview.src = URL.createObjectURL(file);
        preview.style.display = 'block';
    }
}
function showImageAddModal() {
    const preview = document.getElementById('addThumbnailPreview');
    const file = event.target.files[0];

    if (file) {
        preview.src = URL.createObjectURL(file);
        preview.style.display = 'block';
    }
}