import {$ajax, syncFormWithDataObject} from "/common/public.js";

$(document).ready(async function () {
    "use strict";

    /** Biến toàn cục  **/
    const imageBlank = "https://firebasestorage.googleapis.com/v0/b/clothes-f4k.appspot.com/o/common%2Fdata_not_found.png?alt=media&token=36148ded-ba2c-4207-8525-2da16e7a8557";
    const idFormFilter = 'formFilter';
    const STATUS_ON = 1;
    const STATUS_OFF = 0;

    const formFilterDefault = {
        search: '',
        status: '1',
        categoryId: '',
        brandId: ''
    }
    const objFilter = Object.assign({}, {...formFilterDefault});

    syncFormWithDataObject({
        selectorParent: idFormFilter,
        dataObject: objFilter,
        initialValues: formFilterDefault,
    });

    /** Function scope **/
    const addOptionFilter = async (url, selectElement, defaultOption = "Tất cả") => {
        try {
            const res = await $ajax.get(url);
            selectElement.empty().append(`<option value="">${defaultOption}</option>`);
            res.forEach(item => {
                selectElement.append(`<option value="${item.id}">${item.name}</option>`);
            });
        } catch (e) {
            console.log(e)
        }
    }

    const loadOptionFilter = () => {
        addOptionFilter('/admin/category/active', $('#filterCategory'));
        addOptionFilter('/admin/brand/active', $('#filterBrand'));
    }

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

    $(document).on("change", "#filterStatus, #filterCategory, #filterBrand", function (e) {
        reloadTable();
    })

    // Khởi tạo table
    const $tableProduct = $('#products-table').DataTable({
        info: false,
        serverSide: true,
        searching: false,
        bLengthChange: false,
        pageLength: 5,
        ajax: {
            url: "/admin/products/search-list",
            type: 'GET',
            data: function (data) {
                return {
                    page: Math.floor(data.start / data.length) + 1,
                    size: data.length,
                    search: objFilter.search,
                    status: objFilter.status,
                    categoryId: objFilter.categoryId,
                    brandId: objFilter.brandId
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
            {
                data: 'pathImg',
                title: null,
                render: (data, type, row, meta) => {
                    const fileImg = row.image?.fileUrl ? row.image?.fileUrl : imageBlank;
                    return `<img src="${fileImg}" alt="contact-img" title="contact-img" class="rounded me-3" height="48" />`
                }
            },
            {data: 'name', title: 'Tên sản phẩm'},
            {data: 'category.name', title: 'Danh mục'},
            {data: 'brand.name', title: 'Thương hiệu'},
            {
                data: 'status',
                title: 'Trạng thái',
                render: function (data, type, row) {
                    if (data === 1) {
                        return '<span class="badge bg-success">Hoạt động</span>';
                    } else {
                        return '<span class="badge bg-danger">Vô hiệu hóa</span>';
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
                              <span data-bs-toggle="tooltip" title="Chi tiết sản phẩm">
                                <a href="/admin/products/product-detail/${row.id}" class="action-icon action-update" data-id="${row.id}"> <i class="text-info mdi mdi mdi-magnify"></i></a>
                              </span>
                              <span data-bs-toggle="tooltip" title="Cập nhật">
                                <a href="/admin/products/update-product/${row.id}" class="action-icon action-update" data-id="${row.id}"> <i class="text-warning mdi mdi-square-edit-outline"></i></a>
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
            const productId = $(this).data("id");
            await $ajax.patch("/admin/products/"+productId, {status: STATUS_OFF});
            $alter("success", "Thông báo", "Cập nhật thành công");
            reloadTable();
        }
    })

    $(document).on("click", ".action-open", async function (e) {
        e.preventDefault();
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn kích hoạt sản phẩm không?");
        if (isConfirmed.isConfirmed) {
            const productId = $(this).data("id");
            await $ajax.patch("/admin/products/" + productId, {status: STATUS_ON});
            $alter("success", "Thông báo", "Cập nhật thành công");
            reloadTable();
        }
    })

    $(document).on("click", ".action-update", function (e) {
        console.log('vao update')
    })

    $(document).ready(async function () {
        await loadOptionFilter();
    })
});





