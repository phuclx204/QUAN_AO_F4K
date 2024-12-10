// Load data và hiện modal product detail khi click
function addProductDetail() {
    loadSelect();
    fetchProductDetails();
}

function loadOptions(endpoint, selectElement, defaultOption, selectedId = null) {
    $.get(endpoint, function (data) {
        selectElement.empty().append(`<option value="">${defaultOption}</option>`);
        data.forEach(item => {
            const selected = item.id == selectedId ? 'selected' : '';
            selectElement.append(`<option value="${item.id}" ${selected}>${item.name}</option>`);
        });
    });
}

// hàm đổ dữ liệu vào select
function loadSelect(categoryId = null, brandId = null, colorId = null, sizeId = null) {
    const categorySelect = $('#categorySelect');
    const brandSelect = $('#brandSelect');
    const colorSelect = $('#colorSelect');
    const sizeSelect = $('#sizeSelect');
    loadOptions('/admin/category/active', categorySelect, 'Tất cả', categoryId);
    loadOptions('/admin/color/active', colorSelect, 'Tất cả', colorId);
    loadOptions('/admin/size/active', sizeSelect, 'Tất cả', sizeId);
    loadOptions('/admin/brand/active', brandSelect, 'Tất cả', brandId);
}

// hàm lấy số lượng hóa đơn chờ
function getOrderCount() {
    return $('.new-invoice-container button').length;
}

// xác nhận tạo hóa đơn và giới hạn 10 hóa đơn
function confirmCreateInvoice() {

    const orderCount = getOrderCount();

    if (orderCount >= 5) {
        Swal.fire({
            title: 'Giới hạn hóa đơn',
            text: "Bạn đã đạt giới hạn 5 hóa đơn. Không thể tạo thêm hóa đơn mới.",
            icon: 'warning',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Đóng'
        });
        return;
    }

    createInvoice();
}

// tạo hóa đơn với mã tự sinh, trạng thái 1,loại offline
function createInvoice() {
    const orderData = {
        userId: 999,
        code: generateUniqueCode(),
        status: 1,
        paymentStatus: 3,
        order_type: 'offline'
    };

    $.ajax({
        url: '/admin/shopping-offline',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(orderData),
        success: function (response) {
            const newInvoiceId = response.id;
            viewInvoice(newInvoiceId)
        },
        error: function (xhr) {
            let errorMessage = 'Không thể tạo hóa đơn.';
            if (xhr.status === 400 && xhr.responseJSON) {
                errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
            }
            Swal.fire('Lỗi', errorMessage, 'error');
        }
    });
}

// Hàm để tạo mã duy nhất cho hóa đơn
function generateUniqueCode() {
    return 'HD' + Date.now();
}

// cập nhật trạng thái cho đơn hủy = 0
function updateOrderStatus(orderId, status) {
    const orderCode = document.getElementById('orderCode').innerText;
    const updateData = {
        status: status,
        code: orderCode
    }

    $.ajax({
        url: `/admin/shopping-offline/` + orderId + `/order-details`,
        method: 'GET',
        success: function (res) {

            $.ajax({
                url: '/admin/shopping-offline/' + orderId,
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(updateData),
                success: function (response) {
                    Swal.fire({
                        title: 'Hủy đơn thành công!',
                        text: "Đơn hàng đã được hủy.",
                        icon: 'success',
                        confirmButtonText: 'OK',
                        timerProgressBar: true,
                    });
                    // Tăng số lượng tồn kho cho từng sản phẩm
                    res.forEach(prD => {
                        updateProductQuantity(prD.productDetail.id, prD.quantity);
                    });
                    handleRemainingInvoices(orderId);
                },
                error: function (xhr) {
                    let errorMessage = 'Không thể hủy đơn.';
                    if (xhr.status === 400 && xhr.responseJSON) {
                        errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                    }
                    Swal.fire('Lỗi', errorMessage, 'error');
                }
            });

        },
        error: function () {
            Swal.fire('Lỗi', 'Không thể lấy danh sách sản phẩm trong hóa đơn.', 'error');
        }
    });

}

