const exportSelect = $('#exportSelect');

exportSelect.on('change', function () {
    const selectedValue = $(this).val();
    if (selectedValue) {
        Swal.fire({
            title: 'Xác nhận',
            text: 'Bạn có chắc muốn xuất danh sách?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Xuất',
            cancelButtonText: 'Hủy',
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = selectedValue;
            }
        });
    }
});

function loadOptions(endpoint, selectElement, defaultOption, selectedId = null) {
    $.get(endpoint, function (data) {
        selectElement.empty().append(`<option value="">${defaultOption}</option>`);
        data.forEach(item => {
            const selected = item.id == selectedId ? 'selected' : '';
            selectElement.append(`<option value="${item.id}" ${selected}>${item.name}</option>`);
        });
    });
}

function loadCategoriesAndBrands(categoryId = null, brandId = null) {
    const categorySelect = $('#addCategory, #editCategory');
    const brandSelect = $('#addBrand, #editBrand');
    loadOptions('/admin/category/active', categorySelect, '-- Chọn danh mục --', categoryId);
    loadOptions('/admin/brand/active', brandSelect, '-- Chọn thương hiệu --', brandId);
}

//chuyển từ màn product sang màn product detail
$('tbody').on('click', 'a', function (event) {
    event.preventDefault();
    const productId = $(this).attr('data-product-id');
    window.location.href = `/admin/products/product-detail/${productId}`;
});

function closeModal() {
    $('.modal').hide();
    $('#addForm')[0].reset();
    $('#editForm')[0].reset();
}

function openAddModal() {
    loadCategoriesAndBrands();
    $('#addModal').show();
    $('#addForm')[0].reset();
}

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

$(document).ready(function () {
    loadDatas();

    $('.search-form').on('submit', function (event) {
        event.preventDefault();
        const search = $('input[name="search"]').val();
        loadDatas(1, 10, 'id,desc', search);
    });

    function loadDatas(page = 1, size = 10, sort = 'id,desc', search = '') {
        $.ajax({
            url: '/admin/products/list',
            method: 'GET',
            data: {page: page, size: size, sort: sort, search: search},
            success: function (response) {
                renderDatas(response.content);
                setupPagination(response.totalPages, page);

                $('#totalData').text(`Tổng ${response.totalElements} sản phẩm`);
            },
            error: function (error) {
                console.error('Không thể tải dữ liệu:', error);
            }
        });
    }

    function renderDatas(datas) {
        const tbody = $('tbody');
        tbody.empty();

        if (datas.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="8" style="text-align: center;color:red">Không có dữ liệu!</td>
                </tr>
            `);
            $('#pagination').hide();
            return;
        } else {
            $('#pagination').show();
        }
        datas.forEach((i) => {
            tbody.append(`
        <tr>
       
           <td>
                <img src="/admin/img/${i.thumbnail}" class="product-image">
            </td>
            <td>${i.name}</td>
            <td>${i.category.name}</td>
            <td>${i.brand.name}</td>
            <td>${i.description}</td>
            <td>${formatDate(i.createdAt)}</td>
              <td>
                <p style="cursor: pointer; display: inline-block; margin-right: 15px;" title="Chỉnh sửa sản phẩm">
                <i class='bx bx-edit' onclick="showEditModal(${i.id}, '${i.name}', '${i.category.id}', '${i.brand.id}', '${i.thumbnail}', '${i.description}')"></i>
                </p>
                <span style="cursor: pointer; display: inline-block;" title="Chi tiết sản phẩm">
                    <a href="#" data-product-id="${i.id}" class="product-detail-link">
                        <i class="bx bx-show"></i>
                    </a>
                </span>
            </td>
            <td>
                <p style="display: inline-block;" title="Thay đổi trạng thái">
                    <input type="checkbox" value="${i.id}" ${i.status === 1 ? 'checked' : ''} 
                    onchange="submitStatusForm(this)" style="width: 20px; height: 20px; cursor: pointer;">
                </p>
            </td>
        </tr>
        `);
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
            loadDatas(page);
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

                loadDatas(inputPage);
            }
        });
    }


    $('#addForm').on('submit', function (event) {
        event.preventDefault();

        const formData = new FormData(this);

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
                $('#addForm')[0].reset();
                Swal.fire('Success', 'Thêm sản phẩm thành công!', 'success');
            },
            error: function (xhr) {
                if (xhr.status === 400 && xhr.responseJSON) {
                    displayErrors(xhr.responseJSON, 'add');
                } else if(xhr.status === 409){
                    Swal.fire('Lỗi',xhr.responseText,'warning')
                } else {
                    Swal.fire('Lỗi', 'Không thể thêm sản phẩm', 'error');
                }
            }
        });
    });

    $('#editForm').on('submit', function (event) {
        event.preventDefault();

        const formData = new FormData(this);
        const id = $('#editId').val();

        $.ajax({
            url: '/admin/products/' + id,
            method: 'PUT',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData,
            success: function () {
                $('#editModal').hide();
                loadDatas();
                $('#editForm')[0].reset();
                Swal.fire('Success', 'Cập nhật sản phẩm thành công!', 'success');
            },
            error: function (xhr) {
                if (xhr.status === 400 && xhr.responseJSON) {
                    displayErrors(xhr.responseJSON, 'edit');
                } else if(xhr.status === 409){
                    Swal.fire('Lỗi',xhr.responseText,'warning')
                } else {
                    Swal.fire('Lỗi', 'Không thể cập nhật sản phẩm', 'error');
                }
            }
        });
    });

    function displayErrors(errors, formType) {
        $(`#${formType}Error`).html('');

        errors.forEach(error => {
            const field = error.field;
            const message = error.defaultMessage;
            $(`#${formType}${capitalize(field)}Error`).html(message);
        });
    }

    function capitalize(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }
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

// Hàm xem trước ảnh khi người dùng chọn file mới
function previewImage(event, previewId) {
    const preview = document.getElementById(previewId);
    const file = event.target.files[0];

    if (file) {
        preview.src = URL.createObjectURL(file);
        preview.style.display = 'block';
    }
}

