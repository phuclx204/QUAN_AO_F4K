import {$ajax, buttonSpinner, getCommon, ref, syncFormWithDataObject, validateForm} from "/common/public.js";

const {convert2Vnd, transformData} = getCommon();
const {getValidate, clearValidation} = validateForm;

(function () {
    const storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
    if (!storedUserInfo) return alert("LOG: Người dùng chưa đăng nhập");

    // Biến toàn cục
    const $modalAddress = $("#exampleModal");
    const $bodyAddress = $("#body-address");

    const $modalForm = $("#modalForm");
    const $selectProvince = $("#datalistProvince");
    const $selectDistrict = $("#datalistDistrict");
    const $selectWard = $("#datalistWard");

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
        id: "id",
    };
    const trangThaiSp = {
        conHang: 1,
        hetHang: 0
    }

    const idFormAddress = ref(null);
    const isNewUser = ref(false);
    const subtotal = ref(null);
    const shippingFee = ref(0);

    const ruleAddress = {
        createName: [{rule: (value) => value.trim() !== "", message: "Tên bắt buộc"}],
        createPhoneNumber: [
            {rule: (value) => value.trim() !== "", message: "Số điện thoại bắt buộc"},
            {rule: (value) => /^(0[3|5|7|8|9])[0-9]{8}$/.test(value), message: "Số điện thoại không đúng định dạng"},
        ],
        datalistProvince: [{rule: (value) => value.trim() !== "", message: "Thành phố bắt buộc"}],
        datalistDistrict: [{rule: (value) => value.trim() !== "", message: "Quận/Huyện bắt buộc"}],
        datalistWard: [{rule: (value) => value.trim() !== "", message: "Phường/Xã bắt buộc"}],
        createAddressDetail: [{rule: (value) => value.trim() !== "", message: "Địa chỉ cụ thể bắt buộc"}],
    };

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
        wardName: "",
    };
    const objAddress = _.cloneDeep(objAddressDefault);

    // Function scope
    const getListCart = async () => {
        const storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
        const response = await $ajax.get("/shop/cart/list-cart", {username: storedUserInfo.username});
        subtotal.value = response.subtotal;

        return response;
    }
    const getShippingPrice = async () => {
        const objShip = JSON.parse(localStorage.getItem("@f4k/shipping_info"));
        if (objShip == null) {
            return 0;
        } else {
            const response = await $ajax.get("/shop/get-fee", {
                districtId: parseInt(objShip.districtId),
                wardCode: objShip.wardCode,
            });
            const data = JSON.parse(response).data;
            shippingFee.value = data?.total || 0;
            return data?.total || 0;
        }
    }
    const getListShippingInfo = async () => {
        const response = await $ajax.get("/shop/get-shipping-info", {username: storedUserInfo.username});
        idFormAddress.value = !response.length ? "formCreateAddress" : "formShippingInfo";
        isNewUser.value = !response.length;
        localStorage.setItem("@f4k/shipping_info", null);
        return response;
    }

    const bindingFormAddress = (initValue) => {
        syncFormWithDataObject({
            selectorParent: idFormAddress.value,
            dataObject: objAddress,
            initialValues: initValue,
        });
    }
    const updateHtmlCartItem = (cart) => {
        const $cartContainer = $("#cart-items");
        $cartContainer.empty();

        cart.items.forEach((item) => {
            if (item.status !== trangThaiSp.conHang || item.productDetailDto.status !== trangThaiSp.conHang) return

            const productDetail = item.productDetailDto;
            const product = item.productDetailDto.product;

            const discountPercent = productDetail.promotion ? `<span class="badge card-badge bg-secondary">-${productDetail.promotion.discountValue}%</span>` : '';

            const cartItemHTML = `
                <div class="d-none d-md-flex justify-content-between align-items-start py-2">
                    <div class="d-flex flex-grow-1 justify-content-start align-items-start">
                        <div class="position-relative f-w-20 border p-2 me-4  position-relative">
                            ${discountPercent}
                            <span class="checkout-item-qty">${item.quantity}</span>
                            <img src="${product.image.fileUrl}" alt="${product.image.nameFile}" class="rounded img-fluid">
                        </div>
                        <div>
                            <p class="mb-1 fs-6 fw-bolder">${product.name}</p>
                            <span class="fs-xs text-uppercase fw-bolder text-muted">${productDetail.color.name} / ${productDetail.size.name}</span>
                        </div>
                    </div>
                    <div class="flex-shrink-0 fw-bolder">
                        <span>${convert2Vnd(item.total)}</span>
                    </div>
                </div>
            `;
            $cartContainer.append(cartItemHTML);
        });
    }
    const updateHtmlPayment = (shippingPrice) => {
        let total = subtotal.value + shippingPrice;

        $("#subtotal").text(convert2Vnd(subtotal.value));
        $("#shippingPrice").text(convert2Vnd(shippingPrice));
        $("#total").text(convert2Vnd(total));
    }
    const updateHtmlModalDeliveryAddress = (listShippingInfo) => {
        $bodyAddress.empty();
        listShippingInfo.forEach((item, index) => {
            const address = transformData(addressMap, item);

            if (address.isDefault) {
                localStorage.setItem("@f4k/shipping_info", JSON.stringify(address));

                $("#detailReceiver").text(`${address.recipientName} - ${address.phoneNumber}`);
                $("#detailAddress").text(`${address.addressDetail}, ${address.wardName}, ${address.districtName}, ${address.provinceName}`);
            }

            const addressHTML = `
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="address" id="radio-address-${index}" 
                        ${address.isDefault ? "checked" : ""} data-id="${address.id}">
                    <div class="d-flex row">
                        <label class="col-9 form-check-label" for="radio-address-${index}">
                            <div>
                                <span style="font-weight: 600">${address.recipientName}</span> | 
                                <span>SĐT: ${address.phoneNumber}</span><br>
                                <span>${address.addressDetail}, ${address.wardName}, 
                                    ${address.districtName}, ${address.provinceName}</span>
                            </div>
                        </label>
                        <div class="col-3 d-flex align-items-center justify-content-end">
                            <a class="cursor-pointer btn-update-address" style="color: red" 
                                data-id="${address.id}">Cập nhật</a>
                        </div>
                    </div>
                    <div class="divider"></div>
                </div>`;
            $bodyAddress.append(addressHTML);
        });
    }

    const fetchAndSetData = async (endpoint, params, dataMapper) => {
        try {
            const rs = await $ajax.get(endpoint, params);
            const data = JSON.parse(rs).data;
            return data.map(dataMapper);
        } catch (e) {
            return [];
        }
    };
    const getListProvince = () => fetchAndSetData("/shop/get-province", null, ({
                                                                                   ProvinceID,
                                                                                   ProvinceName
                                                                               }) => ({
        id: ProvinceID,
        text: ProvinceName
    }));
    const getListDistrict = (provinceId) => fetchAndSetData("/shop/get-district", {provinceId}, ({
                                                                                                     DistrictID,
                                                                                                     DistrictName
                                                                                                 }) => ({
        id: DistrictID,
        text: DistrictName
    }));
    const getListWard = (districtId) => fetchAndSetData("/shop/get-ward", {districtId}, ({
                                                                                             WardCode,
                                                                                             WardName
                                                                                         }) => ({
        id: WardCode,
        text: WardName
    }));

    const showSelectLoading = (selectId) => {
        const $select = $(selectId);
        $select.empty().append(new Option('Đang tải...', '', true, true)).prop('disabled', true);
    }
    const hideSelectLoading = (selectId, data) => {
        const $select = $(selectId);
        $select.empty();
        $select.append(new Option());
        data.forEach(item => {
            $select.append(new Option(item.text, item.id));
        });
        $select.prop('disabled', false);
    }
    const resetSelect = (selectId, isDisabled = true) => {
        const $select = $(selectId);
        $select.empty().append(new Option());
        $select.prop("disabled", isDisabled);
    };
    const reinitializeSelect2 = ($select) => {
        if ($select.data('select2')) {
            $select.select2('destroy');
        }
        $select.select2({
            theme: "bootstrap-5",
            placeholder: $select.data('placeholder'),
        });
    };
    const initSelectAddress = async (selectId, fetchFunc, selectedValue = null) => {
        const $select = $(selectId);
        showSelectLoading(selectId);

        const data = await fetchFunc();
        hideSelectLoading(selectId, data);

        reinitializeSelect2($select);
        $select.val(selectedValue).trigger("change");
    };
    const setUpAddressSelect = async (address) => {
        if (address === null) {
            await initSelectAddress("#datalistProvince", getListProvince, null);
        } else {
            await initSelectAddress("#datalistProvince", getListProvince, address.provinceId);
            await initSelectAddress("#datalistDistrict", () => getListDistrict(address.provinceId), address.districtId);
            await initSelectAddress("#datalistWard", () => getListWard(address.districtId), address.wardCode);
        }
    }

    const fetchSaveAddress = async () => {
        const url = "/shop/add-shipping-info";
        const message = objAddress.id ? "Cập nhật thành công" : "Thêm thành công";
        await $ajax.post(url, objAddress);

        const shippingPrice = await getShippingPrice();
        await updateHtmlPayment(shippingPrice);

        $alterTop("success", message);
    }
    const clearFormAddress = () => {
        clearValidation(idFormAddress.value);
        objAddress.id = null;
        objAddress.isDefault = false;

        bindingFormAddress(objAddressDefault);

        resetSelect("#datalistProvince", false);
        resetSelect("#datalistDistrict");
        resetSelect("#datalistWard");
    };

    const openModalShippingAddress = async (id = null) => {
        clearFormAddress();

        if (id) {
            const response = await $ajax.get("/shop/get-detail-shipping-info", {shippingId: id});
            Object.assign(objAddress, response);

            bindingFormAddress(response);

            $('#createIdDefault').prop("disabled", objAddress.isDefault);
            await setUpAddressSelect(objAddress);
        } else {
            $('#createIdDefault').prop("disabled", false);
            await initSelectAddress("#datalistProvince", getListProvince);
        }

        setTimeout(() => $modalForm.modal("show"), 400);
    };

    const init = async () => {
        const cart = await getListCart();
        updateHtmlCartItem(cart)

        const listShippingInfo = await getListShippingInfo();
        updateHtmlModalDeliveryAddress(listShippingInfo);

        const shippingPrice = await getShippingPrice();
        await updateHtmlPayment(shippingPrice);

        if (!listShippingInfo.length) {
            await setUpAddressSelect(null);
        }

        syncFormWithDataObject({
            selectorParent: idFormAddress.value,
            dataObject: objAddress,
            initialValues: objAddressDefault,
        });
    }

    // Event scope
    // Change select event address shipping
    $selectProvince.on("change", async function () {
        const provinceId = $(this).val();
        const provinceName = $selectProvince.find("option:selected").text();
        objAddress.provinceId = provinceId;
        objAddress.provinceName = provinceName;

        if (!provinceId) {
            resetSelect("#datalistDistrict");
            resetSelect("#datalistWard");
        } else {
            await initSelectAddress("#datalistDistrict", () => getListDistrict(provinceId));
            resetSelect("#datalistWard");
        }
    });
    $selectDistrict.on("change", async function () {
        const districtId = $(this).val();
        const districtName = $selectDistrict.find("option:selected").text();
        objAddress.districtId = districtId;
        objAddress.districtName = districtName;

        if (!districtId) {
            resetSelect("#datalistWard");
        } else {
            await initSelectAddress("#datalistWard", () => getListWard(districtId));
        }
    });
    $selectWard.on("change", async function () {
        objAddress.wardCode = $(this).val();
        objAddress.wardName = $selectWard.find("option:selected").text();

        if (isNewUser.value) {
            const response = await $ajax.get("/shop/get-fee", {
                districtId: parseInt(objAddress.districtId),
                wardCode: objAddress.wardCode,
            });
            const data = JSON.parse(response).data;
            shippingFee.value = data?.total || 0;
            await updateHtmlPayment(data?.total);
        }
    });

    // Click chọn làm địa chỉ mặc định
    $(document).on("click", ".btn-set-default-address", async function (e) {
        const selectedDataId = $('input[type="radio"][name="address"]:checked').closest('.form-check').find('.btn-update-address').data('id');

        await $ajax.get("/shop/set-default-shipping-info", {shippingId: selectedDataId});
        await init();
        $modalAddress.modal("hide");
    })

    // Click thanh toán
    $(document).on("click", "#buttonPayment", async function (e) {
        e.preventDefault();
        if (idFormAddress.value == null) return;

        if (idFormAddress.value === "formCreateAddress") {
            const isValid = await getValidate(idFormAddress.value, ruleAddress);
            if (!isValid) return;
        }

        const confirm = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn thanh toán không?");
        if (!confirm.isConfirmed) return;

        if (isNewUser.value) {
            objAddress.isDefault = true;
            await $ajax.post("/shop/add-shipping-info", objAddress);
        }

        const paymentMethod = $('input[name="checkoutShippingMethod"]:checked').attr('id');
        if (paymentMethod === 'checkoutShippingMethodOne') {
            window.location.href = `/shop/create-order?buyType=THANH_TOAN_SAU_NHAN_HANG`;
        } else if (paymentMethod === 'checkoutShippingMethodTwo') {
            const totalAmount = subtotal.value + shippingFee.value;
            window.location.href = `/vnPay/submit-order?amount=${totalAmount}&orderInfo=thanh-toan-hoa-don`;
        }
    });

    // Click create new address shipping
    $(document).on("click", ".btn-create", async function (e) {
        $modalAddress.modal("hide");
        await openModalShippingAddress();
    })

    // Click update new address shipping
    $(document).on("click", ".btn-update-address", async function (e) {
        $modalAddress.modal("hide");
        await openModalShippingAddress($(this).data("id"))
    })

    document.getElementById("modalForm").addEventListener("hidden.bs.modal", async () => {
        clearFormAddress();
        const listShippingInfo = await getListShippingInfo();
        updateHtmlModalDeliveryAddress(listShippingInfo);

        $modalAddress.modal("show");
    });

    document.getElementById("exampleModal").addEventListener("show.bs.modal", async function () {

        const listShippingInfo = await getListShippingInfo();
        updateHtmlModalDeliveryAddress(listShippingInfo);
    });

    // Click add new address
    $(".btn-submit").on("click", async function () {
        const isValid = await getValidate(idFormAddress.value, ruleAddress);
        if (!isValid) return;

        try {
            buttonSpinner.show();
            await fetchSaveAddress();
            $modalForm.modal("hide");
        } catch (e) {
            console.error("Lỗi khi lưu địa chỉ:", e);
        } finally {
            buttonSpinner.hidden();
        }
    });

    // Khởi tạo trang
    $(document).ready(async function () {
        await init();
    });
})()