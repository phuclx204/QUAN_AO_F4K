import {$ajax, getCommon, ref, syncFormWithDataObject, validateForm} from "/common/public.js";

const {transformData, convert2Vnd, formatNumberByDot} = getCommon();
const {getValidate, clearValidation} = validateForm;

(function () {

    const orderId = document.querySelector('meta[name="order-id"]').getAttribute("content");
    const orderStatus = document.querySelector('meta[name="order-status"]').getAttribute("content");
    const orderCode = document.querySelector('meta[name="order-code"]').getAttribute("content");

    const imageBlank = "https://firebasestorage.googleapis.com/v0/b/clothes-f4k.appspot.com/o/common%2Fdata_not_found.png?alt=media&token=36148ded-ba2c-4207-8525-2da16e7a8557";

    const statusConfig = {
        3: [
            {icon: "mdi-account-check", text: "Chờ xác nhận", type: "completed"},
            {icon: "mdi-dolly", text: "đang xử lý", type: "completed"},
            {icon: "mdi-truck-outline", text: "Chờ giao hàng", type: "completed"},
            {icon: "mdi-truck-fast-outline", text: "Đang giao hàng", type: "completed"},
            {icon: "mdi-package-variant-closed-check", text: "Đã giao hàng", type: "completed"},
        ],
        6: [
            {icon: "mdi-account-check", text: "Chờ xác nhận", type: "completed"},
            {icon: "mdi-dolly", text: "đang xử lý", type: "completed"},
            {icon: "mdi-truck-outline", text: "Chờ giao hàng", type: "completed"},
            {icon: "mdi-truck-fast-outline", text: "Đang giao hàng", type: "active"},
        ],
        5: [
            {icon: "mdi-account-check", text: "Chờ xác nhận", type: "active"},
        ],
        8: [
            {icon: "mdi-account-check", text: "Chờ xác nhận", type: "completed"},
            {icon: "mdi-dolly", text: "đang xử lý", type: "active"},
        ],
        4: [
            {icon: "mdi-account-check", text: "Chờ xác nhận", type: "completed"},
            {icon: "mdi-dolly", text: "đang xử lý", type: "completed"},
            {icon: "mdi-truck-outline", text: "Chờ giao hàng", type: "active"},
        ],
    };

    /** function **/
    function showAlert(icon, text) {
        return Swal.fire({
            title: "Thông báo",
            text,
            icon,
            timer: 2000,
            showConfirmButton: false
        })
    }

    const updateQuantity = async (productDetailId, value, isPromotion = false, double = false) => {
        const data = {
            orderId: orderId,
            productDetailId: productDetailId,
            quantity: value
        }

        if (isPromotion) {
            let demo = await $confirm("info", "Nhắc nhở", "Sản phẩm này có giảm giá nếu cạp nhật lại sẽ có thể về giá ban đâu, bạn có muốn cập nhật không?");
            if (double) {
                demo = await $confirm("info", "Nhắc nhở", "Sản phẩm này có giảm giá nếu cạp nhật lại sẽ có thể về giá ban đâu, bạn có muốn cập nhật không?");
            }
            if (demo.isConfirmed) {
                try {
                    openLoading();
                    await $ajax.put("/admin/order-detail/update-quantity", data);
                    await closeLoading();
                    showAlert("success", "Cập nhật số lượng thành công").then(_rs => {
                        window.location.href = '/admin/order-detail/' + orderCode;
                    });
                } finally {
                    await closeLoading();
                }
            }
        } else {
            try {
                openLoading();
                await $ajax.put("/admin/order-detail/update-quantity", data);
                await closeLoading();
                showAlert("success", "Cập nhật số lượng thành công").then(_rs => {
                    window.location.href = '/admin/order-detail/' + orderCode;
                });
            } finally {
                await closeLoading();
            }
        }
    }

    const deleteOrderDetail = async (productDetailId) => {
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có muốn xoá sản phẩm không?");
        if (isConfirmed.isConfirmed) {
            const data = await $ajax.remove("/admin/shopping-offlinee/order-detail/delete", null, {orderId: orderId, productDetailId: productDetailId});
            await refreshOrder();
            Swal.fire('Đã xóa!', data, 'success').then(() => {
                location.reload();
            });
        }
    }

    const getHtmlStatus = (status) => {
        const steps = statusConfig[status] || [];
        let html = "";

        steps.forEach(step => {
            const stepClass = step.type === "completed" ? "step-completed" : step.type === "active" ? "step-active" : "step";
            const textColor = step.type === "completed" ? "text-success" : "text-primary";
            const textTickHtml = stepClass !== "step-active" ? `<span class="mdi mdi-check text-success step-indicator"></span>` : ""

            html += `<div class="step ${stepClass}">
                    ${textTickHtml}
                    <span class="mdi ${step.icon} ${textColor}" style="font-size: 50px"></span>
                    <div>${step.text}</div>
                </div>`;
        });

        const nullStepsCount = 5 - steps.length; // Giả sử tổng số bước là 5
        html += getHtmlStatusNull(nullStepsCount);

        return html;
    };

    const getHtmlStatusNull = (count) => {
        return Array.from({length: count}, () => `
        <div class="step">
            <span class="mdi mdi-format-float-none" style="font-size: 50px"></span>
        </div>
    `).join('');
    };

    const genStatus = (currentStatus) => {
        const $stepBody = $("#steps-body");
        $stepBody.empty();
        $stepBody.append(getHtmlStatus(currentStatus));
    };

    function convertDate($this) {
        const date = new Date($this.text().trim());

        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0"); // Tháng trong JS bắt đầu từ 0
        const year = date.getFullYear();

        const formattedDate = `${hours}:${minutes} ${day}-${month}-${year}`;
        $this.text(formattedDate);
    }

    function getStatusLabel(status) {
        const statusMap = {
            5: 'Xác Nhận',
            8: 'Chờ Vận Chuyển',
            6: 'Đang giao hàng',
            4: 'Vận Chuyển',
            3: 'Hoàn Thành',
            0: 'Hủy Đơn'
        };
        return statusMap[status] || 'Không xác định';
    }

    async function changeOrderStatus(newStatus) {
        const statusLabel = getStatusLabel(newStatus);
        const result = await $confirm("question", `Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng sang "${statusLabel}"?`, 'Xác nhận');

        if (result.isConfirmed) {
            openLoading();
            await $ajax.get("/admin/order-detail/update-status/" + orderId, {status: newStatus});
            await closeLoading();
            showAlert("success", "Cập nhật trạng thái thành công").then(_rs => {
                window.location.href = '/admin/order-detail/' + orderCode;
            });
        }
    }

    async function cancelOrder(orderId) {
        const result = await $confirm("warning", `Bạn có chắc chắn muốn hủy đơn này không?`, 'Xác nhận hủy đơn');
        if (result.isConfirmed) {
            openLoading();
            await $ajax.get("/admin/order-detail/update-status/" + orderId, {status: 0});
            await closeLoading();
            showAlert("success", "'Đơn hàng đã được hủy và số lượng sản phẩm đã được cập nhật!").then(_rs => {
                window.location.href = '/admin/order-detail/' + orderCode;
            });
        }
    }

    const formatInputCash = ($query) => {
        let cleanedInput = $query.val().replace(/[^0-9]/g, '').replace(/^0+/, '');

        let numValue = parseInt(cleanedInput, 10);
        if (numValue > 999999999) {
            cleanedInput = 999999999;
        }

        $query.val(formatNumberByDot(cleanedInput.toString()));
    }

    const handlePriceBlur = (type, minSelector, maxSelector) => {
        let min = parseInt($(minSelector).val().replace(/[^0-9]/g, ''), 10);
        let max = parseInt($(maxSelector).val().replace(/[^0-9]/g, ''), 10);

        if (type === 'min' && max && min >= max) {
            min = max;
        } else if (type === 'max' && min && max <= min) {
            max = min;
        }
        // objSearch[type === 'min' ? 'priceForm' : 'priceTo'] = parseInt(type === 'min' ? min : max) || null;
        $(type === 'min' ? minSelector : maxSelector).val(formatNumberByDot((type === 'min' ? min : max).toString()));
    };
    /** event **/
    $(document).on('input', '.inputQuantity', function () {
        let cleanedInput = $(this).val().replace(/[^0-9]/g, '').replace(/^0+/, '');
        $(this).val(cleanedInput.toString());
    });

    $(document).on('blur', '.inputQuantity', function () {
        if (!$(this).val()) {
            $(this).val($(this).data("value"))
        }
    });

    $(document).on("keydown", ".inputQuantity", async function (e) {
        if (e.key === "Enter") {
            if ($(this).val()) {
                const productDetailId = $(this).data("id");
                await updateQuantity(productDetailId, $(this).val(), $(this).data("promotion"), true);
            } else {
                e.preventDefault();
            }
        }
    })

    $(document).on("click", ".btn-cartIndex-sub", async function (e) {
        e.preventDefault();
        const productDetailId = $(this).data("id");
        const value = $(this).data("value");

        if (value === 0) {
            await deleteOrderDetail(productDetailId);
        } else {
            await updateQuantity(productDetailId, value, $(this).data("promotion"));
        }
    })

    $(document).on("click", ".btn-cartIndex-plus", async function (e) {
        e.preventDefault();
        const max = $(this).data("max");

        const productDetailId = $(this).data("id");
        const value = $(this).data("value");

        if (value > max) {
            $alterTop("warning", "Số lượng sản phẩm không đủ")
        } else {
            await updateQuantity(productDetailId, value, $(this).data("promotion"));
        }
    })

    $(document).on("click", ".btnDelete", async function (e) {
        e.preventDefault();
        const productDetailId = $(this).data("id");
        await deleteOrderDetail(productDetailId)
    })

    $(document).on('click', '.btn-change-status', async function () {
        const newStatus = $(this).data('new-status');
        await changeOrderStatus(newStatus);
    });

    $(document).on('click', '.btn-go-back', async function () {
        const previousStatus = $(this).data('previous-status');
        await changeOrderStatus(previousStatus);
    });

    $(document).on('click', '.btn-cancel-order', async function () {
        await cancelOrder(orderId);
    });

    $(document).on('input', '#filterPriceMax, #filterPriceMin', function () {
        formatInputCash($(this));
    });

    $(document).on('blur', '#filterPriceMin', function () {
        handlePriceBlur('min', '#filterPriceMin', '#filterPriceMax');
    });

    $(document).on('blur', '#filterPriceMax', function () {
        handlePriceBlur('max', '#filterPriceMin', '#filterPriceMax');
    });

    $('.filter-list-price').on('click', async function (e) {
        e.preventDefault();

        const val = $(this).data("id")
        if (val === 'desc' || val === 'asc') {
            objSearch.orderBy = val;

            await getListProduct(removeNullProperties({...objSearch, ...objPagination,}))
        }
    })
    /** init table **/
    const idFormFilter = 'formFilter';
    const formFilterDefault = {
        search: '',
        priceMax: null,
        priceMin: null,
        categoryId: '',
        brandId: '',
        sizeId: '',
        colorId: '',
        orderPrice: 'asc'
    }
    const objFilter = Object.assign({}, {...formFilterDefault});

    syncFormWithDataObject({
        selectorParent: idFormFilter,
        dataObject: objFilter,
        initialValues: formFilterDefault,
    });

    const $tableProduct = $('#products-table').DataTable({
        info: false,
        serverSide: true,
        searching: false,
        bLengthChange: false,
        pageLength: 5,
        ajax: {
            url: "/admin/shopping-offline/search-product-detail",
            type: 'GET',
            data: function (data) {
                return {
                    page: Math.floor(data.start / data.length) + 1,
                    size: data.length,
                    nameProduct: objFilter.search,
                    brandId: objFilter.brandId,
                    colorId: objFilter.colorId,
                    categoryId: objFilter.categoryId,
                    sizeId: objFilter.sizeId,
                    orderBy: objFilter.orderPrice,
                    priceFrom: objFilter.priceMin != null ? parseInt(objFilter.priceMin.replaceAll('.', '')) : null,
                    priceTo: objFilter.priceMax != null ? parseInt(objFilter.priceMax.replaceAll('.', '')) : null,
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
            {
                data: 'product.name',
                title: 'Tên sản phẩm',
                width: 300,
                render: (data, type, row, meta) => {
                    const fileImg = row.product.image?.fileUrl ? row.product.image?.fileUrl : imageBlank;
                    const img = `<img src="${fileImg}" alt="contact-img" title="contact-img" class="rounded me-3" height="48" />`;
                    const productName = `<div data-bs-toggle="tooltip"
                                                     title="${data}"
                                                     style="overflow: hidden; max-width: 300px; white-space: nowrap; text-overflow: ellipsis">
                                                    ${data}
                                                </div>`
                    return `<div class="d-flex">
                                    ${img}
                                    <div>
                                        ${productName}
                                    </div>
                                </div>`
                }
            },
            {data: 'product.category.name', title: 'Danh mục'},
            {data: 'product.brand.name', title: 'Thương hiệu'},
            {
                data: 'color',
                title: 'Màu sắc',
                render: (data, type, row, meta) => {
                    return `<div class="d-flex">${data.name} - <span class="ms-1" style="width: 20px; height: 20px; background-color: ${data.hex}"></span></div>`;
                }
            },
            {data: 'size.name', title: 'Kích thước'},
            {data: 'quantity', title: 'Số lượng'},
            {
                data: 'price',
                title: 'Giá bán',
                width: '300px',
                render: function (data, type, row) {
                    const purchasePrice = row.discountValue;
                    const price = convert2Vnd(data);
                    let html = ``;
                    if (row.promotion == null) {
                        html = `<div>
                                        <span class="order-price text-danger">${price}</span>
                                    </div>`
                    } else {
                        html = `<div style="display: flex; align-items: center">
                                        <div class="d-flex flex-column" style="text-align: start">
                                            <s class="lh-1 me-2"><span
                                                    class="fw-bolder m-0 product-price-list text-muted order-price">${price}</span></s>
                                            <span class="order-price text-danger">${convert2Vnd(purchasePrice)}</span>
                                        </div>
                                    </div>`
                    }
                    return `<div class="d-flex flex-column justify-content-center me-3">
                                    ${html}
                                </div>`
                }
            },
            {
                data: null,
                title: 'Hành động',
                render: function (data, type, row) {
                    const buttonHtml = row.quantity === 0 ?
                        `<span class="text-danger">Đang hết hàng</span>` :
                        `<a data-bs-toggle="tooltip" title="Mua hàng" href="#" class="btn btn-outline-success action-update" data-id="${row.id}" data-price="${row.price}"> <i class="mdi mdi-cart-minus" style="font-size: 18px"></i></a>`
                    return `<td class="table-action">${buttonHtml}</td>`;
                }
            }
        ]
    });

    const reloadTable = () => {
        $tableProduct.ajax.reload(null, false);
    }

    const refreshOrder = async () => {
        await $ajax.get("/admin/order-detail/refresh-order/" + orderId);
    }

    const addProductToInvoice = async (productDetailId, price) => {
        const existingProduct = $(`input#productDetailId[value="${productDetailId}"]`).closest('tr');
        if (existingProduct.length > 0) {
            Swal.fire('', 'Sản phẩm đã có trong giỏ', 'warning');
        } else {
            await $ajax.post("/admin/shopping-offline/add", {
                orderId: orderId,
                productDetailId: productDetailId,
                quantity: 1,
                price: parseFloat(price)
            })
            await refreshOrder();
            Swal.fire('Thành công', 'Thêm sản phẩm thành công', 'success')
                .then(() => {
                    location.reload();
                });
        }
    }

    $(document).on("submit", "#formFilter", function (e) {
        e.preventDefault();
    })
    $(document).on("click", "#btnSearchProduct", function (e) {
        e.preventDefault();
        reloadTable();
    })
    $("#btnRefresh").on("click", function (e) {
        syncFormWithDataObject({
            selectorParent: idFormFilter,
            dataObject: objFilter,
            initialValues: formFilterDefault,
        });
        reloadTable();
    })
    $(document).on("click", ".action-update", async function (e) {
        e.preventDefault();
        await addProductToInvoice($(this).data("id"), $(this).data("price"));
    })

    // cancel order
    const $modalCancel = $("#modalCancel");
    const $btnCancel = $("#btnCancel");

    const rules = {
        inputNoteCancel: [
            {rule: (value) => value.trim() !== "", message: "Lý do bắt buộc"}
        ]
    };

    $(document).on("submit", "#formCancelOrder", function (e) {
        e.preventDefault();
    })

    $(document).on("click", "#btnCancel", async function (e) {
        const isValid = await getValidate("formCancelOrder", rules);
        if (!isValid) return;

        const value = $("#inputNoteCancel").val();
        const result = await $confirm("warning", `Bạn có chắc chắn muốn hủy đơn này không?`, 'Xác nhận hủy đơn');
        if (result.isConfirmed) {
            openLoading();
            await $ajax.get("/admin/order-detail/update-status/" + orderId, {status: 0, note: value});
            await closeLoading();
            showAlert("success", "'Đơn hàng đã được hủy và số lượng sản phẩm đã được cập nhật!").then(_rs => {
                window.location.href = '/admin/order-detail/' + orderCode;
            });
        }
    })

    //
    $(document).ready(function () {
        genStatus(orderStatus);

        $('.order-price').each(function () {
            const status = $(this).text().trim();
            $(this).text(convert2Vnd(status));
        });

        $('.format-time').each(function () {
            convertDate($(this))
        });
    })
})()