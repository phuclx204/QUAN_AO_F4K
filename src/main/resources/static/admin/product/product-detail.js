import {getCommon, ref, buttonSpinner, validateForm, $ajax} from "/common/public.js";

const {getFormValuesByName, convert2Vnd} = getCommon();

$(document).ready(async function () {
    "use strict";

    openLoading()
    setTimeout(() => closeLoading(), 300)
    const URL = '/api/v1/admin/products/product-detail';
    const productIdTmp = ref(document.querySelector('meta[name="product-id"]').getAttribute("content"));
    const productNameTmp = ref(document.querySelector('meta[name="product-name"]').getAttribute("content"));
    const productDetailId = ref(null);
    const fileCreate = ref(null);

    // init table
    const table = $('#products-detail-table').DataTable({
        serverSide: true,
        ajax: {
            url: '/api/v1/admin/products/product-detail/' + productIdTmp.value + "/list",
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
            {data: 'product.name', title: 'Tên sản phẩm'},
            {
                data: 'price', title: 'Giá tiền', render: (data, type, row) => {
                    return convert2Vnd(data + "")
                }
            },
            {data: 'product.category.name', title: 'Danh mục'},
            {data: 'product.brand.name', title: 'Thương hiệu'},
            {data: 'color.name', title: 'Màu sắc'},
            {data: 'size.name', title: 'Kích cỡ'},
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
                    return `<td class="table-action">
                             <a href="javascript:void(0);" class="action-icon action-view" data-id="${row.id}"> <i class="mdi mdi-eye"></i></a>
                             <a href="javascript:void(0);" class="action-icon action-update" data-id="${row.id}"> <i class="mdi mdi-square-edit-outline"></i></a>
                             <a href="javascript:void(0);" class="action-icon action-delete" data-id="${row.id}"> <i class="mdi mdi-delete"></i></a>
                             </td>`;
                }
            }
        ]
    });
    const reloadTable = () => {
        table.ajax.reload(null, false);
    }

    // event button
    const modalSelector = document.getElementById('modal-create')
    modalSelector.addEventListener('hide.bs.modal', event => {
        // loadOptionsSelect()
        $('#createQuantity').val("");
        $('#createPrice').val("");
        $('#createColor').val("");
        $('#createSize').val("");
        fileCreate.value.removeFiles();
        productDetailId.value = null;
        validateForm.clearValidation("formCreate")
        fileCreate.value.destroy();
    })

    const setLabelModal = (text) => {
        const label = $('#standard-modalCreateLabel');
        label.empty()
        label.text(text)
    }
    const closeModal = () => {
        $('#modal-create').modal('hide');
    }
    const openModal = async (mode, $this) => {
        $('#createName').text(productNameTmp.value)
        fileCreate.value = FilePond.create(document.querySelector('#createFile'));
        fileCreate.value.setOptions({
            styleItemPanelAspectRatio: 4 / 3,
        })

        if (mode === "create") {
            setLabelModal("Thêm mới sản phẩm")
            loadOptionsSelect()
            fileCreate.value.removeFiles();
        } else if (mode === "update") {
            setLabelModal("Cập nhật sản phẩm")
            const id = $this.data("id");
            productDetailId.value = id;
            await $ajax.get('/api/v1/admin/products/product-detail', {id: id}).then(data => {
                $('#createSize').val(data.size.id)
                $('#createColor').val(data.color.id)
                $('#createPrice').val(data.price)
                $('#createQuantity').val(data.quantity)

                loadOptionsSelect(data.size.id, data.color.id)
                if (data.images.length) {
                    const file = data.images.map(el => {
                        return {
                            source: el.fileUrl,
                            options: {
                                type: 'remote',
                                file: {
                                    id: el.id,
                                    name: el.nameFile,
                                    size: 3000,
                                    type: 'image/jpeg',
                                },
                                metadata: {
                                    poster: el.fileUrl
                                }
                            }
                        }
                    })
                    fileCreate.value.setOptions({
                        files: file
                    });
                }
            })
        }
        $('#modal-create').modal('show');
    }

    // // viewDetail
    $(document).on("click", ".action-view", async function (e) {
        e.preventDefault();
        const id = $(this).data("id");

        await $ajax.get('/api/v1/admin/products/product-detail', {id: id}).then(data => {
            $('#detailSize').text(data.size.name)
            $('#detailColor').text(data.color.name)
            $('#detailName').text(data.product.name)
            $('#detailCategory').text(data.product.category.name)
            $('#detailBrand').text(data.product.brand.name)
            $('#detailQuantity').text(data.quantity)
            $('#detailPrice').text(convert2Vnd(data.price))
            $('#detailStatus').text(data.quantity > 0 ? 'Còn hàng' : 'Hết hàng')

            $('#fancyboxImg').empty()
            if (data.images.length) {
                data.images.forEach(el => {
                    $('#fancyboxImg').append(`
                        <a data-fancybox="gallery" href="${el.fileUrl}">
                             <img src="${el.fileUrl}" alt=""/>
                        </a>
                    `)
                })

                Fancybox.bind("[data-fancybox='gallery']");
            }
        })
        $('#modal-detail').modal('show');
    })
    //view create
    $(document).on("click", ".action-create", function (e) {
        e.preventDefault();
        openModal("create")
    })
    $(document).on("click", ".action-update", function (e) {
        e.preventDefault();
        openModal("update", $(this))
    })
    // onSave
    const validationForm = {
        'createSize': [
            {
                rule: (value) => value.trim() !== "",
                message: "Kích thước bắt buộc",
                type: 'list'
            }
        ],
        'createColor': [
            {
                rule: (value) => value.trim() !== "",
                message: "Màu bắt buộc",
                type: 'list'
            }
        ],
        'createPrice': [
            {
                rule: (value) => value.trim() !== "",
                message: "Số tiền là bắt buộc",
                type: 'text'
            },
            {
                rule: (value) => !isNaN(value),
                message: "Số tiền phải là kiểu số",
                type: 'text'
            },
            {
                rule: (value) => /^(\d{1,63}(\.\d{1,2})?)$/.test(value),
                message: "Số tiền tối đa là 65 ký tự, bao gồm 63 số phần nguyên và 2 số phần thập phân",
                type: 'text'
            }
        ],
        'createQuantity': [
            {
                rule: (value) => value.trim() !== "",
                message: "số lượng bắt buộc",
                type: 'text'
            },
            {
                rule: (value) => !isNaN(value),
                message: "số lượng phải là kiểu số",
                type: 'text'
            }
        ]
    };
    $(document).on("submit", "#formCreate", async e => {
        e.preventDefault();
        const isValid = await validateForm.getValidate('formCreate', validationForm);
        if (isValid) {
            try {
                buttonSpinner.show();
                const object = getFormValuesByName('formCreate');
                object.oldFiles = [];
                if (fileCreate.value.getFile() != null) {
                    object.images = fileCreate.value.getFiles().filter(el => (!el.file.id)).map(el => el.file)
                    object.oldFiles = fileCreate.value.getFiles().filter(el => (el.file.id)).map(el => el.file.id)
                }
                const method = productDetailId.value ? "PUT" : "POST";
                const url = URL + '/' + productIdTmp.value + `${productDetailId.value ? '/update/' + productDetailId.value : '/add'}`;
                await $ajax.callWithMultipartFile(url, method, object).then(() => {
                    $alter("success", "Note", productDetailId.value ? "Cập nhật thành công" : "Thêm mới thành công")
                    reloadTable();
                    closeModal();
                });
            } catch (e) {
                console.log(e)
            } finally {
                buttonSpinner.hidden();
            }
        } else {
            console.log('Form không hợp lệ, vui lòng kiểm tra lại.');
        }
    })

    // functions
    const loadOptionsSelect = (sizeId = null, colorId = null) => {
        const sizeSelect = $('#createSize');
        const colorSelect = $('#createColor');
        loadOption('/api/v1/admin/size/active', sizeSelect, sizeId);
        loadOption('/api/v1/admin/color/active', colorSelect, colorId);
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
});





