import {$ajax, buttonSpinner, getCommon, syncFormWithDataObject, validateForm} from "/common/public.js";

const {getValidate, clearValidation} = validateForm;
const  {formatNumberByDot} = getCommon()

$(document).ready(async function () {
    "use strict";

    /** Biến toàn cục  **/
    const productId = document.querySelector('meta[name="product-id"]').getAttribute("content");
    const productName = document.querySelector('meta[name="product-name"]').getAttribute("content");

    const idFormCreate = 'formCreate';

    const $actionSave = $('#action-save');

    const $btnAddColor = $('#btnAddColor');
    const $btnAddSize = $('#btnAddSize');
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
        colorId: null,
        sizeId: null,
        quantity: null,
        price: null
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
        addOptionFilter('/admin/color/active', $('#createColor'), 'colorId');
        addOptionFilter('/admin/size/active', $('#createSize'), 'sizeId');
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
    // Khi chọn thêm nhanh color
    $btnAddColor.on("click", function (e) {
        e.preventDefault();
        openModalAttributes("Tên màu", typeModal.type_color);
        $modalAddAttributes.modal("show");
    })
    // Khi chọn thêm nhanh kích cỡ
    $btnAddSize.on("click", function (e) {
        e.preventDefault();
        openModalAttributes("Tên kích cỡ", typeModal.type_size);
        $modalAddAttributes.modal("show");
    })
    // Lấy giá value và hiện thị hex code
    $('#changeColor').on('input', function () {
        const selectedColor = $(this).val();
        $('#hexCode').text(selectedColor);
    });
    // Khi nhập giá tiền
    $(document).on('input', '#createPrice', function () {
        let value = $(this).val();
        let cleanedInput = value.replace(/[^0-9]/g, '');
        $(this).val(formatNumberByDot(cleanedInput.replace(/^0+/, '').toString()));
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
    //
    $("#action-close").on("click", function (e) {
        window.location.href = '/admin/products/product-detail/' + productId;
    })

    /** Lưu sản phẩm **/
    const ruleFormProduct = {
        'createColor': [
            {
                rule: (value) => value.trim() !== "",
                message: "Màu sắc bắt buộc",
                type: 'text'
            }
        ],
        'createSize': [
            {
                rule: (value) => value.trim() !== "",
                message: "Kích cỡ bắt buộc",
                type: 'text'
            }
        ],
        'createPrice': [
            {
                rule: (value) => value.trim() !== "",
                message: "Số tiền là bắt buộc",
                type: 'text',
                feedBackDiv: true
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
    $actionSave.on("click", async function (e) {
        e.preventDefault();

        const isValidate = await getValidate('formCreate', ruleFormProduct);
        if (!isValidate) return;

        openLoading();
        buttonSpinner.show();

        const priceValue = parseFloat((objectCreateProduct.price + '').replace(/\./g, '')) || 0;
        const data = {
            colorId: objectCreateProduct.colorId,
            sizeId: objectCreateProduct.sizeId,
            price: priceValue,
            quantity: objectCreateProduct.quantity,
            status: '1'
        }

        try {
            const url = '/admin/products/product-detail/' + productId + "/add";
            await $ajax.post(url, data);

            showAlert("Thêm mới thành công", "success").then(_rs => {
                closeLoading();
                window.location.href = '/admin/products/product-detail/' + productId;
            });
        } catch (e) {
            console.log(e);
        } finally {
            closeLoading();
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