//view hóa đơn được chọn
function viewInvoice(orderIds) {
    window.location.href = "/admin/shopping-offline/" + orderIds
}

// tự động cho các trường nhập tiền chỉ được nhập số
$(document).ready(function () {
    $(".quantity-input").on("input", function () {
        let value = $(this).val().replace(/[^0-9]/g, '');

        $(this).val(value);
    });
    $("#minPrice, #maxPrice").on("input", function () {
        let value = $(this).val().replace(/\D/g, "");
        let formattedValue = value.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
        $(this).val(formattedValue);
    });

});

//hàm kiểm tra xem có giảm giá hay không
function checkDiscountAndRenderPrice(productDetailId, originalPrice, callback) {
    $.ajax({
        url: `/admin/shopping-offline/is-on-sale`,
        type: 'GET',
        data: {
            productDetailId: productDetailId,
            originalPrice: originalPrice
        },
        success: function (discountedPrice) {
            callback(discountedPrice);
        },
        error: function (error) {
            console.error('Lỗi khi tính giảm giá:', error);
            callback(originalPrice);
        }
    });
}

// hàm lấy dữ liệu product detail
function fetchProductDetails(page = 1, size = 5) {
    const search = $("#searchInput").val();
    const brandIds = $("#brandSelect").val();
    const categoryIds = $("#categorySelect").val();
    const sizeIds = $("#sizeSelect").val();
    const colorIds = $("#colorSelect").val();
    const priceFrom = parseFloat($("#minPrice").val()) || null;
    const priceTo = parseFloat($("#maxPrice").val()) || null;
    const orderBy = $("#sortSelect").val() || 'asc';

    $.ajax({
        url: `/admin/shopping-offline/product-detail-list`,
        method: 'GET',
        data: {
            page: page,
            size: size,
            nameProduct: search,
            brandIds: brandIds ? brandIds : [],
            categoryIds: categoryIds ? categoryIds : [],
            sizeIds: sizeIds ? sizeIds : [],
            colorIds: colorIds ? colorIds : [],
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
            console.error('Error fetching product details:', textStatus, errorThrown);
        });
}

//hàm format price
function formatPrice(amount) {
    return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".") + ' ₫';
}

