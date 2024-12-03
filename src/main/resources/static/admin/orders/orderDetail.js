import {$ajax, getCommon, ref} from "/common/public.js";

const {transformData, convert2Vnd} = getCommon();



$(document).ready(async function () {
    "use strict";

    /**  Lấy giá trị từ thẻ meta - header **/
    if (!document.querySelector('meta[name="order-id"]').getAttribute("content")) alert("orderId không tồn tại")
    const orderId = ref(document.querySelector('meta[name="order-id"]').getAttribute("content"));
    const orderStatus = ref(document.querySelector('meta[name="order-status"]').getAttribute("content"));

    /**  Xử lý hiện thị trạng thái hóa đơn **/
    const TT_CHO_XAC_NHAN = '5';

    const updateHtmlTimelineOrder = (listTimelineOrder) => {
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
                <span class="status_order" data-bs-toggle="tooltip" data-bs-placement="bottom" title="${formatDate}">${el.status}</span>
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
        updateHtmlTimelineOrder(res)
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

        return {show, hidden};
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

    //hàm cập nhật số lượng của giỏ hàng vào product detail
    function updateProductQuantity(productId, quantityToSubtract) {
        $.ajax({
            url: `/admin/shopping-offlinee/${productId}/quantity-plus`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({quantity: quantityToSubtract}),
            success: function (response) {
                console.log("Số lượng kho đã được cập nhật thành công.");
            },
            error: function (xhr) {
                console.error("Không thể cập nhật số lượng kho.", xhr);
                Swal.fire('Lỗi', 'Không thể cập nhật số lượng kho.', 'error');
            }
        });
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
            const updateUrl = `/admin/shopping-offline/${currentOrderId}/${productDetailId}`;
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

        products.forEach((prd, index) => {
            const productRow = document.createElement('tr');

            productRow.innerHTML = `
            <td><img src="${prd.product.thumbnail}" alt="${prd.product.name}" style="width: 50px; height: 50px;"></td>
            <td>${prd.product.name}</td>
            <td>${prd.price}</td>
            <td>
                ${prd.color ? `<span class="color-circle" style="background-color: ${prd.color.hex}; display: inline-block; width: 20px; height: 20px; border-radius: 50%;"></span>` : ''}
            </td>
            <td>${prd.size.name}</td>
            <td>${prd.quantity}</td>
            <td>
                    <button class="btn btn-secondary btn-add-cart" 
                            ${prd.quantity === 0 ? 'disabled' : ''} 
                            data-id="${prd.id}">
                            ${prd.quantity === 0 ? 'Đang cập nhật' : 'Chọn vào giỏ'}
                    </button>
            </td>
        `;
            productList.appendChild(productRow);
        });
    }

    function setupPagination(totalPages, currentPage) {
        const pagination = $('#pagination');

        // Ẩn phân trang nếu không có trang nào
        if (totalPages === 0) {
            pagination.hide();
            return;
        }

        pagination.show(); // Hiện phân trang nếu có trang
        pagination.empty();

        // Thêm nút "Trước"
        pagination.append(`
        <button class="page-button" ${currentPage === 1 ? 'disabled' : ''} data-page="${currentPage - 1}">
            Trước
        </button>
    `);

        // Thêm input cho trang hiện tại
        pagination.append(`
        <input type="text" id="pageInput" value="${currentPage}" style="width: 50px; text-align: center;" />
        <span> / ${totalPages}</span>
    `);

        // Thêm nút "Tiếp theo"
        pagination.append(`
        <button class="page-button" ${currentPage === totalPages ? 'disabled' : ''} data-page="${currentPage + 1}">
            Tiếp theo
        </button>
    `);

        // Sự kiện cho nút phân trang
        $('.page-button').on('click',async  function () {
            const page = $(this).data('page');
            if (page >= 1 && page <= totalPages) {
                await fetchProductDetails(page);
            }
        });

        // Kiểm tra và chỉ cho phép nhập số trong input
        $('#pageInput').on('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
        });

        // Xử lý sự kiện khi nhấn Enter trong input
        $('#pageInput').on('keypress',async function (e) {
            if (e.key === 'Enter') {
                let inputPage = parseInt($(this).val());

                // Kiểm tra trang nhập vào
                if (isNaN(inputPage) || inputPage < 1) {
                    inputPage = 1;
                } else if (inputPage > totalPages) {
                    inputPage = totalPages;
                }

                await fetchProductDetails(inputPage);
            }
        });
    }


    const fetchProductDetails = async (page = 1, size = 5) => {
        try {
            const search = $("#searchInput").val(); // Lấy giá trị từ trường tìm kiếm
            const brandIds = $("#brandSelect").val(); // Lấy giá trị từ trường chọn thương hiệu
            const categoryIds = $("#categorySelect").val(); // Lấy giá trị từ trường chọn danh mục
            const sizeIds = $("#sizeSelect").val(); // Lấy giá trị từ trường chọn kích cỡ
            const colorIds = $("#colorSelect").val(); // Lấy giá trị từ trường chọn màu sắc
            const priceFrom = parseFloat($("#minPrice").val()) || null;
            const priceTo = parseFloat($("#maxPrice").val()) || null;
            const orderBy = $("#sortSelect").val() || 'asc'; // Cách sắp xếp

            $.ajax({
                url: `/admin/shopping-offline/product-detail-list`,
                method: 'GET', // Phương thức HTTP
                data: {
                    page: page,
                    size: size,
                    nameProduct: search,
                    brandIds: brandIds ? brandIds : [], // Chuyển đổi thành mảng
                    categoryIds: categoryIds ? categoryIds : [], // Chuyển đổi thành mảng
                    sizeIds: sizeIds ? sizeIds : [], // Chuyển đổi thành mảng
                    colorIds: colorIds ? colorIds : [], // Chuyển đổi thành mảng
                    priceFrom: priceFrom,
                    priceTo: priceTo,
                    orderBy: orderBy
                },
                dataType: 'json'
            })
                .done(function (data) {
                    renderProductList(data.content);
                    setupPagination(data.totalPages, page);
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    console.error('Error fetching product details:', textStatus, errorThrown); // Xử lý lỗi
                });
        } catch (e) {
            console.error('Error fetching product details:', e);
        }
    }

    const loadStatus = () => {
        let orderStatus = document.getElementsByClassName("status_order");

        for (let el of orderStatus) {
            el.innerHTML = getStatusLabel(el.innerHTML);
        }
    }

        // gọi khi đã tải xong js
    $(document).ready(async () => {
        await getStateOrderDetail()

        // Duyệt qua tất cả các thẻ có id là 'productPrice' và 'totalPrice' để định dạng tiền tệ
        $('#productPrice, #totalPrice, #total').each(function () {
            const price = parseFloat($(this).text().replace(/[^0-9.-]+/g, ""));
            if (!isNaN(price)) {
                $(this).text((price));
            }
        });

        $('#paymentStatus').each(function () {
            const status = $(this).data("id");
            $(this).text($(this).text() + getStatusLabel(status))
        });

        setTimeout(() => loadStatus(), 200)
    })

    function updateOrderStatus(orderId, status) {
        // Lấy thông tin từ các phần tử HTML
        const orderCode = $('#orderCode').text()?.trim() || ''; // Lấy mã đơn hàng, nếu không có thì gán giá trị mặc định ''
        const toName = $('#toName').text()?.trim() || ''; // Lấy tên người nhận
        const toAddress = $('#toAddress').text()?.trim() || ''; // Lấy địa chỉ
        const toPhone = $('#toPhone').text()?.trim() || ''; // Lấy số điện thoại
        let totalPay = $('#total').text()?.trim() || ''; // Lấy tổng tiền thanh toán
        // const paymentMethod = $('#paymentMethodId').text()?.trim() || ''; // Lấy phương thức thanh toán
        const paymentMethod =  document.querySelector('meta[name="payment-method"]').getAttribute("content");
        const orderType =  document.querySelector('meta[name="order-type"]').getAttribute("content");
        const paymentStatus =  document.querySelector('meta[name="payment-status"]').getAttribute("content");

        const note = $('#note').val()?.trim() || ''; // Lấy ghi chú (nếu có)
        // const orderType = $('#orderType').val()?.trim() || ''; // Lấy loại đơn hàng (nếu có)

        // Xử lý tổng tiền thanh toán: loại bỏ ký tự không phải số (như VNĐ)
        totalPay = totalPay.replace(/[^0-9.-]/g, '');

        // Kiểm tra các giá trị quan trọng có rỗng không
        if (!orderCode || !toName || !toAddress || !toPhone || !totalPay) {
            Swal.fire('Lỗi', 'Vui lòng kiểm tra lại thông tin đơn hàng!', 'error');
            return; // Dừng việc gửi yêu cầu nếu thiếu thông tin quan trọng
        }

        const orderDetails = [];

        // Lặp qua các chi tiết đơn hàng để lấy thông tin sản phẩm, số lượng, giá...
        $('#products-datatable tbody tr').each(function () {
            const productId = $(this).find('[name="productId"]').val();
            const productName = $(this).find('.product-name').text()?.trim() || ''; // Sử dụng optional chaining
            const quantity = $(this).find('[name="quantity"]').val()?.trim() || ''; // Sử dụng optional chaining
            let price = $(this).find('[name="price"]').text()?.trim() || ''; // Sử dụng optional chaining
            let totalPrice = $(this).find('[name="totalPrice"]').text()?.trim() || ''; // Sử dụng optional chaining

            // Loại bỏ ký tự không phải số trong giá và tổng giá
            price = price.replace(/[^0-9.-]/g, '');
            totalPrice = totalPrice.replace(/[^0-9.-]/g, '');

            orderDetails.push({
                productId: productId,
                productName: productName,
                quantity: quantity,
                price: price,
                totalPrice: totalPrice
            });
        });

        const updateData = {
            orderId: orderId,
            status: status,
            code: orderCode,
            toName: toName,
            toAddress: toAddress,
            toPhone: toPhone,
            totalPay: totalPay,  // Đảm bảo chỉ chứa giá trị số
            paymentMethod: paymentMethod,
            note: note,
            order_type: orderType,
            paymentStatus: paymentStatus,
            orderDetails: orderDetails
        };

        // Gửi yêu cầu PUT để cập nhật trạng thái đơn hàng
        $.ajax({
            url: '/admin/shopping-offline/' + orderId, // URL cập nhật đơn hàng
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(updateData), // Gửi tất cả dữ liệu cập nhật
            success: function (response) {
                // Xử lý thành công
                Swal.fire({
                    title: 'Cập nhật trạng thái đơn hàng thành công!',
                    text: "Đơn hàng đã được cập nhật.",
                    icon: 'success',
                    timer: 2000,
                    timerProgressBar: true,
                    willClose: () => {
                        location.reload(); // Tải lại trang sau khi cập nhật
                    }
                });

                // Gọi hàm createOrderHistory để tạo lịch sử đơn hàng
                createOrderHistory(orderId, status);
            },
            error: function (xhr) {
                let errorMessage = 'Không thể cập nhật trạng thái đơn hàng.';
                if (xhr.status === 400 && xhr.responseJSON) {
                    if (Array.isArray(xhr.responseJSON)) {
                        errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                    } else if (xhr.responseJSON.message) {
                        errorMessage = xhr.responseJSON.message;
                    }
                } else if (xhr.status === 500) {
                    errorMessage = 'Lỗi máy chủ, vui lòng thử lại sau.';
                } else if (xhr.status === 0) {
                    errorMessage = 'Lỗi kết nối, vui lòng kiểm tra lại internet.';
                }
                Swal.fire('Lỗi', errorMessage, 'error');
            }
        });
    }

// Function to create order history (Second API call)
    function createOrderHistory(orderId, status) {
        const historyData = {
            orderId: orderId,
            status: status // Assuming we send the new status to the API
        };

        $.ajax({
            url: '/admin/order-history', // API endpoint to create order history
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(historyData),
            success: function (response) {
                console.log("Order history created successfully", response);
            },
            error: function (xhr) {
                let errorMessage = 'Không thể tạo lịch sử đơn hàng.';
                if (xhr.status === 400 && xhr.responseJSON) {
                    if (Array.isArray(xhr.responseJSON)) {
                        errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                    } else if (typeof xhr.responseJSON === 'object' && xhr.responseJSON.message) {
                        errorMessage = xhr.responseJSON.message;
                    } else {
                        errorMessage = 'Đã xảy ra lỗi không xác định.';
                    }
                }
                Swal.fire('Lỗi', errorMessage, 'error');
            }
        });
    }

    function cancelOrder(orderId) {
        Swal.fire({
            title: 'Xác nhận hủy đơn',
            text: "Bạn có chắc chắn muốn hủy đơn này không?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Có, hủy đơn!',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                // Call updateOrderStatus with status = 0 to cancel the order
                updateOrderStatus(orderId, 0);
            }
        });
    }


// Event listener for the cancel order button
    $(document).on('click', '.btn-cancel-order', function () {
        const orderId = $(this).data('order-id'); // Get the order ID from the data-order-id attribute
        cancelOrder(orderId);
    });

    function changeOrderStatus(orderId, newStatus) {
        const statusLabel = getStatusLabel(newStatus); // Lấy tên trạng thái từ mã trạng thái
        Swal.fire({
            title: 'Xác nhận',
            text: `Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng sang "${statusLabel}"?`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Đồng ý',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                updateOrderStatus(orderId, newStatus); // Gọi hàm cập nhật trạng thái
            }
        });
    }

