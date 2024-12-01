// const avatarInput = document.getElementById('avatar');
// const avatarPreview = document.getElementById('avatar-preview');
//
// avatarInput.addEventListener('change', function () {
//     if (this.files && this.files[0]) {
//         const reader = new FileReader();
//         reader.onload = function (e) {
//             avatarPreview.src = e.target.result;
//             avatarPreview.style.display = 'block';
//         }
//         reader.readAsDataURL(this.files[0]);
//     }
// });

$(document).ready(function () {
    // === Khởi động ===
    initEventListeners();
    loadCustomer();

    /**
     * Gán sự kiện cho các phần tử HTML
     */
    function initEventListeners() {
        $('#modal-create').on('hidden.bs.modal', function () {
            // Reset các trường input
            $(this).find('form')[0].reset();
            $(this).find('.is-invalid').removeClass('is-invalid');
            $(this).find('.invalid-feedback').text('');
            // Ẩn ảnh preview
            $('#avatar-preview').attr('src', '#').hide();
        });

        // Kiểm tra các trường
        $('#email').on('blur', validateEmail);
        $('#numberPhone').on('blur', validateNumberPhone);
        $('#fullName').on('blur', validateFullName);

        // Thay đổi số lượng hiển thị khách hàng
        $('#pageSize').on('change', function () {
            loadCustomer(1, $(this).val());
        });

        // Gửi form để lưu hoặc cập nhật khách hàng
        $('#formCreate').on('submit', function (e) {
            e.preventDefault();
            saveOrUpdateCustomer();
        });

        // Mở modal tạo mới khách hàng
        $(document).on("click", ".action-create", function (e) {
            e.preventDefault();
            openModal("create");
        });

        // Mở modal cập nhật khách hàng
        $(document).on("click", ".action-update", function (e) {
            e.preventDefault();
            openModal("update", $(this));
        });
    }

    const closeModal = () => {
        $('#modal-create').modal('hide');
    }

    /**
     * Kiểm tra email,sdt,fullname có hợp lệ hay không
     */
    function validateEmail() {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const emailInput = $(this);
        const feedback = emailInput.siblings('.invalid-feedback');

        if (!emailPattern.test(emailInput.val())) {
            emailInput.addClass('is-invalid');
            feedback.text('Email không hợp lệ.');
        } else {
            emailInput.removeClass('is-invalid');
            feedback.text('');
        }
    }

    function validateNumberPhone() {
        const phonePattern = /^(?:\+84|0)\d{9,10}$/;
        if (!phonePattern.test($(this).val())) {
            $(this).addClass('is-invalid').siblings('.invalid-feedback').text('Số điện thoại không hợp lệ (đầu +84 hoặc 0 và 9-10 số)');
        } else {
            $(this).removeClass('is-invalid');
        }
    }

    function validateFullName() {
        if ($(this).val().trim() === '') {
            $(this).addClass('is-invalid').siblings('.invalid-feedback').text('Họ và tên không được để trống.');
        } else {
            $(this).removeClass('is-invalid');
        }
    }

    /**
     * Tải dữ liệu khách hàng
     * @param {number} page Số trang hiện tại
     * @param {number} size Số lượng bản ghi trên mỗi trang
     */
    function loadCustomer(page = 1, size = 5) {
        const search = $('#searchName').val();
        $.get('/admin/customer/list', {page, size, search})
            .done(function (response) {
                renderCustomer(response.content);
                setupPagination(response.totalPages, page);
                $('#totalData').text(`Có ${response.totalElements} khách hàng`);
            })
            .fail(function (error) {
                console.error('Không thể tải dữ liệu:', error);
            });
    }

    /**
     * Hiển thị dữ liệu khách hàng vào bảng
     */
    function renderCustomer(customers) {
        const tbody = $('tbody').empty();

        if (!customers.length) {
            tbody.append('<tr><td colspan="9" class="text-center text-danger">Không có dữ liệu!</td></tr>');
            return;
        }

        customers.forEach((customer, index) => {
            const genderText = customer.gender === 1 ? 'Nam' : 'Nữ';
            tbody.append(`
                <tr>
                    <td>${index + 1}</td>
                    <td><img src="${customer.avatarUrl}" alt="${customer.avatarUrl}" width="50" height="50" /></td>
                    <td>${customer.fullName}</td>
                    <td>${customer.email}</td>
                    <td>${customer.numberPhone}</td>
                    <td>${customer.birthDate}</td>
                    <td>${genderText}</td>
                    <td class="table-action">
                        <a href="javascript:void(0);" class="action-icon action-update" data-id="${customer.id}">
                            <i class="mdi mdi-square-edit-outline"></i>
                        </a>
                        <a href="javascript:void(0);" class="action-icon">
                            <i class="mdi mdi-eye"></i>
                        </a>
                    </td>
                </tr>
            `);
        });
    }

    /**
     * Thiết lập phân trang
     */
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
                loadCustomer(page, pageSize); // Truyền thêm `pageSize` hiện tại
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

                loadCustomer(inputPage, pageSize); // Truyền thêm `pageSize` hiện tại
            }
        });
    }

    /**
     * Lưu hoặc cập nhật khách hàng
     */
    function saveOrUpdateCustomer() {
        const url = $('#formCreate').attr('action');
        const method = $('#customerId').val() ? 'PUT' : 'POST';
        // Lấy dữ liệu từ từng trường
        const fullName = $('#fullName').val();
        const email = $('#email').val();
        const numberPhone = $('#numberPhone').val();
        const gender = $('input[name="gender"]:checked').val(); // Lấy giá trị radio đã chọn
        const birthDate = $('#birthDate').val();
        const user = $('#username').val();
        const pass = $('#password').val();
        const customerId = $('#customerId').val();

        const formData = {
            fullName: fullName,
            email: email,
            numberPhone: numberPhone,
            username: user,
            password: pass,
            gender: gender,
            birthDate: birthDate,
            id: customerId
        };
        // Gửi dữ liệu lên server
        $.ajax({
            url: url,
            method: method,
            data: JSON.stringify(formData),
            contentType: 'application/json',
            success: function (response) {
                closeModal();
                loadCustomer();
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công',
                    text: 'Lưu khách hàng thành công!',
                    timer:2000,
                    timerProgressBar: true,
                });
            },
            error: function (error) {
                console.error('Lỗi:', error);
            }
        });
    }

    /**
     * Mở modal thêm mới hoặc cập nhật khách hàng
     */
    function openModal(mode, $this) {
        if (mode === "create") {
            $('#modal-create .modal-title').text("Thêm mới khách hàng");
            $('#formCreate').attr('action', '/admin/customer');
            $('#customerId').val('');  // Reset id
        } else if (mode === "update") {
            const customerId = $this.data('id');
            $('#modal-create .modal-title').text("Cập nhật khách hàng");
            $('#formCreate').attr('action', `/admin/customer`);

            $.get(`/admin/customer/detail`, {id: customerId})
                .done(function (customer) {
                    $('#birthDate').val(customer.birthDate);
                    $('#customerId').val(customer.id);
                    $('#fullName').val(customer.fullName);
                    $('#email').val(customer.email);
                    $('#username').val(customer.username);
                    $('#password').val(customer.password);
                    $('#numberPhone').val(customer.numberPhone);
                    $('#gender').val(customer.gender);
                    // $('#avatar-preview').attr('src', customer.avatarUrl).show();
                })
                .fail(function () {
                    alert('Không thể tải thông tin khách hàng');
                });
        }

        $('#modal-create').modal('show');
    }
});