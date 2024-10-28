$(document).ready(function () {
    loadOrders();

    $('.search-form').on('submit', function (event) {
        event.preventDefault();
        const search = $('input[name="search"]').val();
        const startDate = $('#startDate').val();
        const endDate = $('#endDate').val();

        const filter = buildFilterString("", startDate, endDate);
        loadOrders(1, 10, 'id,desc', search, filter);
    });

    $('.tab').on('click', function () {
        const status = $(this).data('status');
        const filter = buildFilterString(status); // Tạo filter với status
        loadOrders(1, 10, 'id,desc', '', filter);

        $('.tab').removeClass('active');
        $(this).addClass('active');
    });

    function loadOrders(page = 1, size = 10, sort = 'id,desc', search = '',filter='') {
        $.ajax({
            url: '/admin/orders/all',
            method: 'GET',
            data: {page: page, size: size, sort: sort, search: search,filter:filter},
            success: function (response) {
                renderOrder(response.content)
                setupPagination(response.totalPages,page)
            },
            error: function (error) {
                console.error('Không thể tải dữ liệu:', error);
            }
        })
    }

    function buildFilterString(status, startDate, endDate) {
        let filters = [];
        if (status !== "") {
            filters.push(`status==${status}`);
        }
        // Thêm điều kiện cho ngày bắt đầu và ngày kết thúc
        if (startDate) {
            filters.push(`createdAt>=${startDate}`);
        }
        if (endDate) {
            filters.push(`createdAt<=${endDate}`);
        }
        return filters.join(';'); // Ghép các điều kiện bằng dấu `;`
    }

    function renderOrder(orders) {
        const tbody = $('tbody');
        tbody.empty();

        if (orders.length === 0) {
            tbody.append(`
            <tr>
               <td colspan="4" style="text-align: center;color:red">Không có dữ liệu !</td>
            </tr>
            `);
            return;
        }

        orders.forEach((i, index) => {
            tbody.append(`
            <tr>
                <td>${index + 1}</td>
                <td>${i.code}</td>
                <td>${i.toName}</td>
                <td>${formatDate(i.createdAt)}</td>
                <td>${i.toPhone}</td>
                <td>${i.totalPay}</td>
                <td>
                    <span class="badge ${i.orderType === 'online' ? 'online' : 'offline'}">${i.orderType}</span>
                </td>
                 <td>
                    <span class="badge ${getStatusClass(i.status)}">
                        ${getStatusText(i.status)}
                    </span>
                </td>
                <td>
                    <button class="btn detail-btn">Chi tiết</button>
                </td>
            </tr>
            `)
        })

    }

    function getStatusClass(status) {
        switch (status) {
            case 0: return 'cancel';
            case 1: return 'wait-confirm';
            case 3: return 'wait-pickup';
            case 4: return 'wait-delivery';
            case 6: return 'in-delivery';
            case 7: return 'delivered';
            case 8: return 'completed';
            default: return 'wait';
        }
    }

    function getStatusText(status) {
        switch (status) {
            case 0: return 'Đã hủy';
            case 1: return 'Chờ xác nhận';
            case 2: return 'Trả hàng';
            case 3: return 'Chờ lấy hàng';
            case 4: return 'Chờ giao hàng';
            case 6: return 'Đang giao hàng';
            case 7: return 'Đã giao hàng';
            case 8: return 'Hoàn thành';
            default: return 'Chờ thanh toán';
        }
    }

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

        $('.page-button').on('click', function () {
            const page = $(this).data('page');
            loadOrders(page);
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

                loadOrders(inputPage);
            }
        });
    }

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



