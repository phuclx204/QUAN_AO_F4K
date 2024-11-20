import {$ajax, buttonSpinner, getCommon, ref, syncFormWithDataObject, validateForm} from "/common/public.js";

const {convert2Vnd, transformData} = getCommon();
const {getValidate, clearValidation} = validateForm;

(function () {
    // hiện thị danh sách địa chỉ
    let storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
    if (!storedUserInfo) {
        alert("LOG: người dùng chưa đăng nhập")
    }

    const $modalAddress = $("#exampleModal");

    const addressMap = {
        addressDetail: "addressDetail",
        districtId: "districtId",
        districtName: "districtName",
        isDefault: "isDefault",
        phoneNumber: "phoneNumber",
        provinceId: "provinceId",
        provinceName: "provinceName",
        recipientName: "recipientName",
        wardCode: "wardCode",
        wardName: "wardName",
        id: "id"
    };
    const $bodyAddress = $("#body-address");

    const getAndSetShippingInfo = async () => {
        await $ajax.get("/shop/get-shipping-info", {username: storedUserInfo.username}).then(rs => {
            $bodyAddress.empty();
            addDomAddress(rs);
        })
    }
    const addDomAddress = (items) => {
        items.forEach((el, index) => {
            const obj = transformData(addressMap, el);
            if (obj.isDefault) {
                localStorage.setItem("@f4k/shipping_info", JSON.stringify(obj));
                $("#detailReceiver").text(obj.recipientName + " - " + obj.phoneNumber)
                $("#detailAddress").text(`${obj.addressDetail}, ${obj.wardName}, ${obj.districtName}, ${obj.provinceName}`)
            }
            const $div =
                `<div class="form-check">
                        <input class="form-check-input" type="radio" name="address" id="radio-address-${index}" ${obj.isDefault ? 'checked' : ''}>
                        <div class="d-flex row">
                            <label class="col-9 form-check-label" for="radio-address-${index}">
                                  <div>
                                      <span style="font-weight: 600">${obj.recipientName}</span>  | <span>SĐT: </span> <span>${obj.phoneNumber}</span> <br>
                                      <span>${obj.addressDetail}, ${obj.wardName}, ${obj.districtName}, ${obj.provinceName}</span>
                                 </div>
                            </label>
                            <div class="col-3 d-flex align-items-center justify-content-end">
                                <a class="cursor-pointer btn-update-address" style="color: red" data-id="${obj.id}">Cập nhật</a>
                            </div>
                        </div>
                        <div class="divider"></div>
                </div>`
            $bodyAddress.append($div);
        })
    }

    // phần thêm mới cập nhật địa chỉ
    const $modalForm = $("#modalForm")

    const idFormCreate = "formShippingInfo";

    const objAddressDefault = {
        addressDetail: "",
        districtId: 0,
        districtName: "",
        id: null,
        isDefault: false,
        phoneNumber: "",
        provinceId: 0,
        provinceName: "",
        recipientName: "",
        wardCode: "",
        wardName: ""
    };
    const objAddress = _.cloneDeep(objAddressDefault);

    syncFormWithDataObject({
        selectorParent: idFormCreate,
        dataObject: objAddress,
        initialValues: objAddressDefault,
    })

    const $selectProvince = $('#datalistProvince');
    const $selectDistrict = $('#datalistDistrict');
    const $selectWard = $('#datalistWard');

    $(document).on("click", ".btn-create", function (e) {
        $modalAddress.modal("hide");
        openModal();
    })
    $(document).on("click", ".btn-update-address", function (e) {
        $modalAddress.modal("hide");
        openModal($(this).data("id"))
    })

    const openModal = async (id = null) => {
        if (id) {
            const rs = await $ajax.get("/shop/get-detail-shipping-info", {shippingId: id});
            const obj = transformData(addressMap, rs);
            objAddress.id = obj.id;

            syncFormWithDataObject({
                selectorParent: idFormCreate,
                dataObject: objAddress,
                initialValues: obj,
            });

            $('#createIdDefault').prop("disabled", obj.isDefault);
            await initSelect("#datalistProvince", getListProvince, obj.provinceId);
            await initSelect("#datalistDistrict", () => getListDistrict(obj.provinceId), obj.districtId);
            await initSelect("#datalistWard", () => getListWard(obj.districtId), obj.wardCode);
        } else {
            clearFormAddress();
            $('#createIdDefault').prop("disabled", false);
            await initSelect("#datalistProvince", getListProvince, null);
        }
        setTimeout(() => {
            $modalForm.modal("show");
        }, 400)
    }

    const resetSelect = (selectId, isDisabled = true) => {
        const $select = $(selectId);
        $select.empty().append(new Option());
        $select.prop("disabled", isDisabled);
    };
    const clearFormAddress = () => {
        clearValidation(idFormCreate);
        syncFormWithDataObject({
            selectorParent: idFormCreate,
            dataObject: objAddress,
            initialValues: objAddressDefault,
        })

        resetSelect("#datalistProvince", false);
        resetSelect("#datalistDistrict");
        resetSelect("#datalistWard");
    }

    // Hành động sau khi đóng modal create address
    document.getElementById('modalForm').addEventListener('hidden.bs.modal', async event => {
        clearFormAddress();
        await getAndSetShippingInfo();
        $modalAddress.modal("show")
    })

    const reinitializeSelect2 = ($select) => {
        if ($select.data('select2')) {
            $select.select2('destroy');
        }
        $select.select2({
            theme: "bootstrap-5",
            placeholder: $select.data('placeholder'),
        });
    };
    const showSelectLoading = (selectId) => {
        const $select = $(selectId);
        $select.empty().append(new Option('Đang tải...', '', true, true)).prop('disabled', true);
    };
    function hideSelectLoading(selectId, data) {
        const $select = $(selectId);
        $select.empty();
        $select.append(new Option());
        data.forEach(item => {
            $select.append(new Option(item.text, item.id));
        });
        $select.prop('disabled', false);
    }

    const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));
    const initSelect = async (selectId, fetchDataFunc, valueToSet) => {
        const $select = $(selectId);

        showSelectLoading(selectId);
        const data = await fetchDataFunc();
        hideSelectLoading(selectId, data);

        // await delay(100);
        reinitializeSelect2($select);
        $select.val(valueToSet).trigger('change');
    };
    const getListProvince = async () => {
        try {
            const rs = await $ajax.get("/shop/get-province");
            const data = JSON.parse(rs).data;
            return data.map(el => ({
                id: el.ProvinceID,
                text: el.ProvinceName
            }));
        } catch (e) {
            console.error("Lỗi khi lấy danh sách tỉnh:", e);
            return [];
        }
    };
    const getListDistrict = async (provinceId) => {
        try {
            const rs = await $ajax.get("/shop/get-district", { provinceId });
            const data = JSON.parse(rs).data;
            return data
                .filter(el => `${el.DistrictID}` !== '3451') // Lọc quận không hợp lệ
                .map(el => ({
                    id: el.DistrictID,
                    text: el.DistrictName
                }));
        } catch (e) {
            console.error("Lỗi khi lấy danh sách quận:", e);
            return [];
        }
    };
    const getListWard = async (districtId) => {
        try {
            const rs = await $ajax.get("/shop/get-ward", { districtId });
            const data = JSON.parse(rs).data;
            return data.map(el => ({
                id: el.WardCode,
                text: el.WardName
            }));
        } catch (e) {
            console.error("Lỗi khi lấy danh sách phường:", e);
            return [];
        }
    };

    $selectProvince.on('change', async function () {
        console.log('$selectProvince.on(\'change\')')
        const id = $(this).val();
        const selectedText = $selectProvince.find("option:selected").text();
        objAddress.provinceId = id;
        objAddress.provinceName = selectedText;

        if (!id) {
            resetSelect("#datalistDistrict");
            resetSelect("#datalistWard");
        } else {
            await initSelect("#datalistDistrict", () => getListDistrict(id), null);
            resetSelect("#datalistWard");
        }
    });
    $selectDistrict.on('change', async function () {
        console.log('$selectDistrict.on(\'change\')')
        const id = $(this).val();
        const selectedText = $selectDistrict.find("option:selected").text();
        objAddress.districtId = id;
        objAddress.districtName = selectedText;

        if (!id) {
            resetSelect("#datalistWard");
        } else {
            await initSelect("#datalistWard", () => getListWard(id), null);
        }
    });
    $selectWard.on('change', function () {
        console.log('$selectWard.on(\'change\')')
        const id = $(this).val();
        const selectedText = $selectWard.find("option:selected").text();
        objAddress.wardCode = id;
        objAddress.wardName = selectedText;
    });

    const validationRules = {
        "createName": [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên bắt buộc",
                type: 'text'
            }
        ],
        "createPhoneNumber": [
            {
                rule: (value) => value.trim() !== "",
                message: "Số điện thoại bắt buộc",
                type: 'text'
            },
            {
                rule: (value) => /^(0[3|5|7|8|9])[0-9]{8}$/.test(value),
                message: "Số điện thoại không đúng định dạng",
                type: 'text'
            }
        ],
        "datalistProvince": [
            {
                rule: (value) => value.trim() !== "",
                message: "Thành phố bắt buộc",
                type: 'text'
            }
        ],
        "datalistDistrict": [
            {
                rule: (value) => value.trim() !== "",
                message: "Quận/Huyện bắt buộc",
                type: 'text'
            }
        ],
        "datalistWard": [
            {
                rule: (value) => value.trim() !== "",
                message: "Phường/Xã bắt buộc",
                type: 'text'
            }
        ],
        "createAddressDetail": [
            {
                rule: (value) => value.trim() !== "",
                message: "Địa chỉ cụ thể bắt buộc",
                type: 'text'
            }
        ]
    }
    $(".btn-submit").on("click", async function (e) {
        const isValid = await getValidate(idFormCreate, validationRules);
        if (!isValid) return
        try {
            buttonSpinner.show();
            $ajax.post("/shop/add-shipping-info", objAddress).then(_rs => {
                if (objAddress.id) {
                    $alterTop("success", "Cập nhật thành công");
                } else {
                    $alterTop("success", "Thêm thành công");
                }
                $modalForm.modal("hide");
            })
        } catch (e) {
            console.log(e)
        } finally {
            buttonSpinner.hidden();
        }
    })


    // Phân thông tin thanh toán
    const subtotal = ref(null);
    const shipping = ref(null);

    // getObject form model
    const cartMapper = {
        itemCount: "itemCount",
        items: "items",
        subtotal: "subtotal"
    };
    const getListCart = async () => {
        let storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
        if (!storedUserInfo) {
            alert("LOG: người dùng chưa đăng nhập")
        }

        const data = await $ajax.get("/shop/cart/list-cart", {username: storedUserInfo.username});
        const dataMapper = transformData(cartMapper, data);
        renderCartItems(dataMapper)
    }

    const renderCartItems = (data) => {
        const cartContainer = $("#cart-items");
        cartContainer.empty();

        $("#subtotal").text(convert2Vnd(data.subtotal));
        data.items.forEach(item => {
            const productDetail = item.productDetailDto;
            const product = productDetail.product;
            const cartItemHTML = `
                <div class="d-none d-md-flex justify-content-between align-items-start py-2">
                    <div class="d-flex flex-grow-1 justify-content-start align-items-start">
                        <div class="position-relative f-w-20 border p-2 me-4">
                            <span class="checkout-item-qty">${item.quantity}</span>
                            <img src="${product.image.fileUrl}" alt="${product.image.nameFile}" class="rounded img-fluid">
                        </div>
                        <div>
                            <p class="mb-1 fs-6 fw-bolder">${product.name}</p>
                            <span class="fs-xs text-uppercase fw-bolder text-muted">${product.description}</span>
                        </div>
                    </div>
                    <div class="flex-shrink-0 fw-bolder">
                        <span>${convert2Vnd(item.total + '')}</span>
                    </div>
                </div>
            `;
            cartContainer.append(cartItemHTML);
        });
    }
    const getFee = async () => {
        const objShip = JSON.parse(localStorage.getItem("@f4k/shipping_info"));
        await $ajax.get("/shop/get-fee", {districtId: parseInt(objShip.districtId), wardCode: objShip.wardCode}).then(
            res => {
                console.log('vao')
                const data = JSON.parse(res);
                shipping.value = 0;
                if (data.data) {
                    console.log(data)
                    shipping.value = data.data.total;
                    console.log(shipping.value)
                    $("#shippingCost").text(convert2Vnd(data.data.total))
                }
            }
        )
    }

    $(document).on("click", "#buttonPayment", function (e) {
        e.preventDefault();
        window.location.href = `/vnPay/submit-order?amount=${subtotal.value + shipping.value}&orderInfo=thanh-toan-hoa-don&cartId=${cartId}`;
    });

    $(document).ready(async function () {
        await getFee();
        await getAndSetShippingInfo();
        await getListCart();
        $("#total").text(convert2Vnd(subtotal.value + shipping.value))
    });
})()