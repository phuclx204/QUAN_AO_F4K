$(document).ready(function () {
    loadOrders(); // Tải đơn hàng ban đầu

    let currentStatus = null;

    // Xử lý tìm kiếm
    $('#searchForm').on('submit', function (event) {
        event.preventDefault(); // Ngăn chặn hành vi mặc định của form
        loadOrders(); // Gọi lại hàm loadOrders để lấy dữ liệu
    });

    // Xử lý click tab để lọc theo status
    $('.tab').on('click', function () {
        const status = $(this).data('status'); // Lấy status từ data attribute của tab
        console.log(status)
        currentStatus = !status ? null : status;
        loadOrders(1, 10, status); // Gọi lại hàm loadOrders với page và size mặc định
        $('.tab').removeClass('active'); // Cập nhật trạng thái tab
        $(this).addClass('active');
    });

    // Hàm tải dữ liệu đơn hàng từ server
    function loadOrders(page = 1, size = 10, status = null) {
        const search = $('#searchInput').val(); // Lấy giá trị tìm kiếm
        const startDate = $('#startDate').val(); // Lấy ngày bắt đầu
        const endDate = $('#endDate').val(); // Lấy ngày kết thúc

        $.ajax({
            url: '/admin/orders/all',
            method: 'GET',
            data: {
                page: page,
                size: size,
                startDate: startDate,
                endDate: endDate,
                search: search,
                status: status
            },
            success: function (response) {
                renderOrder(response.content); // Hiển thị đơn hàng
                setupPagination(response.totalPages, page); // Thiết lập phân trang
            },
            error: function (error) {
                console.error('Không thể tải dữ liệu:', error);
            }
        });
    }

    // Hàm hiển thị dữ liệu đơn hàng vào bảng
    function renderOrder(orders) {
        const tbody = $('tbody');
        tbody.empty();

        if (orders.length === 0) {
            tbody.append(`
            <tr>
               <td colspan="9" style="text-align: center; color: red;">Không có dữ liệu!</td>
            </tr>
            `);
            return;
        }

        orders.forEach((order, index) => {
            tbody.append(`
                <tr>
                    <td>${index + 1}</td>
                    <td>${order.code}</td>
                    <td>${order.toName}</td>
                    <td>${formatDate(order.createdAt)}</td>
                    <td>${order.toPhone}</td>
                    <td>${order.totalPay}</td>
                    <td>
                        <span class="badge ${order.order_type === 'ONLINE' ? 'online' : order.order_type === 'OFFLINE' ? 'offline' : 'null'}">
                            ${order.order_type}
                        </span>
                    </td>
                    <td>
                        <span class="badge ${getStatusClass(order.status)}">
                            ${getStatusText(order.status)}
                        </span>
                    </td>
                    <td>
                        <a class="btn detail-btn" href="/admin/order-detail/${order.code}">Chi tiết</a>
                    </td>
                </tr>
            `);
        });

    }

    // Hàm lấy class hiển thị cho status
    function getStatusClass(status) {
        switch (status) {
            case 0: return 'cancel';
            case 5: return 'wait-confirm';
            case 2: return 'return';
            case 8: return 'wait-pickup';
            case 4: return 'wait-delivery';
            case 6: return 'in-delivery';
            case 7: return 'delivered';
            case 3: return 'completed';
            default: return 'undefined';
        }
    }

    // Hàm lấy text hiển thị cho status
    function getStatusText(status) {
        switch (status) {
            case 0: return 'Đã hủy';
            case 5: return 'Chờ xác nhận';
            case 2: return 'Trả hàng';
            case 8: return 'đang sử lý';
            case 4: return 'Chờ giao hàng';
            case 6: return 'Đang giao hàng';
            case 7: return 'Đã giao hàng';
            case 3: return 'Hoàn thành';
            default: return 'Không xác định';
        }
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

        // Xử lý sự kiện khi nhấn vào nút trang
        $('.page-button').on('click', function () {
            const page = $(this).data('page');
            loadOrders(page, 10, currentStatus); // Tải lại đơn hàng với trang đã chọn
        });

        // Ràng buộc sự kiện để nhập trang
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

                loadOrders(inputPage); // Tải lại đơn hàng với trang đã nhập
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
        return `${hours}:${minutes}:${seconds} ${day}-${month}-${year}`;
    }
});
