import {$ajax, syncFormWithDataObject, validateForm} from "/common/public.js";
const {getValidate, clearValidation} = validateForm;

(function () {
    "use strict";

    /** Biến toàn cục  **/
    const idFormCreate = 'formAddAttributes';
    const STATUS_ON = 1;
    const STATUS_OFF = 0;
    const $modalCustomer = $('#modalAddCustomer');

    const formDefault = {
        fullName: '',
        phoneNumber: '',
        email: ''
    }
    const objCreate = Object.assign({}, {...formDefault});

    syncFormWithDataObject({
        selectorParent: idFormCreate,
        dataObject: objCreate,
        initialValues: formDefault,
    });

    const rules = {
        'createName': [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên bắt buộc",
                type: 'text'
            }
        ],
        'createPhoneNumber': [
            {rule: (value) => value.trim() !== "", message: "Số điện thoại bắt buộc"},
            {rule: (value) => /^(0[3|5|7|8|9])[0-9]{8}$/.test(value), message: "Số điện thoại không đúng định dạng"},
        ],
        'createEmail': [
            {
                rule: (value) => {
                    if (value) {
                        return /^\S+@\S+\.\S+$/.test(value)
                    } else {
                        return true
                    }
                },
                message: "Email không hợp lệ."
            }
        ]
    };

    /** Function scope **/
    const refreshFilter = () => {
        $("#filterSearch").val("");
    }

    /** Event **/
    $("#btnRefresh").on("click", function (e) {
        refreshFilter();
        reloadTable();
    })

    $(document).on("click", "#btnCreateCustomer", function (e) {
        e.preventDefault();

        $modalCustomer.modal("show")
    })

    $(document).on("keydown", "#filterSearch", async function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            reloadTable();
        }
    });

    $(document).on("submit", "#formAddAttributes", async function (e) {
        e.preventDefault();

        const isValid = await getValidate('formAddAttributes', rules);
        if (!isValid) return;

        await $ajax.post("/admin/customer/save-customer", {
            numberPhone: objCreate.phoneNumber,
            email: objCreate.email,
            fullName: objCreate.fullName
        });

        $alter("success" ,"Thêm mới thành công");
        reloadTable();
        $modalCustomer.modal("hide");
    })

    // Khởi tạo table
    const $tableProduct = $('#products-table').DataTable({
        info: false,
        serverSide: true,
        searching: false,
        bLengthChange: false,
        pageLength: 5,
        ajax: {
            url: "/admin/customer/list",
            type: 'GET',
            data: function (data) {
                return {
                    page: Math.floor(data.start / data.length) + 1,
                    search: $("#filterSearch").val().trim()
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
            {data: 'fullName', title: 'Tên'},
            {data: 'email', title: 'Email'},
            {data: 'numberPhone', title: 'Số đện thoại'},
            {data: 'address', title: 'Địa chỉ'},
            {
                data: null,
                title: 'Hành động',
                render: function (data, type, row) {
                    return `<td class="table-action">
                              <span data-bs-toggle="tooltip" title="Chi tiết sản phẩm">
                                <a href="/admin/products/product-detail/${row.id}" class="action-icon action-update" data-id="${row.id}"> <i class="text-info mdi mdi mdi-magnify"></i></a>
                              </span>
                              <span data-bs-toggle="tooltip" title="Cập nhật">
                                <a href="/admin/products/update-product/${row.id}" class="action-icon action-update" data-id="${row.id}"> <i class="text-warning mdi mdi-square-edit-outline"></i></a>
                              </span>
                             </td>`;
                }
            }
        ]
    });

    const reloadTable = () => {
        $tableProduct.ajax.reload(null, false);
    }

    $(document).on("click", ".action-update", function (e) {
        console.log('vao update')
    })

    $(document).ready(async function () {

    })
})()





