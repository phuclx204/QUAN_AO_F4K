function closeModal() {
    $('.modal').hide();
}

function openAddModal() {
    $('#addModal').show();
}

function showEditModal(id, name,description) {
    $('#editId').val(id);
    $('#editName').val(name);
    $('#editDescription').val(description);
    $('#editModal').show();
}

$(document).ready(function () {
    loadDatas();

    $('.search-form').on('submit', function (event) {
        event.preventDefault();
        const search = $('input[name="search"]').val();
        loadDatas(1, 5, 'id,desc', search);
    });

    function loadDatas(page = 1, size = 5, sort = 'id,desc', search = '') {
        $.ajax({
            url: '/admin/category/list',
            method: 'GET', // Phương thức HTTP
            data: {page: page, size: size, sort: sort, search: search},
            success: function (response) {
                renderDatas(response.content);
                setupPagination(response.totalPages, page);
                $('#totalData').text(`Tổng  ${response.totalElements}` + ' bản ghi');
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
                <td colspan="4" style="text-align: center;color:red">Không có dữ liệu !</td>
            </tr>
        `);
            $('#pagination').hide();
            return;
        } else {
            $('#pagination').show();
        }


        datas.forEach((i, index) => {
            tbody.append(`
                <tr>
                    <td>${index + 1}</td>
                    <td>${i.name}</td>
                    <td>${i.description}</td>
                    <td>
                        <i class='bx bx-edit' onclick="showEditModal(${i.id}, '${i.name}', '${i.description}')"></i>
                    </td>
                    <td>
                        <input type="checkbox" value="${i.id}" ${i.status === 1 ? 'checked' : ''} 
                               onchange="submitStatusForm(this)">
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


    // Thêm
    $('#addForm').on('submit', function (event) {
        event.preventDefault(); // Ngăn chặn hành vi mặc định của biểu mẫu

        const dataName = $('#addName').val();
        const dataDescription = $('#desciptionName').val();
        $.ajax({
            url: '/admin/category',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({name: dataName,description:dataDescription}),
            success: function (response) {
                $('#addModal').hide();
                loadDatas();
                Swal.fire('Success', 'Thêm mới thành công', 'success');
            },
            error: function (xhr) {
                let errorMessage = 'Không thể thêm mới';
                if (xhr.status === 409) {
                    errorMessage = xhr.responseText;
                } else if (xhr.status === 400 && xhr.responseJSON) {
                    errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                }
                Swal.fire('Error', errorMessage, 'error');
            }
        });
    });

    $('#editForm').on('submit', function (event) {
        event.preventDefault();

        const id = $('#editId').val();
        const dataName = $('#editName').val();
        const dataDescription = $('#editDescription').val();
        $.ajax({
            url: `/admin/category/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({name: dataName,description:dataDescription}),
            success: function (response) {
                $('#editModal').hide();
                loadDatas();
                Swal.fire('Success', 'Cập nhật thành công!', 'success');
            },
            error: function (xhr) {
                let errorMessage = 'Không thể thêm mới';
                if (xhr.status === 409) {
                    errorMessage = xhr.responseText;
                } else if (xhr.status === 400 && xhr.responseJSON) {
                    errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                }
                Swal.fire('Error', errorMessage, 'error');
            }
        });
    });


});

function submitStatusForm(checkbox) {
    const id = checkbox.value;
    const status = checkbox.checked ? 1 : 0;

    $.ajax({
        url: `/admin/category/${id}`,
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

