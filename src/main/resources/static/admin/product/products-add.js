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
    const addOptionFilter = async (url, selectElement, objKey = null) => {
            try {
                const res = await $ajax.get(url);
                selectElement.empty();
                res.forEach((item, index) => {
                    selectElement.append(`<option value="${item.id}">${item.name}</option>`);
                });
                if (objKey && res.length) {
                    objectCreateProduct[objKey] = res[0].id;
                }
            } catch (e) {
                console.log(e)
            }
    }

    // load select của thuộc tính
    const loadOptionFilter = () => {
        addOptionFilter('/admin/category/active', $('#createCategory'), 'categoryId');
        addOptionFilter('/admin/brand/active', $('#createBrand'), 'brandId');
        addOptionFilter('/admin/color/active', $('#createColor'));
        addOptionFilter('/admin/size/active', $('#createSize'));
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

    $("#action-close").on("click", function (e) {
        window.location.href = '/admin/products';
    })
    // Lấy giá value và hiện thị hex code
    $('#changeColor').on('input', function () {
        const selectedColor = $(this).val();
        $('#hexCode').text(selectedColor);
    });

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

    /** === Xử lý thêm nhiều sản phẩm chi tiết === **/

    const refProductDetail = ref({});
    const fileCreate = ref(null);
    const fileCreateDetail = ref(null);

    const $btnAddSize = $('#btnAddSize');

    const $modalPickSize = $('#modalPickSize');

    fileCreate.value = FilePond.create(document.querySelector('#createFile'));
    fileCreateDetail.value = FilePond.create(document.querySelector('#createFileDetail'));
    // Khi chọn thêm nhanh size
    $btnAddSize.on("click", function (e) {
        e.preventDefault();

        $modalPickSize.modal('hide');

        openModalAttributes("Tên kích cỡ", typeModal.type_size);
        $modalAddAttributes.modal("show");
    })
    /** Lưu sản phẩm **/

    const ruleFormProduct = {
        'createName': [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên sản phẩm tính bắt buộc",
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

        console.log(refProductDetail.value)

        const isValidate = await getValidate('formCreate', ruleFormProduct);
        if (!isValidate) return;

        openLoading();
        buttonSpinner.show();
        const data = {
            name: objectCreateProduct.name,
            categoryId: objectCreateProduct.categoryId,
            brandId: objectCreateProduct.brandId,
            description: objectCreateProduct.description,
            status: '1',
            listProductDetail: null
        }
        if (fileCreate.value.getFile() != null) {
            data.thumbnail = fileCreate.value.getFile().file;
        }
        if (fileCreateDetail.value.getFile() != null) {
            data.images = fileCreateDetail.value.getFiles().filter(el => (!el.file.id)).map(el => el.file);
        }

        try {
            const url = '/admin/products/add-product';
            console.log(data, ' -- data')
            await $ajax.callWithMultipartFile(url, "POST", data);

            await closeLoading();
            showAlert("Thêm mới thành công", "success").then(_rs => {
                window.location.href = '/admin/products';
            });
        } catch (e) {
            console.log(e);
        } finally {
            await closeLoading();
            buttonSpinner.hidden();
        }
    })

    syncFormWithDataObject({
        selectorParent: idFormCreate,
        dataObject: objectCreateProduct,
        initialValues: formCreateProductDefault,
    });
    /** Gọi cuối khi tải xong bên trên **/
    $(document).ready(async function () {
        await loadOptionFilter();
    })
});

