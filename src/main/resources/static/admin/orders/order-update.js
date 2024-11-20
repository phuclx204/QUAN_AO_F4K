import {$ajax, getCommon} from "/common/public.js";

const {transformData, convert2Vnd} = getCommon();

$(document).ready(async function () {
    "use strict";

    const orderDetailMapper = {
        id: 'id',
        status: 'status',
        note: 'note',
        orderType: 'orderType',
        changeDate: 'changeDate',
        orderId: 'orderId'
    }

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

        return stateOnline.map(el => {
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
    const getStateOrderDetail = async () => {
        const url = window.location.href;
        const orderCode = url.substring(url.lastIndexOf('/') + 1);

        const res = await $ajax.get("/admin/order-detail/get-state/" + orderCode);
        if (!res.length) return

        const currentOrder = transformData(orderDetailMapper, res.at(-1));

        const states = getStateByPaymentMethod(currentOrder.orderType, currentOrder.status)
        const listTimelineOrder = mapStatusWithDates(res, states);
        updateHtmlTimelineOrder(listTimelineOrder, currentOrder)
    }

    // Phần logic xử lý modal
    // Duyệt qua tất cả các thẻ có id là 'productPrice' và 'totalPrice' để định dạng tiền tệ
    $('#productPrice, #totalPrice, #total').each(function () {
        const price = parseFloat($(this).text().replace(/[^0-9.-]+/g, "")); // Lấy giá trị số và loại bỏ ký tự không phải số

        if (!isNaN(price)) {
            $(this).text(convert2Vnd(price));
        }
    });


    const $modalAddProduct = $('#productModal') // $modalAddProduct.modal("show") -> Hiện thị modal / $modalAddProduct.modal("hide") -> đóng modal
    const modalAddProduct = document.getElementById('productModal')
    const btnAddProduct = document.querySelector(".new-btn-add");
    const spanClose = document.querySelector(".new-close");

    // function scope

    function showAlert(title, text, icon) {
        Swal.fire({
            title,
            text,
            icon,
            timer: 2000,
            showConfirmButton: false
        }).then(() => {
            location.reload(); // Tải lại trang sau khi thông báo đã ẩn
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

        products.forEach((product, index) => {
            const productRow = document.createElement('tr');

            productRow.innerHTML = `
                <td>${index + 1}</td>
                <td>
                    <img src="${product.imageUrl}" alt="${product.product.name}" style="width: 50px; height: 50px;">
                </td>
                <td>${product.product.name}</td>
                <td>${convert2Vnd(product.price)}</td>
                <td>
                    ${product.color ? `<span class="color-circle" style="background-color: ${product.color.hex}; display: inline-block; width: 20px; height: 20px; border-radius: 50%;"></span>` : ''}
                </td>
                <td>${product.quantity}</td>
                <td>
                    <button class="new-btn-buy" onclick="addProductToInvoice(${product.id},${product.price})">Chọn vào giỏ</button>
                </td>
            `;

            productList.appendChild(productRow); // Thêm sản phẩm vào bảng
        });
    }

    function setupPagination(totalPages, currentPage) {
        $('#totalPagesText').text(`Trang ${currentPage} / ${totalPages}`);
        // Cập nhật nút "Trước" và "Tiếp theo" nếu có
        $('#prevPage').prop('disabled', currentPage === 1);
        $('#nextPage').prop('disabled', currentPage === totalPages);
    }

    const fetchProductDetails = async (page = 1, size = 5, sort = 'id,desc', search = '') => {
        search = $('input[name="search"]').val(); // Lấy giá trị tìm kiếm từ input

        try {
            const paramSearch = {
                page: page,
                size: size,
                sort: sort,
                search: search
            }
            const url = `/admin/products/product-detail/list`;
            const response = await $ajax.get(url, paramSearch);

            renderProductList(response.content); // Hiển thị sản phẩm
            setupPagination(response.totalPages, page); // Cập nhật phân trang
        } catch (e) {
            console.error('Error fetching product details:', e);
        }
    }

    const updateProductStock = async (productId, quantityToSubtract) => {
        try {
            const response = await $ajax.put(`/admin/shopping-offlinee/${productId}/quantity`, {quantity: quantityToSubtract});
            $alterTop("success", "Số lượng kho đã được cập nhật thành công!")
        } catch (e) {
            $alterTop("error", "Không thể cập nhật số lượng kho!")
        }
        // $.ajax({
        //     url: `/admin/shopping-offlinee/${productId}/quantity`,
        //     method: 'PUT',
        //     contentType: 'application/json',
        //     data: JSON.stringify({ quantity: quantityToSubtract }),
        //     success: function(response) {
        //         console.log("Số lượng kho đã được cập nhật thành công.");
        //     },
        //     error: function(xhr) {
        //         console.error("Không thể cập nhật số lượng kho.", xhr);
        //         Swal.fire('Lỗi', 'Không thể cập nhật số lượng kho.', 'error');
        //     }
        // });
    }

    function getCurrentOrderId() {
        const orderIdInput = document.getElementById('currentOrderId');
        const orderId = orderIdInput ? orderIdInput.value : null; // Lấy giá trị từ input ẩn
        console.log('Current Order ID:', orderId); // Ghi lại để kiểm tra
        return orderId;
    }

    function addProductToInvoice(productId, productPrice) {
        const currentOrderId = getCurrentOrderId(); // Lấy ID hóa đơn hiện tại

        if (!currentOrderId) {
            $modalAddProduct.modal('hide');
            $alter("error", "Lỗi", "Vui lòng chọn hóa đơn");
            return;
        }

        // Kiểm tra nếu sản phẩm đã có trong hóa đơn
        const existingRow = $('tr').filter(function () {
            return $(this).find('input[type="hidden"][id="productDetailId"]').val() === `${productId}`;
        });

        const updateData = {
            orderId: currentOrderId,
            productDetailId: productId,
            price: productPrice
        };

        // Hàm xử lý AJAX
        const handleAjax = async (url, method, data, successMessage) => {
            try {
                await $ajax.callApi(url, method, data, null, false)
                await updateProductStock(productId, 1); // Trừ 1 sản phẩm từ kho
                Swal.fire({
                    title: successMessage,
                    text: "Sản phẩm đã được thêm vào giỏ",
                    icon: 'success',
                    timer: 2000,
                    timerProgressBar: true,
                    willClose: () => {
                        location.reload();
                    }
                });
            } catch (e) {
                let errorMessage = 'Không thể thực hiện thao tác.';
                if (xhr.status === 400 && xhr.responseJSON) {
                    errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                }
                Swal.fire('Lỗi', errorMessage, 'error');
            }
        };

        if (existingRow.length) {
            // Nếu sản phẩm đã có, cập nhật số lượng
            const quantityInput = existingRow.find('.quantity-input');
            const currentQuantity = parseInt(quantityInput.val());
            const newQuantity = currentQuantity + 1; // Tăng số lượng lên 1
            quantityInput.val(newQuantity);

            updateData.quantity = newQuantity;
            const updateUrl = `/admin/shopping-offline/${currentOrderId}/${productId}`;
            handleAjax(updateUrl, 'PUT', updateData, 'Cập nhật thành công vào giỏ hàng!');
        } else {
            // Nếu sản phẩm chưa có, thêm mới vào hóa đơn
            const orderDetailData = {...updateData, quantity: 1};
            handleAjax('/admin/shopping-offline/add', 'POST', orderDetailData, 'Thêm sản phẩm thành công!');
        }
    }

    // end function scope

    // event khi click nút submit tìm kiếm trong modal thêm sản phẩm
    $('#searchForm').on('submit', function (event) {
        event.preventDefault();
        const searchValue = $('input[name="search"]').val();
        fetchProductDetails(1, 5, 'id,desc', searchValue);
    });

    // event khi click nút làm mới hoặc thêm sản phẩm
    btnAddProduct.onclick = function () {
        console.log('vap')
        loadSelect(); // Gọi hàm load dữ liệu chọn sản phẩm
        fetchProductDetails(); // Gọi API khi mở modal
    };

    // Xử lý khi đóng modal
    modalAddProduct.addEventListener('hide.bs.modal', event => {
        // $('#createName').val("");
        // $('#createStatus1').prop("checked", true)
        // resetDynamicInput()
        // promotionId.value = null;
        // clearValidation("formCreate")
        console.log('Xử lý đóng')
    })

    $(document).ready(async () => {
        await getStateOrderDetail()

        // Lắng nghe sự kiện 'keydown' trên ô nhập số lượng
        $('.quantity-input').on('keydown', function (event) {
            // Kiểm tra nếu phím nhấn là Enter
            if (event.key === 'Enter') {
                event.preventDefault();

                // Lấy giá trị số lượng và các giá trị liên quan từ dòng chứa input
                const $row = $(this).closest('tr');
                const quantity = parseInt($(this).val());
                const orderId = $row.find('input[type="hidden"][id="orderId"]').val();
                const productDetailId = $row.find('input[type="hidden"][id="productDetailId"]').val();
                const priceText = $row.find('#productPrice').text().trim();
                const productPrice = parseFloat(priceText.replace(/,/g, '').replace('₫', '').trim());

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

                // Tạo dữ liệu cần gửi đi
                const updateData = {
                    orderId,
                    productDetailId,
                    quantity,
                    price: productPrice
                };

                // Gửi dữ liệu qua API PUT để cập nhật số lượng sản phẩm

                $.ajax({
                    url: `/admin/shopping-offline/${orderId}/${productDetailId}`,
                    method: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify(updateData),
                    success: function (response) {
                        showAlert('Thành công', 'Số lượng đã được cập nhật', 'success');
                    },
                    error: function (xhr) {
                        let errorMessage = 'Không thể cập nhật số lượng.';
                        if (xhr.status === 400 && xhr.responseJSON) {
                            errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                        }
                        showAlert('Lỗi', errorMessage, 'error');
                    }
                });
            }
        });
    })
});