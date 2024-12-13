import {$ajax, syncFormWithDataObject, ref, validateForm, getCommon, buttonSpinner} from "/common/public.js";

const {getValidate, clearValidation} = validateForm;
const  {convert2Vnd, formatNumberByDot} = getCommon()

$(document).ready(async function () {
    "use strict";

    /** Biến toàn cục  **/
    const idFormCreate = 'formCreate';

    const $actionSave = $('#action-save');

    const $btnModalCategory = $('#btnAddCategory');
    const $btnModalBrand = $('#btnAddBrand');
    const $btnAddColor = $('#btnAddColor');
    const $modalAddAttributes = $('#modalAddAttributes');

    const typeModal = {
        type_color: 'color',
        type_category: 'category',
        type_brand: 'brand',
        type_size: 'size'
    }
    const ruleFormAttributes = {
        'createAttributes': [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên thuộc tính bắt buộc",
                type: 'text'
            }
        ]
    };

    const formCreateProductDefault = {
        name: '',
        categoryId: null,
        brandId: null,
        description: '',
        colorsId: null
    }
    const objectCreateProduct = Object.assign({}, {...formCreateProductDefault});
    /** Function scope **/
    function showAlert(text, icon) {
        return Swal.fire({
            title: "Thông báo",
            text,
            icon,
            timer: 2000,
            showConfirmButton: false
        })
    }
    // load select của thuộc tính
    const addOptionFilter = async (url, selectElement, value = []) => {
        try {
            const res = await $ajax.get(url);
            selectElement.empty();
            res.forEach((item, index) => {
                selectElement.append(`<option value="${item.id}" ${value.includes(item.id) ? 'selected' : ''}>${item.name}</option>`);
            });
        } catch (e) {
            console.log(e)
        }
    }
    // load select của thuộc tính
    const loadOptionFilter = (res) => {
        addOptionFilter('/admin/category/active', $('#createCategory'), [res.category.id]);
        addOptionFilter('/admin/brand/active', $('#createBrand'),[res.brand.id]);
    }
    // Hàm mở modal thêm nhanh thuộc tính
    const openModalAttributes = (label = 'Tên thuộc tính', type) => {
        $('#formAddAttributes').data('type', type)
        $('#createAttributesLabel').text(label)
        $('#createAttributes').prop("placeholder", label);
        $('#invalidFeedback').text(label + ' không thể trống')

        if (type === typeModal.type_color) {
            $('#rowColorChange').prop('hidden', false);
            $('#hexCode').text($('#changeColor').val());
        } else {
            $('#rowColorChange').prop('hidden', true);
        }
    }
    // Hàm thêm mới thuộc tính
    const fetchAttributes = async (type, data) => {
        let url = ``;
        if (type === typeModal.type_category) url = `/admin/category`;
        if (type === typeModal.type_brand) url = `/admin/brand`;
        if (type === typeModal.type_color) url = `/admin/color`;
        if (type === typeModal.type_size) url = `/admin/size`;
        await $ajax.post(url, data);
    }

    /** Event scope **/
    // Khi chọn thêm nhanh danh mục
    $btnModalCategory.on("click", function (e) {
        e.preventDefault();
        openModalAttributes("Tên danh mục", typeModal.type_category);
        $modalAddAttributes.modal("show");
    })
    // Khi chọn thêm nhanh thương hiệu
    $btnModalBrand.on("click", function (e) {
        e.preventDefault();
        openModalAttributes("Tên thương hiệu", typeModal.type_brand);
        $modalAddAttributes.modal("show");
    })
    // Khi chọn thêm nhanh color
    $btnAddColor.on("click", function (e) {
        e.preventDefault();
        openModalAttributes("Tên màu", typeModal.type_color);
        $modalAddAttributes.modal("show");
    })
    // Lấy giá value và hiện thị hex code
    $('#changeColor').on('input', function () {
        const selectedColor = $(this).val();
        $('#hexCode').text(selectedColor);
    });
    $("#action-close").on("click", function (e) {
        window.location.href = '/admin/products';
    })
    // Xử lý đóng modal
    document.getElementById("modalAddAttributes").addEventListener('hide.bs.modal', event => {
        clearValidation('formAddAttributes');
    })

    // Xử lý khi submit form tạo tạo nhanh thuộc tính
    $(document).on("submit", "#formAddAttributes", async function (e) {
        e.preventDefault();
        const isValid = await getValidate('formAddAttributes', ruleFormAttributes);
        if (!isValid) return;

        const type = $(this).data("type");
        const data = {
            name: $('#createAttributes').val().trim()
        }
        if (type === typeModal.type_category) {
            data.description = data.name;
        } else if (type === typeModal.type_color) {
            data.hex = $('#changeColor').val();
        }

        await fetchAttributes(type, data);
        $alterTop("success", "Thêm mới thanh công");

        await loadOptionFilter();
        $modalAddAttributes.modal("hide");
    })

    /** === Xử lý thêm sản phẩm chi tiết === **/
    const fileCreate = ref(null);
    const fileCreateDetail = ref(null);

    fileCreate.value = FilePond.create(document.querySelector('#createFile'));
    fileCreateDetail.value = FilePond.create(document.querySelector('#createFileDetail'));

    /** Lưu sản phẩm **/
    const ruleFormProduct = {
        'createName': [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên sản phẩm tính bắt buộc",
                type: 'text'
            },
            {
                rule: (value) => value.trim().length >= 6,
                message: "Tên sản phẩm tối thiểu 6 ký tự",
                type: 'text'
            }
        ],
        'createCategory': [
            {
                rule: (value) => value.trim() !== "",
                message: "Danh mục bắt buộc",
                type: 'text'
            }
        ],
        'createBrand': [
            {
                rule: (value) => value.trim() !== "",
                message: "Thương hiệu bắt buộc",
                type: 'text'
            }
        ]
    };
    $actionSave.on("click", async function (e) {
        e.preventDefault();

        const isValidate = await getValidate('formCreate', ruleFormProduct);
        if (!isValidate) return;
        openLoading();
        buttonSpinner.show();
        const data = {
            productId: objectCreateProduct.productId,
            name: objectCreateProduct.name.trim(),
            categoryId: objectCreateProduct.categoryId,
            brandId: objectCreateProduct.brandId,
            description: objectCreateProduct.description.trim(),
            status: '1'
        }
        if (fileCreate.value.getFile() != null) {
            data.thumbnail = fileCreate.value.getFile().file;
            if (data.thumbnail?.id) delete data.thumbnail
        }
        if (fileCreateDetail.value.getFile() != null) {
            data.images = fileCreateDetail.value.getFiles().filter(el => (!el.file.id)).map(el => el.file);
            data.oldFiles = fileCreateDetail.value.getFiles().filter(el => (el.file.id)).map(el => el.file.id)
        }

        try {
            const url = '/admin/products/update-product/'+objectCreateProduct.productId;
            await $ajax.callWithMultipartFile(url, "POST", data);

            await closeLoading();
            showAlert("Cập nhật thành công", "success").then(_rs => {
                window.location.href = '/admin/products';
            });
        } catch (e) {
            console.log(e);
        } finally {
            await closeLoading();
            buttonSpinner.hidden();
        }
    })

    const path = window.location.pathname;
    const segments = path.split('/');
    const id = segments[segments.length - 1];
    const getData = async () => {
        const res = await $ajax.get("/admin/products/get-detail", {id: id})

        objectCreateProduct.productId = res.id;

        formCreateProductDefault.name = res.name;
        formCreateProductDefault.brandId = res.brand.id;
        formCreateProductDefault.categoryId = res.category.id;
        formCreateProductDefault.description = res.description;

        if (res.image) {
            const file = {
                source: res.image.fileUrl,
                options: {
                    type: 'remote',
                    file: {
                        id: res.image.id,
                        name: res.image.nameFile,
                        size: 3000,
                        type: 'image/jpeg',
                    },
                    metadata: {
                        poster: res.image.fileUrl
                    }
                }
            }
            console.log(file)
            fileCreate.value.setOptions({
                files: [file]
            });
        }

        if (res.images.length) {
            const file = res.images.map(el => {
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
            fileCreateDetail.value.setOptions({
                files: file
            });
        }

        syncFormWithDataObject({
            selectorParent: idFormCreate,
            dataObject: objectCreateProduct,
            initialValues: formCreateProductDefault,
        });
        return res;
    }

    /** Gọi cuối khi tải xong bên trên **/
    $(document).ready(async function () {
        const res = await getData();
        await loadOptionFilter(res);
    })
});

