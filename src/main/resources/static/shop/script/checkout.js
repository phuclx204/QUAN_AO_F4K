import {$ajax, buttonSpinner, getCommon, ref, syncFormWithDataObject, validateForm} from "/common/public.js";

const {convert2Vnd, transformData} = getCommon();
const {getValidate, clearValidation} = validateForm;

(function () {
    /**  hiện thị danh sách địa chỉ **/
    const storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
    if (!storedUserInfo) return alert("LOG: Người dùng chưa đăng nhập");

    const $modalAddress = $("#exampleModal");
    const $bodyAddress = $("#body-address");
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

    // Lấy và cập nhật thông tin địa chỉ giao hàng
    const getAndSetShippingInfo = async () => {
        try {
            const response = await $ajax.get("/shop/get-shipping-info", { username: storedUserInfo.username });
            $bodyAddress.empty();
            updateAddressDOM(response);
        } catch (error) {
            console.error("Lỗi khi lấy thông tin địa chỉ:", error);
        }
    };

    // set localstorage cho address
    const setLocalForShipping = (address) => {
        localStorage.setItem("@f4k/shipping_info", JSON.stringify(address));
    }

    // Cập nhật giao diện danh sách địa chỉ
    const updateAddressDOM = (items) => {
        items.forEach((el, index) => {
            const address = transformData(addressMap, el);

            // Lưu địa chỉ mặc định vào localStorage
            if (address.isDefault) {
                setLocalForShipping(address);
                updateDefaultAddressUI(address);
            }

            // Tạo HTML cho mỗi địa chỉ
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
    };

    // Cập nhật UI địa chỉ mặc định
    const updateDefaultAddressUI = (address) => {
        $("#detailReceiver").text(`${address.recipientName} - ${address.phoneNumber}`);
        $("#detailAddress").text(`${address.addressDetail}, ${address.wardName}, ${address.districtName}, ${address.provinceName}`);
    };

    // Set giá trị mặc định cho địa chỉ giao hàng
    $(document).on("click", ".btn-set-default-address", async function (e) {
        const selectedDataId = $('input[type="radio"][name="address"]:checked').closest('.form-check').find('.btn-update-address').data('id');

        try {
            await $ajax.get("/shop/set-default-shipping-info", {shippingId: selectedDataId});
            await getAndSetShippingInfo();
            await getFee();
            $modalAddress.modal("hide");
        } catch (e) {
            console.log('Không thể set giá trị default cho địa chỉ: ', e)
        }
    })

    /** phần thêm mới cập nhật địa chỉ **/
    const $modalForm = $("#modalForm");
    const $selectProvince = $("#datalistProvince");
    const $selectDistrict = $("#datalistDistrict");
    const $selectWard = $("#datalistWard");

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
        wardName: "",
    };
    const objAddress = _.cloneDeep(objAddressDefault);

    const addressValidationRules = {
        createName: [{ rule: (value) => value.trim() !== "", message: "Tên bắt buộc" }],
        createPhoneNumber: [
            { rule: (value) => value.trim() !== "", message: "Số điện thoại bắt buộc" },
            { rule: (value) => /^(0[3|5|7|8|9])[0-9]{8}$/.test(value), message: "Số điện thoại không đúng định dạng" },
        ],
        datalistProvince: [{ rule: (value) => value.trim() !== "", message: "Thành phố bắt buộc" }],
        datalistDistrict: [{ rule: (value) => value.trim() !== "", message: "Quận/Huyện bắt buộc" }],
        datalistWard: [{ rule: (value) => value.trim() !== "", message: "Phường/Xã bắt buộc" }],
        createAddressDetail: [{ rule: (value) => value.trim() !== "", message: "Địa chỉ cụ thể bắt buộc" }],
    };

    syncFormWithDataObject({
        selectorParent: idFormCreate,
        dataObject: objAddress,
        initialValues: objAddressDefault,
    });

    // Xử lý mở modal tạo hoặc cập nhật địa chỉ
    const openModal = async (id = null) => {
        clearFormAddress();

        if (id) {
            const response = await fetchShippingDetail(id);
            Object.assign(objAddress, transformData(addressMap, response));

            syncFormWithDataObject({
                selectorParent: idFormCreate,
                dataObject: objAddress,
                initialValues: objAddress,
            });

            $('#createIdDefault').prop("disabled", objAddress.isDefault);
            await populateSelects(objAddress);
        } else {
            $('#createIdDefault').prop("disabled", false);
            await initSelect("#datalistProvince", getListProvince);
        }

        setTimeout(() => $modalForm.modal("show"), 400);
    };

    // Lấy thông tin chi tiết địa chỉ
    const fetchShippingDetail = async (id) => {
        try {
            return await $ajax.get("/shop/get-detail-shipping-info", { shippingId: id });
        } catch (e) {
            console.error("Lỗi khi lấy chi tiết địa chỉ:", e);
            return null;
        }
    };

    // Khởi tạo select với dữ liệu tương ứng
    const populateSelects = async (address) => {
        await initSelect("#datalistProvince", getListProvince, address.provinceId);
        await initSelect("#datalistDistrict", () => getListDistrict(address.provinceId), address.districtId);
        await initSelect("#datalistWard", () => getListWard(address.districtId), address.wardCode);
    };

    // Xóa form địa chỉ
    const clearFormAddress = () => {
        clearValidation(idFormCreate);
        syncFormWithDataObject({
            selectorParent: idFormCreate,
            dataObject: objAddress,
            initialValues: objAddressDefault,
        });
        resetSelect("#datalistProvince", false);
        resetSelect("#datalistDistrict");
        resetSelect("#datalistWard");
    };

    // Hàm khởi tạo select với dữ liệu từ API
    const initSelect = async (selectId, fetchFunc, selectedValue = null) => {
        const $select = $(selectId);
        showSelectLoading(selectId);

        const data = await fetchFunc();
        hideSelectLoading(selectId, data);

        reinitializeSelect2($select);
        $select.val(selectedValue).trigger("change");
    };

    // Sự kiện thay đổi tỉnh, quận, phường
    $selectProvince.on("change", async function () {
        const provinceId = $(this).val();
        const provinceName = $selectProvince.find("option:selected").text();
        objAddress.provinceId = provinceId;
        objAddress.provinceName = provinceName;

        if (!provinceId) {
            resetSelect("#datalistDistrict");
            resetSelect("#datalistWard");
        } else {
            await initSelect("#datalistDistrict", () => getListDistrict(provinceId));
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
            await initSelect("#datalistWard", () => getListWard(districtId));
        }
    });
    $selectWard.on("change", function () {
        objAddress.wardCode = $(this).val();
        objAddress.wardName = $selectWard.find("option:selected").text();
    });

    // Nút submit
    $(".btn-submit").on("click", async function () {
        const isValid = await getValidate(idFormCreate, addressValidationRules);
        if (!isValid) return;

        try {
            buttonSpinner.show();
            await saveAddress();
            $modalForm.modal("hide");
        } catch (e) {
            console.error("Lỗi khi lưu địa chỉ:", e);
        } finally {
            buttonSpinner.hidden();
        }
    });

    // Lưu thông tin địa chỉ
    const saveAddress = async () => {
        const url = "/shop/add-shipping-info";
        const message = objAddress.id ? "Cập nhật thành công" : "Thêm thành công";
        await $ajax.post(url, objAddress);
        await getFee();
        $alterTop("success", message);
    };

    // Xử lý đóng modal
    document.getElementById("modalForm").addEventListener("hidden.bs.modal", async () => {
        clearFormAddress();
        await getAndSetShippingInfo();
        $modalAddress.modal("show");
    });

    // Lấy danh sách từ API
    const getListProvince = () => fetchAndSetData("/shop/get-province", null,({ ProvinceID, ProvinceName }) => ({ id: ProvinceID, text: ProvinceName }));
    const getListDistrict = (provinceId) => fetchAndSetData("/shop/get-district", {provinceId}, ({ DistrictID, DistrictName }) => ({ id: DistrictID, text: DistrictName }));
    const getListWard = (districtId) => fetchAndSetData("/shop/get-ward", {districtId},({ WardCode, WardName }) => ({ id: WardCode, text: WardName }));

    const fetchAndSetData = async (endpoint, params,dataMapper) => {
        try {
            const rs = await $ajax.get(endpoint, params);
            const data = JSON.parse(rs).data;
            return data.map(dataMapper);
        } catch (e) {
            console.error(`Lỗi khi gọi API ${endpoint}:`, e);
            return [];
        }
    };
    /////
    $(document).on("click", ".btn-create", async function (e) {
        $modalAddress.modal("hide");
        await openModal();
    })
    $(document).on("click", ".btn-update-address", async function (e) {
        $modalAddress.modal("hide");
        await openModal($(this).data("id"))
    })

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

    /** Phân thông tin thanh toán **/
    const subtotal = ref(null);
    const shipping = ref(null);
    const cartMapper = {
        itemCount: "itemCount",
        items: "items",
        subtotal: "subtotal"
    };

    const getListCart = async () => {
        const storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
        if (!storedUserInfo) return alert("LOG: Người dùng chưa đăng nhập");

        try {
            const res = await $ajax.get("/shop/cart/list-cart", { username: storedUserInfo.username });
            const data = transformData(cartMapper, res);
            renderCartItems(data);
            subtotal.value = data.subtotal;
            updateTotal();
        } catch (error) {
            console.error("Lỗi khi lấy thông tin giỏ hàng:", error);
        }
    }
    const renderCartItems = (data) => {
        const cartContainer = $("#cart-items");
        cartContainer.empty();
        $("#subtotal").text(convert2Vnd(data.subtotal));

        data.items.forEach((item) => {
            const productDetail = item.productDetailDto;
            const { product } = item.productDetailDto;
            const cartItemHTML = `
                <div class="d-none d-md-flex justify-content-between align-items-start py-2">
                    <div class="d-flex flex-grow-1 justify-content-start align-items-start">
                        <div class="position-relative f-w-20 border p-2 me-4">
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
            cartContainer.append(cartItemHTML);
        });
    }
    const getFee = async () => {
        const objShip = JSON.parse(localStorage.getItem("@f4k/shipping_info"));
        if (!objShip) return;

        try {
            const res = await $ajax.get("/shop/get-fee", {
                districtId: parseInt(objShip.districtId),
                wardCode: objShip.wardCode,
            });
            const data = JSON.parse(res).data;
            shipping.value = data?.total || 0;
            $("#shippingCost").text(convert2Vnd(shipping.value));
            updateTotal();
        } catch (error) {
            console.error("Lỗi khi lấy phí vận chuyển:", error);
        }
    }

    // Cập nhật tổng tiền
    const updateTotal = () => {
        const total = subtotal.value + shipping.value;
        $("#total").text(convert2Vnd(total));
    };

    // Xử lý sự kiện nút thanh toán
    $(document).on("click", "#buttonPayment", async function (e) {
        e.preventDefault();
        await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn thanh toán không?").then(rs => {
            if (!rs.isConfirmed) return;
            try {
                const cartId = "123"; // Thay bằng logic lấy cartId thực tế
                const selectedMethod = $('input[name="checkoutShippingMethod"]:checked').attr('id');
                if (selectedMethod === 'checkoutShippingMethodOne') {
                    window.location.href = `/shop/create-order?buyType=THANH_TOAN_SAU_NHAN_HANG`;
                } else if (selectedMethod === 'checkoutShippingMethodTwo') {
                    const totalAmount = subtotal.value + shipping.value;
                    window.location.href = `/vnPay/submit-order?amount=${totalAmount}&orderInfo=thanh-toan-hoa-don&cartId=${cartId}`;
                }
            } catch (e) {
                console.log(e)
            }
        })
    });


    // Khởi tạo trang
    $(document).ready(async function () {
        await getFee();
        await getAndSetShippingInfo();
        await getListCart();
        $("#total").text(convert2Vnd(subtotal.value + shipping.value))
    });
})()