// đổ dữ liệu sản phẩm chi tiết ra bảng
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

        const listImg = prd.images;
        let image = ``;

        if (!listImg.length) {
            image = `<img src="/admin/img/people.png" alt="${prd.product.thumbnail}" style="width: 50px; height: 50px;object-fit: cover;">`
        } else {
            image = `<img src="${listImg[0].fileUrl}" alt="${prd.product.thumbnail}" style="width: 50px; height: 50px;object-fit: cover;">`
        }

        productRow.innerHTML = `
            <td>${image}</td>
            <td>${prd.product.name}</td>
            <td class="original-price">${formatPrice(prd.price)}</td>
            <td>
                ${prd.color ? `
                  <span class="color-circle" style="background-color: ${prd.color.hex}; display: inline-block; width: 20px; height: 20px; border-radius: 50%;"></span>
                  <span class="color-name" style="margin-left: 8px;">${prd.color.name}</span>
                ` : ''}
            </td>
            <td>${prd.size.name}</td>
            <td>${prd.quantity}</td>
            <td>
                 <button 
                  ${prd.quantity === 0 ? 'disabled="true"' : ''} 
                  data-bs-toggle="tooltip" 
                  data-bs-placement="top"
                  title="${prd.quantity === 0 ? 'Đang cập nhật' : 'Chọn vào giỏ'}"
                  onclick="addProductToInvoice(${prd.id})" 
                  style="cursor: ${prd.quantity === 0 ? 'not-allowed' : 'pointer'};">
                  <i class='mdi mdi-cart ${prd.quantity === 0 ? 'text-danger' : 'text-success'}' style="font-size: 19px;"></i>
                </button>
            </td>
        `;

        // Kiểm tra giảm giá và cập nhật giao diện
        checkDiscountAndRenderPrice(prd.id, prd.price, function (discountedPrice) {
            discountedPrice = Math.floor(discountedPrice);
            const originalPriceCell = productRow.querySelector('.original-price');
            if (discountedPrice !== prd.price) {
                const discountPercentage = ((prd.price - discountedPrice) / prd.price) * 100;

                originalPriceCell.innerHTML = `
                    <span style="text-decoration: line-through; color: red;">${formatPrice(prd.price)}</span>
                    <span style="color: black;">${formatPrice(discountedPrice)}</span>
                    <span class="hidden">${discountedPrice}</span>
                `;

                // Hiển thị phần trăm giảm
                const discountLabel = document.createElement('span');
                discountLabel.classList.add('discount-label');
                discountLabel.textContent = `- ${discountPercentage.toFixed(0)}%`;
                discountLabel.style.color = '#ff5733';
                discountLabel.style.fontWeight = 'bold';
                discountLabel.style.fontSize = '14px';
                discountLabel.style.backgroundColor = 'rgba(255, 87, 51, 0.1)';
                discountLabel.style.padding = '2px 8px';
                discountLabel.style.borderRadius = '4px';
                discountLabel.style.marginLeft = '10px';
                discountLabel.style.display = 'inline-block';

                // Thêm nhãn giảm giá vào giá sau giảm
                originalPriceCell.appendChild(discountLabel);
            } else {
                originalPriceCell.innerHTML = `
                    <span style="color: black;">${formatPrice(prd.price)}</span>
                    <span style="color: black;" class="hidden">${prd.price}</span>
                `;
            }
        });

        productList.appendChild(productRow);
    });
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.forEach(function (tooltipTriggerEl) {
        new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

//hàm thiết lập phân trang cho product detail
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
    $('.page-button').on('click', function () {
        const page = $(this).data('page');
        if (page >= 1 && page <= totalPages) {
            fetchProductDetails(page);
        }
    });

    // Kiểm tra và chỉ cho phép nhập số trong input
    $('#pageInput').on('input', function () {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    // Xử lý sự kiện khi nhấn Enter trong input
    $('#pageInput').on('keypress', function (e) {
        if (e.key === 'Enter') {
            let inputPage = parseInt($(this).val());

            // Kiểm tra trang nhập vào
            if (isNaN(inputPage) || inputPage < 1) {
                inputPage = 1;
            } else if (inputPage > totalPages) {
                inputPage = totalPages;
            }

            fetchProductDetails(inputPage);
        }
    });
}

// Gọi hàm lấy dữ liệu product detail khi Tìm kiếm
$("#searchButton").on("click", function () {
    fetchProductDetails();
});

// Gọi hàm lấy dữ liệu product detail khi nhấn nút Reset
$("#resetButton").on("click", function () {
    $("#searchInput").val("");
    $("#minPrice").val("");
    $("#maxPrice").val("");
    $("#sortSelect").val("");
    $("#brandSelect").val(null);
    $("#categorySelect").val(null);
    $("#sizeSelect").val(null);
    $("#colorSelect").val(null);

    fetchProductDetails();
});

//hàm lấy id hóa đơn đang chọn
function getCurrentOrderId() {
    const orderIdInput = document.getElementById('currentOrderId');
    return orderIdInput ? orderIdInput.value : null;
}

//hàm cập nhật số lượng của giỏ hàng vào product detail
function updateProductStock(productId, quantityToSubtract) {
    $.ajax({
        url: `/admin/shopping-offlinee/${productId}/quantity`,
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

//hàm cập nhật số lượng của giỏ hàng và product detail
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

// thêm product detail vào giỏ hàng
function addProductToInvoice(productDetailId) {
    const currentOrderId = getCurrentOrderId();

    if (!currentOrderId) {
        Swal.fire('Lỗi', 'Chưa có đơn hàng.', 'error');
        return;
    }

    // Tìm sản phẩm trong giỏ hàng bằng input hidden productDetailId
    const existingProduct = $(`input#productDetailId[value="${productDetailId}"]`).closest('tr');

    if (existingProduct.length > 0) {
        // Sản phẩm đã tồn tại trong giỏ
        Swal.fire('', 'Sản phẩm đã có trong giỏ', 'warning');
    } else {
        // Sản phẩm chưa có trong giỏ - thêm mới
        const productRow = $(`button[onclick="addProductToInvoice(${productDetailId})"]`).closest('tr');
        const price = productRow.find('.hidden').text().trim(); // Lấy giá từ modal sản phẩm

        $.ajax({
            url: '/admin/shopping-offline/add',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                orderId: currentOrderId,
                productDetailId: productDetailId,
                quantity: 1,
                price: parseFloat(price)
            }),
            success: function (response) {
                if (response) {
                    updateProductStock(productDetailId, 1)
                    Swal.fire('Thành công', 'Thêm sản phẩm thành công', 'success')
                        .then(() => {
                            location.reload();
                        });
                } else {
                    Swal.fire('Lỗi', 'Không thể thêm sản phẩm', 'error');
                }
            },
            error: function (xhr) {
                Swal.fire('Lỗi', 'Đã xảy ra lỗi khi thêm sản phẩm', 'error');
            }
        });
    }
}

// hủy hóa đơn đang chọn
function cancelOrder(orderId) {
    Swal.fire({
        title: 'Xác nhận hủy đơn',
        text: "Bạn có chắc chắn muốn hủy đơn này không?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Hủy bỏ'
    }).then((result) => {
        if (result.isConfirmed) {
            updateOrderStatus(orderId, 0);
        }
    });
}

//thao tác của nút xóa trong giỏ hàng
$(document).on('click', '.delete-btn', function () {
    const orderId = $(this).data('order-id');
    const productDetailId = $(this).data('product-detail-id');
    const currentQty = $(this).data('current-qty');

    confirmDelete(orderId, productDetailId, currentQty);
});

// tooltip nút xóa ở giỏ hàng,productDetaiil modal
document.addEventListener('DOMContentLoaded', function () {
    const priceElements = document.querySelectorAll(".priceFormat");
    const priceRevenu = document.querySelectorAll(".priceRevenu");
    priceRevenu.forEach(element => {
        const price = element.getAttribute("data-price-revenu");
        if (price) {
            const formattedPrice = formatPrice(Number(price));
            element.textContent = formattedPrice;
        }
    });
    priceElements.forEach(element => {
        const price = element.getAttribute("data-price");
        if (price) {
            const formattedPrice = formatPrice(Number(price));
            element.textContent = formattedPrice;
        }
    });
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.forEach(function (tooltipTriggerEl) {
        new bootstrap.Tooltip(tooltipTriggerEl, {
            boundary: 'window'
        });
    });

});

// Hàm xác nhận xóa sản phẩm khỏi giỏ trả lại số lượng
function confirmDelete(orderId, productDetailId, currentQty) {
    Swal.fire({
        title: 'Xác nhận xóa sản phẩm khỏi giỏ ?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Xóa!',
        cancelButtonText: 'Hủy'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/shopping-offlinee/order-detail/delete',
                method: 'DELETE',
                contentType: 'application/json',
                data: JSON.stringify({orderId: orderId, productDetailId: productDetailId}),
                success: function (data) {
                    updateProductQuantity(productDetailId, currentQty)
                    Swal.fire('Đã xóa!', data, 'success').then(() => {
                        location.reload();
                    });
                },
                error: function (xhr, status, error) {
                    console.error('Error:', error);
                }
            });
        }
    });
}

//hàm lấy số lượng của productDetail
function getProductQuantity(productDetailId) {
    return $.ajax({
        url: `/admin/shopping-offlinee/${productDetailId}/product-detail-quantity`,
        method: 'GET',
        success: function (response) {
            console.log(`Số lượng sản phẩm: ${response}`);
        },
        error: function (xhr) {
            console.error(`Không thể lấy số lượng sản phẩm: ${xhr.responseText}`);
        }
    });
}

// thao tác của các nút select address
$(document).ready(function () {
    // Load provinces
    $.get('/admin/shopping-offlinee/provinces', function (data) {
        populateSelect('#province', data);
    });

    // Khi chọn tỉnh/thành phố
    $('#province').change(function () {
        const provinceId = $(this).val();
        if (provinceId) {
            $.get(`/admin/shopping-offlinee/districts/${provinceId}`, function (data) {
                populateSelect('#district', data);
                $('#ward').empty().append('<option value="">Chọn xã/phường</option>');
            });
        } else {
            $('#district, #ward').empty().append('<option value="">Chọn</option>');
        }
    });

    // Khi chọn quận/huyện
    $('#district').change(function () {
        const districtId = $(this).val();
        if (districtId) {
            $.get(`/admin/shopping-offlinee/wards/${districtId}`, function (data) {
                populateSelect('#ward', data);
            });
        } else {
            $('#ward').empty().append('<option value="">Chọn</option>');
        }
    });
});

// hàm render option and data address
function populateSelect(selector, items) {
    const $select = $(selector);
    $select.empty().append('<option value="">Chọn</option>');
    items.forEach(item => {
        $select.append(`<option value="${item.id}">${item.name}</option>`);
    });
}

//update quantity from cart
$(document).ready(function () {
    $('.quantity-input').each(function () {
        $(this).data('originalValue', $(this).val());
    });

    // Lắng nghe sự kiện 'keydown' trên ô nhập số lượng ở giỏ hàng
    $('.quantity-input').on('keydown', function (event) {
        const row = $(this).closest('tr'); // Lấy dòng cha chứa input
        const orderId = row.find(`input[type="hidden"][id="orderId"]`).val();
        const productDetailId = row.find(`input[type="hidden"][id="productDetailId"]`).val();
        const existingProduct = $(`input#productDetailId[value="${productDetailId}"]`).closest('tr');
        const productPrice = existingProduct.find('.priceInput').val();

        if (event.key === 'Enter') {
            event.preventDefault();
            const quantity = parseInt($(this).val());

            // nếu bỏ trống số lượng và ấn enter thì giá trị ko đổi
            if ($(this).val().trim() === "") {
                const originalValue = $(this).data('originalValue');
                const currentValue = $(this).val();

                if (currentValue !== originalValue) {
                    $(this).val(originalValue);
                }
                return;
            }
            if (quantity === 0) {
                //
                const originalQuantity = $(this).data('originalValue');
                confirmDelete(orderId, productDetailId, originalQuantity);
                return;
            }

            Swal.fire({
                title: 'Xác nhận thay đổi số lượng',
                text: "Cập nhật số lượng?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Xác nhận',
                cancelButtonText: 'Hủy'
            }).then((result) => {
                if (result.isConfirmed) {
                    if (!orderId || !productDetailId) {
                        Swal.fire('Lỗi', 'Không tìm thấy thông tin đơn hàng hoặc chi tiết sản phẩm.', 'error');
                        return;
                    }
                    const updateData = {
                        orderId: orderId,
                        productDetailId: productDetailId,
                        quantity: quantity,
                        price: productPrice
                    };

                    const originalQuantity = $(this).data('originalValue'); //số lượng trên giỏ
                    const quantityMinus = quantity - originalQuantity;
                    const quantityPlus = originalQuantity - quantity;

                    //quantity là số lượng nhập vào muốn thay đổi
                    //nếu số nhập vào lớn hơn số lượng sản phẩm trong giỏ hiện tại
                    if (quantity > originalQuantity) {
                        // Lấy số lượng sản phẩm trong kho
                        getProductQuantity(productDetailId).then(function (res) {
                            if (res === 0 || (res - quantityMinus) < 0) {
                                Swal.fire({
                                    title: 'Thông báo',
                                    text: 'Sản phẩm trong kho không đủ ' + quantity + ' sản phẩm',
                                    icon: 'warning',
                                    showConfirmButton: true
                                })
                                return;
                            }
                            $.ajax({
                                url: `/admin/shopping-offline/${orderId}/${productDetailId}`,
                                method: 'PUT',
                                contentType: 'application/json',
                                data: JSON.stringify(updateData),
                                success: function (response) {
                                    Swal.fire({
                                        title: 'Thành công',
                                        text: 'Số lượng đã được cập nhật',
                                        icon: 'success',
                                        timer: 1000,
                                        showConfirmButton: false
                                    }).then(() => {
                                        updateProductStock(productDetailId, quantityMinus);
                                        location.reload();
                                    });
                                },
                                error: function (xhr) {
                                    let errorMessage = 'Không thể cập nhật số lượng.';
                                    if (xhr.status === 400 && xhr.responseJSON) {
                                        errorMessage = Array.isArray(xhr.responseJSON) ?
                                            xhr.responseJSON.map(error => error.defaultMessage).join(', ') :
                                            xhr.responseJSON.error || errorMessage;
                                    }
                                    Swal.fire('Lỗi', errorMessage, 'error');
                                }
                            });
                        });
                    } else if (quantity < originalQuantity) {
                        $.ajax({
                            url: `/admin/shopping-offline/${orderId}/${productDetailId}`,
                            method: 'PUT',
                            contentType: 'application/json',
                            data: JSON.stringify(updateData),
                            success: function (response) {
                                Swal.fire({
                                    title: 'Thành công',
                                    text: 'Số lượng đã được cập nhật',
                                    icon: 'success',
                                    timer: 2000,
                                    showConfirmButton: false
                                }).then(() => {
                                    updateProductQuantity(productDetailId, quantityPlus);
                                    location.reload();
                                });
                            },
                            error: function (xhr) {
                                let errorMessage = 'Không thể cập nhật số lượng.';
                                if (xhr.status === 400 && xhr.responseJSON) {
                                    errorMessage = Array.isArray(xhr.responseJSON) ?
                                        xhr.responseJSON.map(error => error.defaultMessage).join(', ') :
                                        xhr.responseJSON.error || errorMessage;
                                }
                                Swal.fire('Lỗi', errorMessage, 'error');
                            }
                        });
                    }
                } else {
                    $(this).val($(this).data('originalValue'));
                }
            });
        } else {
            $('.quantity-input').on('blur', function () {
                const originalValue = $(this).data('originalValue');
                const currentValue = $(this).val();

                if (currentValue !== originalValue) {
                    $(this).val(originalValue);
                }
            });
        }
    });
});
// Format tổng tiền
const totalAmountElement = document.getElementById("totalAmount");
const totalAmount = parseInt(totalAmountElement.textContent.replace(/\D/g, ""), 10) || 0;
totalAmountElement.textContent = formatPrice(totalAmount);
// Handling customer input
const inputField = document.getElementById("customerAmount");
const changeAmount = document.getElementById("changeAmount");

// Hàm định dạng số thành chuỗi có dấu chấm
function formatCustomerPrice(value) {
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
}

inputField.addEventListener("input", () => {
    const numericValue = parseInt(inputField.value.replace(/[^0-9]/g, ""), 10) || '';
    inputField.value = formatCustomerPrice(numericValue);
    const change = numericValue - totalAmount;

    if (numericValue >= totalAmount) {
        changeAmount.textContent = formatCustomerPrice(change) + ' ₫';
        changeAmount.classList.remove("text-danger");
        changeAmount.classList.add("text-primary");
    } else {
        changeAmount.textContent = "Chưa đủ tiền";
        changeAmount.classList.remove("text-primary");
        changeAmount.classList.add("text-danger");
    }
});

// Confirm and process order
function confirmOrder(orderId) {
    let isValid = true;

    // Kiểm tra input #name
    const nameValue = $("#name").val().trim();
    if (nameValue === "") {
        isValid = false;
        $("#nameError").removeClass("d-none").text("Trường bắt buộc!");
    } else {
        $("#nameError").addClass("d-none");
    }

    // Kiểm tra input #phone
    const phoneValue = $("#phone").val().trim();
    const phonePattern = /^(\+84|84|0[3|5|7|8|9])([0-9]{8,9})$/;

    if (!phonePattern.test(phoneValue)) {
        isValid = false;
        $("#phoneError").removeClass("d-none").text("Số điện thoại không hợp lệ!");
    } else {
        $("#phoneError").addClass("d-none");
    }


    // Cập nhật trạng thái kiểm tra ngay khi có thay đổi trong input
    $("#name, #phone").on("input", function () {
        const inputId = $(this).attr("id");
        const errorId = inputId + "Error";
        const value = $(this).val().trim();

        if (value === "") {
            $("#" + errorId).removeClass("d-none").text("Trường bắt buộc!");
        } else if (inputId === "phone") {
            const phonePattern = /^(\+84|84|0[3|5|7|8|9])([0-9]{8,9})$/;

            if (!phonePattern.test(value)) {
                $("#" + errorId).removeClass("d-none").text("Số điện thoại không hợp lệ!");
            } else {
                $("#" + errorId).addClass("d-none");
            }
        } else {
            $("#" + errorId).addClass("d-none");
        }
    });

    const customerAmount = parseInt($("#customerAmount").val().replace(/\D/g, ""), 10) || 0;
    const totalPay = parseInt($("#totalAmount").text(), 10) || 0; // Assuming totalAmount is a span
    if (!isValid) {
        return;
    }
    if (customerAmount < totalPay) {
        Swal.fire({
            title: 'Cảnh báo',
            text: 'Tiền khách đưa chưa đủ!',
            icon: 'warning'
        });
        return;
    }

    Swal.fire({
        title: 'Xác nhận thanh toán ?',
        text: "Bạn có chắc chắn muốn thanh toán!",
        icon: 'info',
        showCancelButton: true,
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Hủy',
    }).then((result) => {
        if (result.isConfirmed) {
            updateOrderStatus1(orderId, 3, totalPay, 1);
        }
    });
}

// Thêm hóa đơn vào order history
function addOrderToHistory(orderId, note) {
    $.ajax({
        url: '/admin/order-history',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            orderId: orderId,
            status: 3,
            note: note
        }),
    });
}

