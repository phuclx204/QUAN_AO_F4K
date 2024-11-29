import {$ajax, syncFormWithDataObject, getCommon} from "/common/public.js";

const {convert2Vnd} = getCommon();

$(document).ready(async function () {
    "use strict";

    /** Biến toàn cục  **/
    const productId = document.querySelector('meta[name="product-id"]').getAttribute("content");
    const productName = document.querySelector('meta[name="product-name"]').getAttribute("content");

    const idFormFilter = 'formFilter';
    const STATUS_ON = 1;
    const STATUS_OFF = 0;

    const formFilterDefault = {
        search: ''
    }
    const objFilter = Object.assign({}, {...formFilterDefault});

    syncFormWithDataObject({
        selectorParent: idFormFilter,
        dataObject: objFilter,
        initialValues: formFilterDefault,
    });

    /** Function scope **/

    const refreshFilter = () => {
        syncFormWithDataObject({
            selectorParent: idFormFilter,
            dataObject: objFilter,
            initialValues: formFilterDefault,
        });
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

    // Khởi tạo table
    const $tableProduct = $('#products-table').DataTable({
        info: false,
        serverSide: true,
        searching: false,
        bLengthChange: false,
        pageLength: 5,
        ajax: {
            url: "/admin/products/product-detail/"+productId+"/list",
            type: 'GET',
            data: function (data) {
                return {
                    page: Math.floor(data.start / data.length) + 1,
                    size: data.length,
                    search: objFilter.search
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
            {data: 'color.name', title: 'Màu sắc'},
            {data: 'size.name', title: 'Kích cỡ'},
            {
                data: 'price', title: 'Giá tiền', render: (data, type, row) => {
                    return convert2Vnd(data + "")
                }
            },
            {data: 'quantity', title: 'Số lượng'},
            {
                data: 'status',
                title: 'Trạng thái',
                render: function (data, type, row) {
                    if (row.quantity === 0) {
                        return '<span class="badge bg-danger">Hết hàng</span>';
                    } else if (row.quantity >= 1) {
                        return '<span class="badge bg-success">Còn hàng</span>';
                    }
                }
            },
            {
                data: null,
                title: 'Hành động',
                render: function (data, type, row) {
                    const htmlAction = row?.status === 1 ?
                        `<span data-bs-toggle="tooltip" title="Vô hiệu hóa">
                           <a href="javascript:void(0);" class="action-icon action-lock" data-id="${row.id}"> <i class="text-danger mdi mdi-lock-outline"></i></a>
                        </span>`
                        :
                        `<span data-bs-toggle="tooltip" title="Kích hoạt">
                           <a href="javascript:void(0);" class="action-icon action-open" data-id="${row.id}"> <i class="text-success mdi mdi-lock-open-variant-outline"></i></a>
                        </span>`

                    return `<td class="table-action">
                              <span data-bs-toggle="tooltip" title="Cập nhật">
                                <a href="/admin/products/product-detail/${productId}/update/${row.id}" class="action-icon action-update" data-id="${row.id}"> <i class="text-warning mdi mdi-square-edit-outline"></i></a>
                              </span>
                              ${htmlAction}
                             </td>`;
                }
            }
        ]
    });

    const reloadTable = () => {
        $tableProduct.ajax.reload(null, false);
    }

    $(document).on("click", ".action-lock", async function (e) {
        e.preventDefault();
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn vô hiệu hóa sản phẩm không?");
        if (isConfirmed.isConfirmed) {
            const id = $(this).data("id");
            await $ajax.get("/admin/products/product-detail/" + productId + "/update-status/" + id, {status: STATUS_OFF});
            $alter("success", "Thông báo", "Cập nhật thành công");
            reloadTable();
        }
    })

    $(document).on("click", ".action-open", async function (e) {
        e.preventDefault();
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn kích hoạt sản phẩm không?");
        if (isConfirmed.isConfirmed) {
            const id = $(this).data("id");
            await $ajax.get("/admin/products/product-detail/" + productId + "/update-status/" + id, {status: STATUS_ON});
            $alter("success", "Thông báo", "Cập nhật thành công");
            reloadTable();
        }
    })

    $(document).on("click", ".action-update", function (e) {
        console.log('vao update')
    })

    $(document).ready(async function () {

    })
});





