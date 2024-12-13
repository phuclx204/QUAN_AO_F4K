import {getCommon, syncFormWithDataObject} from "/common/public.js";

const {transformData, convert2Vnd} = getCommon();

(function () {
    "use strict";

    /** Biến toàn cục  **/
    const idFormFilter = 'formFilter';
    const $createDateRanger = $('#createDateRanger');

    const formFilterDefault = {
        search: '',
        orderType: '',
        status: '',
        dayStart: '',
        dayEnd: ''
    }
    const objFilter = Object.assign({}, {...formFilterDefault});

    objFilter.dayStart = moment().startOf('hour').subtract(1, 'year').format("YYYY-MM-DD");
    objFilter.dayEnd = moment().startOf('hour').format("YYYY-MM-DD");

    syncFormWithDataObject({
        selectorParent: idFormFilter,
        dataObject: objFilter,
        initialValues: formFilterDefault,
    });

    /** Function scope **/
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

    const refreshFilter = () => {
        syncFormWithDataObject({
            selectorParent: idFormFilter,
            dataObject: objFilter,
            initialValues: formFilterDefault,
        });


        $createDateRanger.data('daterangepicker').setStartDate(moment().startOf('hour').subtract(1, 'year'));
        $createDateRanger.data('daterangepicker').setEndDate(moment().startOf('hour'));

        objFilter.dayStart = moment().startOf('hour').subtract(1, 'year').format("YYYY-MM-DD");
        objFilter.dayEnd = moment().startOf('hour').format("YYYY-MM-DD");

        $("#filterStatus1").prop("checked", true);

    }

    /** Xử lý form filter **/
    $("#btnRefresh").on("click", function (e) {
        refreshFilter();
        reloadTable();
    })

    $(document).on("keydown", "#filterSearch", async function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            reloadTable();
        }
    });

    $(document).on("change", "#filterOrderType", function (e) {
        reloadTable();
    })

    // Khởi tạo table
    const $tableProduct = $('#products-table').DataTable({
        info: false,
        serverSide: true,
        searching: false,
        bLengthChange: false,
        pageLength: 10,
        ajax: {
            url: '/admin/orders/search-list',
            type: 'GET',
            data: function (data) {
                return {
                    page: Math.floor(data.start / data.length) + 1,
                    size: data.length,
                    search: objFilter.search,
                    startDate: moment(objFilter.dayStart).startOf('day').format("YYYY-MM-DDTHH:mm:ss"),
                    endDate: moment(objFilter.dayEnd).startOf('day').format("YYYY-MM-DDTHH:mm:ss"),
                    status: objFilter.status,
                    orderType: objFilter.orderType
                }
            }
        },
        columns: [
            {
                data: null,
                title: "STT",
                render: (data, type, full, meta) => {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {data: 'code', title: 'Mã đơn hàng'},
            {data: 'toName', title: 'Tên khách hàng'},
            {
                data: 'createdAt',
                title: 'Ngày tạo',
                render: function (data, type, row) {
                    return formatDate(data)
                }
            },
            {data: 'toPhone', title: 'Số điện thoại'},
            {
                data: 'totalPay',
                title: 'Thành tiền',
                render: function (data, type, row) {
                    return convert2Vnd(data);
                }
            },
            {
                data: 'order_type',
                title: 'Loại đơn',
                render: function (data, type, row) {
                    if (data === 'online') {
                        return `<span class="badge bg-success">${data}</span>`;
                    } else {
                        return `<span class="badge bg-danger">${data}</span>`;
                    }
                }
            },
            {
                data: 'status',
                title: 'Trạng thái',
                render: function (data, type, row) {
                    if (data === 0) {
                        return '<span class="badge bg-danger">Đã hủy</span>';
                    } else if (data === 3) {
                        return '<span class="badge bg-success">Hoàn thành</span>';
                    } else if (data === 4) {
                        return '<span class="badge bg-info">Chờ giao hàng</span>';
                    } else if (data === 5) {
                        return '<span class="badge bg-warning">Chờ xác nhận</span>';
                    } else if (data === 6) {
                        return '<span class="badge bg-in-delivery">Đang giao hàng</span>';
                    } else if (data === 7) {
                        return '<span class="badge bg-delivered">Đã giao hàng</span>';
                    } else if (data === 8) {
                        return '<span class="badge bg-wait-pickup">đang xử lý</span>';
                    }
                }
            },
            {
                data: null,
                title: 'Hành động',
                render: function (data, type, row) {
                    return `<td class="table-action">
                              <span data-bs-toggle="tooltip" title="Chi tiết">
                                <a href="/admin/order-detail/${row.code}" class="btn btn-info">Chi tiết</a>
                              </span>
                           </td>`;
                }
            }
        ]
    });

    $('.btn-size-picker').on("click", function (e) {
        reloadTable();
    })

    const reloadTable = () => {
        objFilter.status = $('input[name="filterStatus"]:checked').data('value');

        $tableProduct.ajax.reload(null, false);
    }

    $(document).ready(async function () {
        $createDateRanger.daterangepicker({
            startDate: moment().startOf('hour').subtract(1, 'year'),
            endDate: moment().startOf('hour'),
            locale: {
                format: 'DD/MM/YYYY'
            }
        });

        $createDateRanger.on('apply.daterangepicker', function (ev, picker) {
            objFilter.dayStart = picker.startDate.format('YYYY-MM-DD');
            objFilter.dayEnd = picker.endDate.format('YYYY-MM-DD');

            reloadTable();
        });
    })
})();





