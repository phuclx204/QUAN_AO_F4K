$(document).ready(function () {
    loadEmployee();

    // Gọi hàm loadCustomer khi thay đổi số lượng hiển thị
    $('#pageSize').on('change', function() {
        const pageSize = $(this).val();
        loadEmployee(1, pageSize);
    });
    // Hàm tải dữ liệu khách hàng từ server
    function loadEmployee(page = 1, size = 5) {
        const search = $('#searchName').val();
        $.ajax({
            url: '/admin/employee/list',
            method: 'GET',
            data: {
                page: page,
                size: size,
                search: search
            },
            success: function (response) {
                $('#totalData').text(`Có  ${response.totalElements}` + ' nhân viên');
                renderCustomer(response.content);
                setupPagination(response.totalPages, page);
            },
            error: function (error) {
                console.error('Không thể tải dữ liệu:', error);
            }
        });
    }

    // Hàm hiển thị dữ liệu vào bảng
    function renderCustomer(customers) {
        const tbody = $('tbody');
        tbody.empty();

        if (customers.length === 0) {
            tbody.append(`
            <tr>
               <td colspan="9" style="text-align: center; color: red;">Không có dữ liệu!</td>
            </tr>
            `);
            return;
        }
        customers.forEach((customer, index) => {
            const genderText = customer.user.gender === 1 ? 'Nam' : 'Nữ'; // Chọn giới tính
            const employmentText = customer.employmentType === 1 ? 'full time' : 'part time'; // Chọn giới tính
            tbody.append(`
                <tr>
                    <td>${index + 1}</td>
                   <td><img src="${customer.user.avatarUrl}" alt="Avatar" width="50" height="50" /></td>
                    <td>${customer.user.fullName}</td>
                    <td>${customer.user.email}</td>
                    <td>${customer.user.numberPhone}</td>
                    <td>${formatDate(customer.user.birthDate)}</td>
                    <td>${genderText}</td>
                    <td>${employmentText}</td>
                    <td class="table-action">
                         <a href="javascript:void(0);" class="action-icon action-update" data-id="${customer.id}"> 
                         <i class="mdi mdi-square-edit-outline"></i></a>
                         <a class="action-icon"><i class="mdi mdi-eye"></i></a>
                    </td>
                </tr>
            `);
        });
    }

    // Hàm thiết lập phân trang
    function setupPagination(totalPages, currentPage) {
        const pagination = $('#pagination');
        if (totalPages === 0) {
            pagination.hide();
            return;
        } else {
            pagination.show();
        }
        pagination.empty();

        const pageSize = parseInt($('#pageSize').val()); // Lấy số lượng hiển thị hiện tại

        // Nút "Trước"
        pagination.append(`
        <button class="btn btn-outline-primary page-button" ${currentPage === 1 ? 'disabled' : ''} data-page="${currentPage - 1}">
            Trước
        </button>
    `);

        // Trường nhập số trang
        pagination.append(`
        <input type="text" id="pageInput" value="${currentPage}" style="width: 50px; text-align: center;" />
        <span> / ${totalPages}</span>
    `);

        // Nút "Tiếp theo"
        pagination.append(`
        <button class="btn btn-outline-primary page-button" ${currentPage === totalPages ? 'disabled' : ''} data-page="${currentPage + 1}">
            Tiếp theo
        </button>
    `);

        $('.page-button').on('click', function () {
            const page = $(this).data('page');
            if (page >= 1 && page <= totalPages) {
                loadEmployee(page, pageSize); // Truyền thêm `pageSize` hiện tại
            }
        });

        // Ràng buộc sự kiện để nhập trang
        $('#pageInput').on('input', function () {
            this.value = this.value.replace(/[^0-9]/g, ''); // Chỉ cho phép nhập số
        });

        $('#pageInput').on('keypress', function (e) {
            if (e.key === 'Enter') {
                let inputPage = parseInt($(this).val());

                if (isNaN(inputPage) || inputPage < 1) {
                    inputPage = 1;
                } else if (inputPage > totalPages) {
                    inputPage = totalPages;
                }

                loadEmployee(inputPage, pageSize); // Truyền thêm `pageSize` hiện tại
            }
        });
    }



    // Hàm định dạng ngày tháng
    function formatDate(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${day}-${month}-${year}`;
    }

    const openModal = (mode, $this) => {

        if (mode === "create") {
            setLabelModal("Thêm mới")
        } else if (mode === "update") {
            setLabelModal("Cập nhật")

        }

        $('#modal-create').modal('show');
    }
    // event button
    $(document).on("click", ".action-create", function (e) {
        e.preventDefault();
        openModal("create")
    })
    $(document).on("click", ".action-update", function (e) {
        e.preventDefault();
        openModal("update", $(this))
    })

});
