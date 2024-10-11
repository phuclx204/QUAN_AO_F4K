// Đóng tất cả các modal
function closeModal() {
    $('.modal').hide(); // Ẩn tất cả các modal
}

// Mở modal thêm
function openAddModal() {
    $('#addModal').show(); // Hiển thị modal thêm
}

// Hiển thị modal chỉnh sửa và điền dữ liệu
function showEditModal(id, name) {
    $('#editId').val(id); // Đặt ID
    $('#editName').val(name); // Đặt tên
    $('#editModal').show(); // Hiển thị modal chỉnh sửa
}


// Tải danh sách và xử lý tìm kiếm
$(document).ready(function () {
    loadDatas(); // Tải danh sách  khi trang được tải

    $('.search-form').on('submit', function (event) {
        event.preventDefault(); // Ngăn chặn hành vi mặc định của biểu mẫu
        const search = $('input[name="search"]').val(); // Lấy từ tìm kiếm
        loadDatas(1, 10, 'id,dsc', search); // Tải dữ liệu với từ tìm kiếm
    });

    // Hàm tải thương hiệu
    function loadDatas(page = 1, size = 10, sort = 'id,dsc', search = '') {
        $.ajax({
            url: '/admin/guarantee/list', // URL để lấy danh sách thương hiệu
            method: 'GET', // Phương thức HTTP
            data: {page: page, size: size, sort: sort, search: search}, // Tham số truy vấn
            success: function (response) {
                renderDatas(response.content); // Hiển thị danh sách
                setupPagination(response.totalPages, page); // Thiết lập phân trang
                // Hiển thị tổng số thương hiệu
                $('#totalData').text(`Tổng  ${response.totalElements}` + ' bản ghi');
            },
            error: function (error) {
                console.error('Không thể tải dữ liệu:', error); // Ghi lại lỗi
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
                <td colspan="4" style="text-align: center;">Không có dữ liệu !</td>
            </tr>
        `);
            return;
        }

        datas.forEach((i, index) => {
            tbody.append(`
                <tr>
                    <td>${index + 1}</td>
                    <td>${i.name}</td>
                    <td>
                        <i class='bx bx-edit' onclick="showEditModal(${i.id}, '${i.name}')"></i>
                    </td>
                    <td>
                        <input type="checkbox" value="${i.id}" ${i.status === 1 ? 'checked' : ''} 
                               onchange="submitStatusForm(this)">
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

        // Thêm sự kiện lắng nghe cho các nút phân trang
        $('.page-button:not(.disabled)').on('click', function () {
            const page = $(this).data('page');
            loadDatas(page, 10, 'id,dsc', ''); // Gọi lại hàm loadBrands với page mới
        });
    }


    // Thêm
    $('#addForm').on('submit', function (event) {
        event.preventDefault(); // Ngăn chặn hành vi mặc định của biểu mẫu

        const dataName = $('#addName').val(); // Lấy tên thương hiệu
        $.ajax({
            url: '/admin/guarantee', // URL để thêm thương hiệu mới
            method: 'POST', // Phương thức HTTP
            contentType: 'application/json', // Đặt loại nội dung
            data: JSON.stringify({name: dataName}), // Chuyển đổi dữ liệu thành JSON
            success: function (response) {
                $('#addModal').hide(); // Đóng modal
                loadDatas(); // Làm mới danh sách thương hiệu
                Swal.fire('Success', 'Thêm mới thành công', 'success'); // Thông báo thành công
            },
            error: function (xhr) {
                if (xhr.status === 409) {
                    Swal.fire('Error', xhr.responseText, 'error'); // Hiển thị thông báo lỗi cụ thể
                } else {
                    console.error('Error adding:', xhr); // Ghi lại các lỗi không mong muốn
                    Swal.fire('Error', 'Không thể thêm mới', 'error'); // Thông báo lỗi chung
                }
            }
        });
    });

/// Update
    $('#editForm').on('submit', function (event) {
        event.preventDefault(); // Ngăn chặn hành vi mặc định của biểu mẫu

        const id = $('#editId').val(); // Lấy ID thương hiệu
        const dataName = $('#editName').val(); // Lấy tên thương hiệu
        $.ajax({
            url: `/admin/guarantee/${id}`, // URL để cập nhật thương hiệu
            method: 'PUT', // Phương thức HTTP
            contentType: 'application/json', // Đặt loại nội dung
            data: JSON.stringify({name: dataName}), // Chuyển đổi dữ liệu thành JSON
            success: function (response) {
                $('#editModal').hide(); // Đóng modal
                loadDatas(); // Làm mới danh sách thương hiệu
                Swal.fire('Success', 'Cập nhật thành công!', 'success'); // Thông báo thành công
            },
            error: function (xhr) {
                if (xhr.status === 409) {
                    Swal.fire('Error', 'Tên đã tồn tại!', 'error'); // Thông báo tên đã tồn tại
                } else {
                    Swal.fire('Error', 'Cập nhật thất bại', 'error'); // Thông báo lỗi khác
                }
            }
        });
    });


});

// Cập nhật trạng thái thương hiệu
function submitStatusForm(checkbox) {
    const id = checkbox.value; // Lấy ID thương hiệu từ checkbox
    const status = checkbox.checked ? 1 : 0; // Đặt trạng thái dựa trên trạng thái checkbox

    $.ajax({
        url: `/admin/guarantee/${id}`, // URL để cập nhật trạng thái thương hiệu
        method: 'PATCH', // Phương thức HTTP
        contentType: 'application/json', // Đặt loại nội dung
        data: JSON.stringify({status: status}), // Chuyển đổi dữ liệu thành JSON
        success: function () {
            Swal.fire('Success', 'Cập nhật trạng thái thành công', 'success'); // Thông báo thành công
        },
        error: function () {
            Swal.fire('Error', 'Cập nhật trạng thái thất bại', 'error'); // Thông báo lỗi khác
        }
    });
}

