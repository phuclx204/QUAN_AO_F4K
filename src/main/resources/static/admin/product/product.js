import {$ajax, buttonSpinner, getCommon, ref, validateForm} from "/common/public.js";

const {getFormValuesByName} = getCommon();
const {getValidate, clearValidation} = validateForm;

$(document).ready(async function () {
    "use strict";

    openLoading()
    setTimeout(() => closeLoading(), 200)
    const fileCreate = ref(null);
    const productIdTmp = ref(null);

    // init table
    const table = $('#products-table').DataTable({
        serverSide: true,
        ajax: {
            url: '/admin/products/list',
            type: 'GET'
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
                render: (data, type, full, meta) => {
                    return `<img src="${data}" alt="contact-img" title="contact-img" class="rounded me-3" height="48" />`
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
                    return `<td class="table-action">
                             <a href="/admin/products/product-detail/${row.id}" class="action-icon action-view" data-id="${row.id}"> <i class="mdi mdi-eye"></i></a>
                             <a href="javascript:void(0);" class="action-icon action-update" data-id="${row.id}"> <i class="mdi mdi-square-edit-outline"></i></a>
<!--                             <a href="javascript:void(0);" class="action-icon action-delete" data-id="${row.id}"> <i class="mdi mdi-delete"></i></a>-->
                             </td>`;
                }
            }
        ]
    });
    const reloadTable = () => {
        table.ajax.reload(null, false);
    }

    const setLabelModal = (text) => {
        const label = $('#standard-modalCreateLabel');
        label.empty()
        label.text(text)
    }

    const closeModal = () => {
        $('#modal-create').modal('hide');
    }
    const openModal = (mode, $this) => {
        fileCreate.value = FilePond.create(document.querySelector('#createFile'));

        if (mode === "create") {
            setLabelModal("Thêm mới sản phẩm")
            loadOptionsSelect()
        } else if (mode === "update") {
            setLabelModal("Cập nhật sản phẩm")
            const id = $this.data("id");
            productIdTmp.value = id;
            $ajax.get("/admin/products/" + id).then(data => {
                $('#createName').val(data.name)
                $('#createDescription').val(data.description)
                $('#createBrand').val(data.brand.id)
                $('#createCategory').val(data.category.id)
                loadOptionsSelect(data.category.id, data.brand.id)
                if (data.status === 1) {
                    $('#createStatus1').prop("checked", true)
                } else {
                    $('#createStatus2').prop("checked", true)
                }
                if (data.pathImg) {
                    fileCreate.value.setOptions({
                        styleItemPanelAspectRatio: 9 / 16,
                        files: [
                            {
                                source: data.pathImg,
                                options: {
                                    type: 'remote',
                                    file: {
                                        name: 'imageTmp.j',
                                        size: 3000,
                                        type: 'image/jpeg',
                                    },
                                    metadata: {
                                        poster: data.pathImg
                                    }
                                }
                            }
                        ]
                    });
                }
            })
        }

        $('#modal-create').modal('show');
    }
    // functions
    const loadOptionsSelect = (categoryId = null, brandId = null) => {
        const categorySelect = $('#createCategory');
        const brandSelect = $('#createBrand');
        loadOption('/admin/category/active', categorySelect, categoryId);
        loadOption('/admin/brand/active', brandSelect, brandId);
    }
    const loadOption = (endpoint, selectElement, selectedId = null) => {
        $.get(endpoint, data => {
            selectElement.empty().append(`<option value="">Vui lòng chọn</option>`);
            data.forEach(item => {
                const selected = item.id === selectedId ? 'selected' : '';
                selectElement.append(`<option value="${item.id}" ${selected}>${item.name}</option>`);
            });
        });
    }

    const modalSelector = document.getElementById('modal-create')
    modalSelector.addEventListener('hide.bs.modal', event => {
        // loadOptionsSelect()
        $('#createBrand').val("");
        $('#createCategory').val("");
        $('#createDescription').val("");
        $('#createName').val("");
        fileCreate.value.removeFiles();
        productIdTmp.value = null;
        clearValidation("formCreate")
        fileCreate.value.destroy()
    })

    // event button
    $(document).on("click", ".action-create", function (e) {
        e.preventDefault();
        openModal("create")
    })
    $(document).on("click", ".action-update", function (e) {
        e.preventDefault();
        openModal("update", $(this))
    })
    // $(document).on("click", ".action-delete", async function (e) {
    //     e.preventDefault();
    //     const id = $(this).data("id");
    //     try {
    //         openLoading();
    //         await $ajax.remove("/admin/products/" + id).then(_ => {
    //             $alterTop("success", "Xóa thành công")
    //             reloadTable();
    //         });
    //     } finally {
    //         closeLoading()
    //     }
    // })

    // onSave
    const validationRules = {
        'createName': [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên sản phẩm bắt buộc",
                type: 'text'
            }
        ],
        'createBrand': [
            {
                rule: (value) => value.trim() !== "",
                message: "Thương hiệu bắt buộc",
                type: 'list'
            }
        ],
        'createCategory': [
            {
                rule: (value) => value.trim() !== "",
                message: "Danh mục bắt buộc",
                type: 'list'
            }
        ]
    };
    $(document).on("submit", "#formCreate", async e => {
        e.preventDefault();
        const isValid = await getValidate('formCreate', validationRules);
        if (isValid) {
            try {
                buttonSpinner.show();
                const object = getFormValuesByName('formCreate');
                if (fileCreate.value.getFile() != null) {
                    if (fileCreate.value.getFile().file.name !== 'imageTmp.j') {
                        object.thumbnail = fileCreate.value.getFile().file;
                    }
                }
                object.status = $('#createStatus1').is(":checked") ? '1' : '0';
                const method = productIdTmp.value ? "PUT" : "POST";
                const url = productIdTmp.value ? '/admin/products/' + productIdTmp.value : '/admin/products';
                await $ajax.callWithMultipartFile(url, method, object).then(() => {
                    $alter("success", "Note", productIdTmp.value ? "Cập nhật thành công" : "Thêm mới thành công")
                    reloadTable();
                    closeModal();
                });
            } catch (e) {
                console.log(e)
            } finally {
                buttonSpinner.hidden()
            }
        } else {
            console.log('Form không hợp lệ, vui lòng kiểm tra lại.');
        }
    })
});





