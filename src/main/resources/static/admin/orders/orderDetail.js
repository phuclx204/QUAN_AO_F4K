import {$ajax, getCommon, ref} from "/common/public.js";

const {transformData, convert2Vnd} = getCommon();

$(document).ready(async function () {
    "use strict";
    openLoading();

    /**  Lấy giá trị từ thẻ meta - header **/
    if (!document.querySelector('meta[name="order-id"]').getAttribute("content")) alert("orderId không tồn tại")
    const orderId = ref(document.querySelector('meta[name="order-id"]').getAttribute("content"));
    const orderStatus = ref(document.querySelector('meta[name="order-status"]').getAttribute("content"));

    /**  Xử lý hiện thị trạng thái hóa đơn **/
    const orderDetailHistoryMapper = {
        id: 'id',
        status: 'status',
        note: 'note',
        orderType: 'orderType',
        changeDate: 'changeDate',
        orderId: 'orderId'
    }

    const TT_CHO_XAC_NHAN = '5';
    const trangThaiThanhToan = {
        CHUA_THANH_TOAN: {status: 1, mess: "Chưa thanh toán"},
        DA_THANH_TOAN: {status: 2, mess: "Đã thanh toán"},
        CHO_THANH_TOAN: {status: 3, mess: "Chờ thanh toán"}
    }
    const statusMapping = Object.values(trangThaiThanhToan).reduce((map, item) => {
        map[item.status] = item.mess;
        return map;
    }, {});
    const getTrangThaiThanhToan = (status) => {
        return statusMapping[status] || "Không xác định";
    };

    const trangThaiHoaDon = [
        {status: 0, mess: "Hủy đơn"},
        {status: 1, mess: "Tạo mới"},
        {status: 3, mess: "Hoàn tất"},
        {status: 4, mess: "Chờ giao hàng"},
        {status: 5, mess: "Chờ xác nhận"},
        {status: 6, mess: "Đang giao hàng"},
        {status: 7, mess: "Đã giao hàng"},
        {status: 8, mess: "Chờ lấy hàng"},
    ];
    const getStateByPaymentMethod = (phuongThuc, currentStatus = null) => {
        const stateOffline = [1];
        const stateOnline = [5, 4, 8, 6, 7];
        if (currentStatus != null) {
            if (`${currentStatus}` === '3') {
                stateOffline.push(3);
                stateOnline.push(3);
            }
            if (`${currentStatus}` === '0') {
                stateOffline.push(0);
                stateOnline.push(0);
            }
        }

        const states = phuongThuc.trim() === 'online' ? stateOnline : stateOffline;

        return states.map(el => {
            const currentItem = trangThaiHoaDon.find(item => item.status === el);
            return {
                status: currentItem.status,
                mess: currentItem.mess
            }
        })
    };
    const mapStatusWithDates = (orderHistory, statusOrders) => {
        return statusOrders.map(item => {
            const matchedStatus = orderHistory.find(el => el.status === item.status)
            return {
                status: item.status,
                mess: item ? item.mess : '',
                changeDate: matchedStatus?.changeDate ?? ''
            };
        })
    };
    const updateHtmlTimelineOrder = (listTimelineOrder, currentOrder) => {
        const $processLine = $('#process-line');
        const spaceSizeLine = 100 / (listTimelineOrder.length - 1);
        let process = 0;
        const $timeLineOrder = $('#timeLineOrder');

        // Xóa nội dung trước đó
        $timeLineOrder.empty();

        // Lặp qua từng phần tử trong listTimelineOrder
        listTimelineOrder.forEach(el => {
            // Định dạng ngày giờ nếu có
            let formatDate = '';
            if (el.changeDate) {
                formatDate = dayjs(el.changeDate).format('DD-MM-YYYY hh:mm:ss A');
            }

            // Xác định class của bước
            let classTimeLine = 'step-item';
            if (el.status === currentOrder.status) {
                $processLine.css('width', process + '%');
                classTimeLine += ' current';
            }

            // Tạo HTML mới cho mỗi bước
            const $html = `
            <div class="${classTimeLine}">
                <span data-bs-toggle="tooltip" data-bs-placement="bottom" title="${formatDate}">${el.mess}</span>
            </div>
        `;

            process += spaceSizeLine;
            $timeLineOrder.append($html);
        });

        $('[data-bs-toggle="tooltip"]').tooltip();
    };
    const updateHtmlTimelineOrder2 = (listTimelineOrder) => {
        const $processLine = $('#process-line');
        let process = 100;
        if (listTimelineOrder.length === 1) process = 0;
        const $timeLineOrder = $('#timeLineOrder');

        // Xóa nội dung trước đó
        $timeLineOrder.empty();

        // Lặp qua từng phần tử trong listTimelineOrder
        listTimelineOrder.forEach((el, index) => {
            // Định dạng ngày giờ nếu có
            let formatDate = '';
            if (el.changeDate) {
                formatDate = dayjs(el.changeDate).format('DD-MM-YYYY hh:mm:ss A');
            }

            // Xác định class của bước
            let classTimeLine = 'step-item';
            $processLine.css('width', process + '%');
            if ((index + 1) === listTimelineOrder.length) {
                classTimeLine += ' current';
            }

            // Tạo HTML mới cho mỗi bước
            const $html = `
            <div class="${classTimeLine}">
                <span data-bs-toggle="tooltip" data-bs-placement="bottom" title="${formatDate}">${el.note}</span>
            </div>
        `;
            $timeLineOrder.append($html);
        });

        $('[data-bs-toggle="tooltip"]').tooltip();
    };
    const getStateOrderDetail = async () => {
        const url = window.location.href;
        const orderCode = url.substring(url.lastIndexOf('/') + 1);

        const res = await $ajax.get("/admin/order-detail/get-state/" + orderCode);
        if (!res.length) return

        // const currentOrder = transformData(orderDetailHistoryMapper, res.at(-1));
        //
        // const states = getStateByPaymentMethod(currentOrder.orderType, currentOrder.status)
        // const listTimelineOrder = mapStatusWithDates(res, states);
        // updateHtmlTimelineOrder(listTimelineOrder, currentOrder)
        updateHtmlTimelineOrder2(res)
    }

    /** Xử lý của table giỏ hàng **/
    // Khởi tạo nút tăng giảm trong bảng giỏ hàng
    const $inputQuantityProducts = $("input[name='inputQuantityProduct']");
    $inputQuantityProducts.TouchSpin();

    const updateHtmlQuantity = ($this, value = 0) => {
        console.log($this, ' -$this')
        const quantityProduct = parseInt($this.data("value"));
        const quantity = quantityProduct - value;
        $this.text(`Còn ${quantity} sản phẩm`)
    }

    // Bắt sự kiện khi giá trị thay đổi bằng cách nhập trực tiếp
    $inputQuantityProducts.on('change', async function (e) {
        if (`${orderStatus.value}` !== TT_CHO_XAC_NHAN) {
            e.preventDefault();
            alert("Không thể cập nhật hóa đơn ở trạng thái hiện tại")
            $(this).prop('disabled', true);
            $(this).val($(this).data("revalue"));
            return
        }
        const value = $(this).val();
        const productDetailId = $(this).data('id');
        updateHtmlQuantity($(this).closest('td').find('span[name="showQuantityProduct"]'), value);
        console.log("Giá trị hiện tại:", value, "| ID:", productDetailId);
        await updateQuantityCart(productDetailId, value);
    });

    // Hiện thị load cho nút cật nhật giỏ hàng
    const buttonSpinner = (() => {
        const show = () => {
            $('.bootstrap-touchspin-up, .bootstrap-touchspin-up').each(function () {
                $(this).prop('disabled', true);
                $(this).prepend('<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>');
            });
        };

        const hidden = () => {
            $('.bootstrap-touchspin-up, .bootstrap-touchspin-up').each(function () {
                $(this).prop('disabled', false);
                $(this).find('.spinner-border').remove();
            });
        };

        return { show, hidden };
    })();

    // Khởi tạo bảng local (dùng bảng đã ren trong html - size = 10/trang)
    $("#products-datatable").DataTable({
        lengthChange: false,
        processing: true,
        ordering: false,
        searching: false,
        pageLength: 10,
        info: false,
        fixedColumns: {
            end: 1
        },
        language: {
            paginate: {
                previous: "<i class='mdi mdi-chevron-left'>",
                next: "<i class='mdi mdi-chevron-right'>"
            }
        },
        drawCallback: function () {
            $(".dataTables_paginate > .pagination").addClass("pagination-rounded"),
                $("#products-datatable_length label").addClass("form-label"),
                document
                    .querySelector(".dataTables_wrapper .row")
                    .querySelectorAll(".col-md-6")
                    .forEach(function (e) {
                        e.classList.add("col-sm-6"),
                            e.classList.remove("col-sm-12"),
                            e.classList.remove("col-md-6");
                    });
        }
    })

    // Xử lý khi xóa khỏi giỏ hàng
    $(document).on("click", ".btn-remove-cart", async function (e) {
        e.preventDefault();

        const isConfirm = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn xóa không?");
        if (isConfirm.isConfirmed) {
            const productDetailId = $(this).data("id");
            try {
                await $ajax.get("/admin/order-detail/remove-product-detail", {
                    orderId: orderId.value,
                    productDetailId: productDetailId
                })
                showAlert("Thông báo", "Đã xóa sản phẩm", "success");
            } catch (e) {
                showAlert("Lỗi", "Không thể thực hiện thao tác, hãy thử lại", "error");
            }
        }
    })

    // Hàm xử lý update số lượng
    const updateQuantityCart = async (productDetailId, quantity) => {
        buttonSpinner.show()
        const updateData = {
            orderId: orderId.value,
            productDetailId: productDetailId,
            quantity: quantity
        };

        // Kiểm tra số lượng hợp lệ
        if (isNaN(quantity) || quantity <= 0) {
            showAlert('Lỗi', 'Vui lòng nhập một số lượng hợp lệ.', 'error');
            return;
        }

        // Kiểm tra xem các ID có giá trị hợp lệ không
        if (!orderId || !productDetailId) {
            showAlert('Lỗi', 'Không tìm thấy thông tin đơn hàng hoặc chi tiết sản phẩm.', 'error');
            return;
        }

        // Gửi dữ liệu qua API PUT để cập nhật số lượng sản phẩm
        try {
            await $ajax.put(`/admin/shopping-offline/${orderId.value}/${productDetailId}`, updateData);
            showAlert('Thành công', 'Số lượng đã được cập nhật', 'success');
        } catch (e) {
            console.log(e)
            showAlert('Lỗi', 'Không thể cập nhật số lượng', 'error');
        } finally {
            buttonSpinner.hidden()
        }
    }

    /** Xử lý modal của giỏ hàng **/
    const $modalAddProduct = $('#productModal') // $modalAddProduct.modal("show") -> Hiện thị modal / $modalAddProduct.modal("hide") -> đóng modal
    const modalAddProduct = document.getElementById('productModal')

    // Xử lý khi mở modal - load dữ liệu trc r mới mở tránh bất bồng bộ
    $(document).on("click", "#openModalProduct", async function (e) {
        loadSelect();
        await fetchProductDetails();
        $modalAddProduct.modal("show");
    })

    // Xử lý khi đóng modal
    modalAddProduct.addEventListener('hide.bs.modal', async event => {
        loadSelect();
        await fetchProductDetails();
    })

    // chặn enter input tìm kiếm
    $(document).on("keydown", "#searchInput", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            return false;
        }
    });

    // event khi click nút submit tìm kiếm trong modal thêm sản phẩm
    $(document).on("click", "#searchButton", function (e) {
        e.preventDefault();
        const searchValue = $('#searchInput').val();
        console.log(searchValue)
        fetchProductDetails(1, 5, 'id,desc', searchValue);
    });

    // event click button làm mới
    $(document).on("click", "#resetModalButton", async function (e) {
        loadSelect();
        await fetchProductDetails();
        $modalAddProduct.modal("show");
    })

    // event chọn vào giỏ hàng
    $(document).on("click", '.btn-add-cart', async function (e) {
        e.preventDefault();
        const productId = $(this).data("id")
        await addProductToInvoice(productId)
    })

    const orderDetailMapper = {
        orderId: 'orderId',
        productDetailId: 'productDetailId',
        quantity: 'quantity',
        price: 'price'
    }

    const updateProductStock = async (productId, quantityToSubtract) => {
        try {
            const response = await $ajax.put(`/admin/shopping-offlinee/${productId}/quantity`, {quantity: quantityToSubtract});
            $alterTop("success", "Số lượng kho đã được cập nhật thành công!")
        } catch (e) {
            $alterTop("error", "Không thể cập nhật số lượng kho!")
        }
    }

    async function addProductToInvoice(productDetailId) {
        const currentOrderId = orderId.value; // Lấy ID hóa đơn hiện tại

        // Kiểm tra nếu sản phẩm đã có trong hóa đơn
        const productDetail = await $ajax.get("/admin/order-detail/exists-product-detail", {
            productDetailId: productDetailId,
            orderId: currentOrderId
        })
        console.log(productDetail)
        const response = transformData(orderDetailMapper, productDetail);

        const updateData = {
            orderId: currentOrderId,
            productDetailId: productDetailId
        };

        // Hàm xử lý AJAX
        const handleAjax = async (url, method, data, successMessage) => {
            try {
                await $ajax.callApi(url, method, data, null, false)
                await updateProductStock(productDetailId, 1); // Trừ 1 sản phẩm từ kho
                showAlert(successMessage, "Sản phẩm đã được thêm vào giỏ", "success", true)
            } catch (e) {
                let errorMessage = 'Không thể thực hiện thao tác.';
                if (xhr.status === 400 && xhr.responseJSON) {
                    errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                }
                showAlert("Lỗi", errorMessage, "success", true)
            }
        };

        if (Object.keys(response).length === 0) {
            console.log(1)
            // Nếu sản phẩm chưa có, thêm mới vào hóa đơn
            updateData.quantity = 1;
            await handleAjax('/admin/shopping-offline/add', 'POST', updateData, 'Thêm sản phẩm thành công!');
        } else {
            console.log(2)
            // Nếu sản phẩm đã có, cập nhật số lượng
            updateData.quantity = response.quantity + 1;
            const updateUrl = `/admin/shopping-offline/${currentOrderId}/${productDetailId }`;
            await handleAjax(updateUrl, 'PUT', updateData, 'Cập nhật thành công vào giỏ hàng!');
        }
    }

    /** Function scope **/
    function showAlert(title, text, icon, reload = true) {
        Swal.fire({
            title,
            text,
            icon,
            timer: 2000,
            showConfirmButton: false
        }).then(() => {
            if (reload) {
                location.reload();
            }
        });
    }

    const loadOptions = async (endpoint, selectElement, defaultOption, selectedId = null) => {
        try {
            const res = await $ajax.get(endpoint);
            selectElement.empty().append(`<option value="">${defaultOption}</option>`);
            res.forEach(item => {
                const selected = item.id === selectedId ? 'selected' : '';
                selectElement.append(`<option value="${item.id}" ${selected}>${item.name}</option>`);
            });
        } catch (e) {
            console.log(e)
        }
    }

    function loadSelect(categoryId = null, brandId = null) {
        const categorySelect = $('#categorySelect');
        const brandSelect = $('#brandSelect');
        const colorSelect = $('#colorSelect');
        const sizeSelect = $('#sizeSelect');
        loadOptions('/admin/category/active', categorySelect, 'Tất cả', categoryId);
        loadOptions('/admin/color/active', colorSelect, 'Tất cả', null);
        loadOptions('/admin/size/active', sizeSelect, 'Tất cả', null);
        loadOptions('/admin/brand/active', brandSelect, 'Tất cả', brandId);
    }

    function renderProductList(products) {
        const productList = document.getElementById('productList');
        productList.innerHTML = '';

        if (products.length === 0) {
            productList.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align: center; color: red;">Không có dữ liệu!</td>
                </tr>
            `;
            return;
        }

        products.forEach((productDetail, index) => {
            const {product} = productDetail;
            const fileUrl = product?.image?.fileUrl
            const productRow = document.createElement('tr');
            productRow.innerHTML = `
                <td>${index + 1}</td>
                <td>
                    <img class="rounded img-thumbnail product-image" src="${fileUrl}" alt="${product.name}" style="width: 60px;">
                </td>
                <td>${product.name}</td>
                <td>${convert2Vnd(productDetail.price)}</td>
                <td>
                    ${productDetail.color ? `<span class="color-circle" style="background-color: ${productDetail.color.hex}; display: inline-block; width: 20px; height: 20px; border-radius: 50%;"></span>` : ''}
                </td>
                <td>${productDetail.size.name}</td>
                <td>${productDetail.quantity}</td>
                <td>
                    <button class="new-btn-buy btn-add-cart" data-id="${productDetail.id}">Chọn vào giỏ</button>
                </td>
            `;

            productList.appendChild(productRow); // Thêm sản phẩm vào bảng
        });
    }

    function setupPagination(totalPages, currentPage) {
        $('#totalPagesText').text(`Trang ${currentPage} / ${totalPages}`);
        // Cập nhật nút "Trước" và "Tiếp theo" nếu có
        const $prevPage = $('#prevPage');
        const $nextPage = $('#nextPage');
        const $pageInput = $("#pageInput");
        $prevPage.prop('disabled', currentPage === 1);
        $prevPage.data("value", currentPage - 1);
        $nextPage.prop('disabled', currentPage === totalPages);
        $nextPage.data("value", currentPage + 1);

        $pageInput.data("value", totalPages)
        $pageInput.val("")
    }

    $(document).on("click", "#nextPage", async function (e) {
        await fetchProductDetails($(this).data("value"))
    })

    $(document).on("click", "#prevPage", async function (e) {
        await fetchProductDetails($(this).data("value"))
    })

    $(document).on("keydown", "#pageInput", async function (e) {
        if (e.key === "Enter") {
            const maxPage = $(this).data("value");
            let pages = $(this).val();
            console.log(!isNaN(pages))
            console.log(typeof pages)
            if (isNaN(pages)) {
                pages = 1;
            }
            if (pages > maxPage) {
                $(this).val(maxPage)
                pages = maxPage
            }
            if (pages <= 1) {
                pages = 1;
                $(this).val(1)
            }
            await fetchProductDetails(pages)
        }
    });

    const fetchProductDetails = async (page = 1, size = 5, sort = 'id,desc', search = '') => {
        try {
            const paramSearch = {
                page: page,
                size: size,
                sort: sort,
                search: search
            }
            const url = `/admin/products/product-detail/list`;
            const response = await $ajax.get(url, paramSearch);
            console.log(response, ' -- response')

            renderProductList(response.content); // Hiển thị sản phẩm
            setupPagination(response.totalPages, page); // Cập nhật phân trang
        } catch (e) {
            console.error('Error fetching product details:', e);
        }
    }

    // gọi khi đã tải xong js
    $(document).ready(async () => {
        await getStateOrderDetail()

        // Duyệt qua tất cả các thẻ có id là 'productPrice' và 'totalPrice' để định dạng tiền tệ
        $('#productPrice, #totalPrice, #total').each(function () {
            const price = parseFloat($(this).text().replace(/[^0-9.-]+/g, ""));

            if (!isNaN(price)) {
                $(this).text(convert2Vnd(price));
            }
        });

        // update tt thanh toán
        $('#paymentStatus').each(function () {
            const status = $(this).data("id");
            $(this).text($(this).text() + getTrangThaiThanhToan(status))
        });

        closeLoading();
    })
});