// Hàm lấy tên trạng thái từ mã trạng thái
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

// Sự kiện nút thay đổi trạng thái
    $(document).on('click', '.btn-change-status', function () {
        const orderId = $(this).data('order-id'); // Lấy ID đơn hàng
        const newStatus = $(this).data('new-status'); // Lấy trạng thái mới
        changeOrderStatus(orderId, newStatus); // Gọi hàm đổi trạng thái
    });

// Sự kiện nút "Quay Lại" trạng thái
    $(document).on('click', '.btn-go-back', function () {
        const orderId = $(this).data('order-id'); // Lấy ID đơn hàng
        const previousStatus = $(this).data('previous-status'); // Lấy trạng thái trước đó
        changeOrderStatus(orderId, previousStatus); // Gọi hàm đổi trạng thái
    });

    // Gọi hàm lấy dữ liệu product detail khi Tìm kiếm
    $("#searchButton").on("click", function () {
        fetchProductDetails();
    });

// Gọi hàm lấy dữ liệu product detail khi nhấn nút Reset

    $("#resetButton").on("click", function () {
        // Đặt lại giá trị của các trường nhập
        $("#searchInput").val("");
        $("#minPrice").val("");
        $("#maxPrice").val("");
        $("#sortSelect").val("");
        $("#brandSelect").val(null); // Hoặc giá trị mặc định nếu cần
        $("#categorySelect").val(null); // Hoặc giá trị mặc định nếu cần
        $("#sizeSelect").val(null); // Hoặc giá trị mặc định nếu cần
        $("#colorSelect").val(null); // Hoặc giá trị mặc định nếu cần

        // Gọi hàm tìm kiếm để cập nhật danh sách sản phẩm
        fetchProductDetails();
    });

});
