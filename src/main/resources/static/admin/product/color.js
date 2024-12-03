import {$ajax, syncFormWithDataObject, validateForm, ref} from "/common/public.js";

const {getValidate, clearValidation} = validateForm;

$(document).ready(async function () {
    "use strict";

    const $modalAddAttributes = $('#modalAddAttributes');

    /** Biến toàn cục  **/
    const idFormFilter = 'formFilter';
    const STATUS_ON = 1;
    const STATUS_OFF = 0;
    const idRow = ref(null);


    const typeModal = {
        type_color: 'color',
        type_category: 'category',
        type_brand: 'brand',
        type_size: 'size'
    }

    const urlUpdate = '/admin/color/';
    const modalType = typeModal.type_color;

    const formFilterDefault = {
        search: '',
        status: '1'
    }
    const objFilter = Object.assign({}, {...formFilterDefault});
    syncFormWithDataObject({
        selectorParent: idFormFilter,
        dataObject: objFilter,
        initialValues: formFilterDefault,
    });

    const ruleFormAttributes = {
        'createAttributes': [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên thuộc tính bắt buộc",
                type: 'text'
            }
        ]
    };

    /** Function scope **/
    const refreshFilter = () => {
        syncFormWithDataObject({
            selectorParent: idFormFilter,
            dataObject: objFilter,
            initialValues: formFilterDefault,
        });
    }

    const openModalAttributes = (title = 'Thêm mới thuộc tính', label = 'Tên thuộc tính', type, data = null) => {
        const $formAddAttributes = $('#formAddAttributes');
        const $createAttributes = $('#createAttributes');

        $('#standard-modalCreateLabel').text(title)

        $formAddAttributes.data('type', type)
        $('#createAttributesLabel').text(label)
        $createAttributes.prop("placeholder", label);
        $('#invalidFeedback').text(label + ' không thể trống')

        if (data) {
            $formAddAttributes.data("is", true);
            $createAttributes.val(data.name);

            if (type === typeModal.type_color) {
                $('#changeColor').val(data.hex);
            }
        } else {
            $formAddAttributes.data("is", false);
        }

        if (type === typeModal.type_color) {
            $('#rowColorChange').prop('hidden', false);
            $('#hexCode').text($('#changeColor').val());
        } else {
            $('#rowColorChange').prop('hidden', true);
        }
    }

    const fetchAttributes = async (type, data, isUpdate = false) => {
        let url = ``;
        if (type === typeModal.type_category) url = `/admin/category`;
        if (type === typeModal.type_brand) url = `/admin/brand`;
        if (type === typeModal.type_color) url = `/admin/color`;
        if (type === typeModal.type_size) url = `/admin/size`;

        if (isUpdate) {
            await $ajax.put(url + "/" + idRow.value, data);
        } else {
            await $ajax.post(url, data);
        }
    }

    $('#changeColor').on('input', function () {
        const selectedColor = $(this).val();
        $('#hexCode').text(selectedColor);
    });

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

    $(document).on("change", "#filterStatus", function (e) {
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
            url: "/admin/color/list",
            type: 'GET',
            data: function (data) {
                return {
                    page: Math.floor(data.start / data.length) + 1,
                    size: data.length,
                    search: objFilter.search,
                    sort: 'id,desc',
                    status: objFilter.status
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
            {data: 'name', title: 'Tên sản phẩm'},
            {
                data: 'hex', title: 'Mã màu',
                render: (data, type, full, meta) => {
                    return `<div class="d-flex">${data} - <span class="ms-2" style="width: 40px; height: 20px; background-color: ${data}"></span></div>`;
                }
            },
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
                              <span data-bs-toggle="tooltip" title="Cập nhật">
                                <a href="#" class="action-icon action-update" data-id="${row.id}"> <i class="text-warning mdi mdi-square-edit-outline"></i></a>
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

    const getRowById = (rowId) => {
        const row = $tableProduct.rows().data().toArray().find(row => row.id === rowId);
        if (row) {
            return row;
        } else {
            return null;
        }
    }

    $(document).on("click", ".action-lock", async function (e) {
        e.preventDefault();
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn vô hiệu hóa sản phẩm không?");
        if (isConfirmed.isConfirmed) {
            const rowId = $(this).data("id");
            await $ajax.patch(urlUpdate + rowId, {status: STATUS_OFF});
            $alter("success", "Thông báo", "Cập nhật thành công");
            reloadTable();
        }
    })

    $(document).on("click", ".action-open", async function (e) {
        e.preventDefault();
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn kích hoạt sản phẩm không?");
        if (isConfirmed.isConfirmed) {
            const rowId = $(this).data("id");
            await $ajax.patch(urlUpdate + rowId, {status: STATUS_ON});
            $alter("success", "Thông báo", "Cập nhật thành công");
            reloadTable();
        }
    })

    $(document).on("click", ".action-update", function (e) {
        const data = getRowById($(this).data("id"));
        idRow.value = $(this).data("id");
        openModalAttributes("Cập nhật thuộc tính", "Tên danh mục", modalType, data);
        $modalAddAttributes.modal("show");
    })

    // Modal thêm thuộc tính
    $(document).on("click", "#btnCreateProduct", function (e) {
        e.preventDefault();
        idRow.value = null;
        openModalAttributes('Thêm mới thuộc tính', "Tên danh mục", modalType);
        $modalAddAttributes.modal("show");
    })

    document.getElementById("modalAddAttributes").addEventListener('hide.bs.modal', event => {
        idRow.value = null;
        clearValidation('formAddAttributes');
    })

    // Xử lý khi submit form tạo tạo nhanh thuộc tính
    $(document).on("submit", "#formAddAttributes", async function (e) {
        e.preventDefault();
        const isValid = await getValidate('formAddAttributes', ruleFormAttributes);
        if (!isValid) return;

        openLoading();
        const isUpdate = $(this).data("is");

        const type = $(this).data("type");
        const data = {
            name: $('#createAttributes').val().trim()
        }
        if (type === typeModal.type_category) {
            data.description = data.name;
        } else if (type === typeModal.type_color) {
            data.hex = $('#changeColor').val();
        }

        await fetchAttributes(type, data, isUpdate);

        await closeLoading();
        $alterTop("success", isUpdate ? "Cập nhật thành công" : "Thêm mới thanh công");

        reloadTable();
        $modalAddAttributes.modal("hide");
    })

    $(document).ready(async function () {
        // await loadOptionFilter();
    })
});