// Update order status when checkout success
function updateOrderStatus1(orderId, status, totalPay, paymentMethodId) {
    const updateData = {
        status: status,
        code: document.getElementById('orderCode').innerText,
        toName: document.getElementById('name').value,
        toPhone: document.getElementById('phone').value,
        toAddress: document.getElementById('to_address').value,
        totalPay: totalPay,
        paymentMethodType: paymentMethodId,
        note: $('#orderNote').val().trim(),
        paymentStatus: 2,
        order_type: 'offline'
    };

    $.ajax({
        url: '/admin/shopping-offline/' + orderId,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(updateData),
        success: function () {
            Swal.fire({
                title: 'Thanh toán thành công!',
                icon: 'success',
                showConfirmButton: true,
                confirmButtonText: 'OK',
            }).then((result) => {
                if (result.isConfirmed) {
                    addOrderToHistory(orderId, updateData.note);
                    handleRemainingInvoices(orderId);
                }
            });
        },
        error: function (xhr) {
            let errorMessage = 'Không thể thanh toán đơn hàng.';
            if (xhr.status === 400) {
                errorMessage = "Không thấy đơn hàng";
            }
            Swal.fire('Lỗi', errorMessage, 'error');
        }
    });
}

//chuyển hóa đơn khi thực hiện xong các thao tác như thanh toán thành công,hủy đơn
function handleRemainingInvoices(orderId) {
    const allOrders = document.querySelectorAll('.invoice-item');
    let nextOrderId = null;

    allOrders.forEach(order => {
        const idOrder = order.getAttribute('onclick').match(/\d+/)[0]; // Lấy ID từ onclick
        if (idOrder != orderId) {
            nextOrderId = idOrder;
            return false;
        }
    });

    if (nextOrderId) {
        viewInvoice(nextOrderId);
    } else {
        Swal.fire({
            title: 'Thông báo',
            text: 'Không có hóa đơn nào, tạo mới ?',
            icon: 'info',
            showCancelButton: true,
            confirmButtonText: 'Tạo',
            cancelButtonText: 'Hủy',
        }).then((result) => {
            if (result.isConfirmed) {
                confirmCreateInvoice();
            } else {
                window.location.href = "/admin/shopping-offline/";
            }
        });
    }
}

