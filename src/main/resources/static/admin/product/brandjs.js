function closeModal() {
    $('.modal').hide();
}

function openAddModal() {
    $('#addModal').show();
}

// Hiển thị modal chỉnh sửa và điền dữ liệu
function showEditModal(id, name) {
    $('#editId').val(id);
    $('#editName').val(name);
    $('#editModal').show();
}


// Tải danh sách và xử lý tìm kiếm
$(document).ready(function () {
    loadDatas();

    $('.search-form').on('submit', function (event) {
        event.preventDefault();
        const search = $('input[name="search"]').val();
        loadDatas(1, 10, 'id,dsc', search);
    });

    // Hàm tải thương hiệu
    function loadDatas(page = 1, size = 10, sort = 'id,dsc', search = '') {
        $.ajax({
            url: '/admin/brand/list',
            method: 'GET',
            data: { page: page, size: size, sort: sort, search: search },
            success: function (response) {
                renderDatas(response.content);
                setupPagination(response.totalPages, page);
                // Hiển thị tổng số thương hiệu
                $('#totalData').text(`Tổng  ${response.totalElements}`+' bản ghi');
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
                <td colspan="4" style="text-align: center;color:red">Không có dữ liệu !</td>
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
        pagination.empty();

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

        // Thêm sự kiện cho các nút phân trang
        $('.page-button:not(.disabled)').on('click', function() {
            const page = $(this).data('page');
            loadDatas(page, 10, 'id,dsc', '');
        });
    }


    // Thêm
    $('#addForm').on('submit', function (event) {
        event.preventDefault();

        const dataName = $('#addName').val();
        $.ajax({
            url: '/admin/brand',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ name: dataName }),
            success: function (response) {
                $('#addModal').hide();
                loadDatas();
                Swal.fire('Success', 'Thêm mới thành công', 'success');
            },
            error: function (xhr) {
                if (xhr.status === 409) {
                    Swal.fire('Error', xhr.responseText, 'error');
                } else {
                    console.error('Error adding:', xhr);
                    Swal.fire('Error', 'Không thể thêm mới', 'error');
                }
            }
        });
    });

/// Update brand
    $('#editForm').on('submit', function (event) {
        event.preventDefault();

        const id = $('#editId').val();
        const dataName = $('#editName').val();
        $.ajax({
            url: `/admin/brand/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ name: dataName }),
            success: function (response) {
                $('#editModal').hide();
                loadDatas();
                Swal.fire('Success', 'Cập nhật thành công!', 'success');
            },
            error: function (xhr) {
                if (xhr.status === 409) {
                    Swal.fire('Error', 'Tên đã tồn tại!', 'error');
                }  else {
                    Swal.fire('Error', 'Cập nhật thất bại', 'error');
                }
            }
        });
    });


});
// Cập nhật trạng thái thương hiệu
function submitStatusForm(checkbox) {
    const id = checkbox.value;
    const status = checkbox.checked ? 1 : 0;

    $.ajax({
        url: `/admin/brand/${id}`,
        method: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({ status: status }),
        success: function () {
            Swal.fire('Success', 'Cập nhật trạng thái thành công', 'success');
        },
        error: function () {
            Swal.fire('Error', 'Cập nhật trạng thái thất bại', 'error');
        }
    });
}

