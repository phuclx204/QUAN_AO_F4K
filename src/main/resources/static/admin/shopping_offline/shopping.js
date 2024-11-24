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
    return $('.new-invoice-container button').length; // Lấy số lượng hóa đơn từ các nút trong invoice-container
}

// xác nhận tạo hóa đơn và giới hạn 10 hóa đơn
function confirmCreateInvoice() {

    const orderCount = getOrderCount();

    if (orderCount >= 10) {
        Swal.fire({
            title: 'Giới hạn hóa đơn',
            text: "Bạn đã đạt giới hạn 10 hóa đơn. Không thể tạo thêm hóa đơn mới.",
            icon: 'warning',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Đóng'
        });
        return;
    }

    Swal.fire({
        title: 'Xác nhận tạo hóa đơn',
        text: "Bạn có chắc chắn muốn tạo hóa đơn mới không?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Có, tạo hóa đơn!',
        cancelButtonText: 'Hủy'
    }).then((result) => {
        if (result.isConfirmed) {
            createInvoice();
        } else {
            handleRemainingInvoices()
        }
    });
}

// tạo hóa đơn với mã tự sinh, trạng thái 1,loại offline
function createInvoice() {
    const orderData = {
        userId: 999,
        code: generateUniqueCode(),
        status: 1,
        order_type: 'OFFLINE'
    };

    $.ajax({
        url: '/admin/shopping-offline',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(orderData),
        success: function (response) {
            const newInvoiceId = response.id;

            Swal.fire({
                title: 'Tạo hóa đơn thành công!',
                text: "Hóa đơn đã được tạo.",
                icon: 'success',
                timer: 2000,
                timerProgressBar: true,
                willClose: () => {
                    viewInvoice(newInvoiceId)
                }
            });
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

    // Gọi API lấy danh sách sản phẩm trong hóa đơn
    $.ajax({
        url: `/admin/shopping-offline/` + orderId + `/order-details`, // API để lấy danh sách sản phẩm
        method: 'GET',
        success: function (response) {
            // Tăng số lượng tồn kho cho từng sản phẩm
            response.forEach(productDetail => {
                updateProductQuantity(productDetail.productDetail.id, productDetail.quantity);
            });

            // Sau khi cập nhật tồn kho, thay đổi trạng thái đơn hàng
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

//hàm xác nhận cho trường nhập tiền
function isValidPrice(value) {
    const num = parseFloat(value);
    return !isNaN(num) && num >= 0;
}

// tự động cho các trường nhập tiền chỉ được nhập số
$(document).ready(function () {
    $("#minPrice, #maxPrice,.quantity-input").on("input", function () {
        // Lấy giá trị hiện tại và loại bỏ tất cả ký tự không phải số
        let value = $(this).val().replace(/[^0-9]/g, '');

        $(this).val(value);
    });

});

// hàm lấy dữ liệu product detail
function fetchProductDetails(page = 1, size = 5) {
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
                <button 
                    class="btn ${prd.quantity === 0 ? 'btn-secondary' : 'btn-success'}" 
                    ${prd.quantity === 0 ? 'disabled' : ''} 
                    onclick="addProductToInvoice(${prd.id})">
                    ${prd.quantity === 0 ? 'Đang cập nhật' : 'Chọn vào giỏ'}
                </button>
            </td>
        `;
        productList.appendChild(productRow);
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
        Swal.fire('Lỗi', 'Vui lòng chọn hóa đơn.', 'error');
        return;
    }

    // Tìm sản phẩm trong giỏ hàng bằng input hidden productDetailId
    const existingProduct = $(`input#productDetailId[value="${productDetailId}"]`).closest('tr');

    if (existingProduct.length > 0) {
        // Sản phẩm đã tồn tại trong giỏ
        const price = existingProduct.find('.priceInput').val(); // Lấy giá từ input có class priceInput
        const currentQty = parseInt(existingProduct.find('.quantity-input').val()) || 0; // Lấy số lượng từ input có class quantity-input
        const newQty = currentQty + 1;

        // Gọi API cập nhật
        $.ajax({
            url: `/admin/shopping-offline/${currentOrderId}/${productDetailId}`,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                orderId: currentOrderId,
                productDetailId: productDetailId,
                quantity: newQty,
                price: parseFloat(price)
            }),
            success: function (response) {
                if (response) {
                    updateProductStock(productDetailId, 1);
                    Swal.fire('Thành công', 'Cập nhật sản phẩm thành công', 'success')
                        .then(() => {
                            location.reload();
                        });
                } else {
                    Swal.fire('Lỗi', 'Không thể cập nhật sản phẩm', 'error');
                }
            },
            error: function (xhr) {
                Swal.fire('Lỗi', 'Đã xảy ra lỗi khi cập nhật sản phẩm', 'error');
            }
        });
    } else {
        // Sản phẩm chưa có trong giỏ - thêm mới
        const productRow = $(`button[onclick="addProductToInvoice(${productDetailId})"]`).closest('tr');
        const price = productRow.find('td:nth-child(3)').text().trim(); // Lấy giá từ bảng sản phẩm

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
            updateOrderStatus(orderId, 0); // Gọi hàm updateOrderStatus với status = 0
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
document.addEventListener('DOMContentLoaded', function () {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.forEach(function (tooltipTriggerEl) {
        new bootstrap.Tooltip(tooltipTriggerEl, {
            boundary: 'window' // Đảm bảo tooltip hiển thị trong vùng nhìn thấy
        });
    });
});
// tooltip nút xóa

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
                    // Swal.fire('Lỗi!', xhr.responseText || 'Có lỗi xảy ra.', 'error');
                    console.error('Error:', error); // Log lỗi
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

// hàm render option and data
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
        // Lưu giá trị ban đầu
        $(this).data('originalValue', $(this).val());
    });

    // Lắng nghe sự kiện 'keydown' trên ô nhập số lượng
    $('.quantity-input').on('keydown', function (event) {
        const row = $(this).closest('tr'); // Lấy dòng cha chứa input
        const orderId = row.find(`input[type="hidden"][id="orderId"]`).val();
        const productDetailId = row.find(`input[type="hidden"][id="productDetailId"]`).val();
        const existingProduct = $(`input#productDetailId[value="${productDetailId}"]`).closest('tr');
        const productPrice = existingProduct.find('.priceInput').val();

        // Kiểm tra nếu phím nhấn là Enter
        if (event.key === 'Enter') {
            event.preventDefault();
            const quantity = parseInt($(this).val());

            // nếu bỏ trống số lượng và ấn enter thì giá trị ko đổi
            if ($(this).val().trim() === "") {
                const originalValue = $(this).data('originalValue');
                const currentValue = $(this).val();

                // Nếu không có thay đổi, khôi phục lại giá trị ban đầu
                if (currentValue !== originalValue) {
                    $(this).val(originalValue);
                }
                return;
            }
            // nếu nhập vào số lượng bằng 0 thì xóa sản phẩm đó khỏi giỏ
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
                console.log("Order ID:", orderId);
                console.log("Product Detail ID:", productDetailId);
                console.log("Product Price:", productPrice);
                console.log("Quantity:", quantity);

                if (result.isConfirmed) {
                    if (!orderId || !productDetailId) {
                        Swal.fire('Lỗi', 'Không tìm thấy thông tin đơn hàng hoặc chi tiết sản phẩm.', 'error');
                        return;
                    }

                    // Tạo dữ liệu cần gửi đi
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
                                    timer: 1000,
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
            // Lắng nghe sự kiện 'blur' | Ấn ra chỗ khác ngoài ô nhập số lượng
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

// Currency formatting
const formatCurrency = (value) => {
    return new Intl.NumberFormat("vi-VN").format(value);
};

// sự kiện change cho phương thức thanh toán
document.querySelectorAll('input[name="paymentMethod"]').forEach((radio) => {
    radio.addEventListener('change', () => {
        const inputField = document.getElementById("customerAmount");
        const changeAmount = document.getElementById("changeAmount");
        const statusMessage = document.getElementById("statusMessage");
        const formattedNumber = document.getElementById("formattedNumber");

        if (radio.value === 'vnpay') {
            inputField.value = 0;
            inputField.disabled = true;

            changeAmount.textContent = 0;
            statusMessage.textContent = '';
            formattedNumber.textContent = 0;
        } else {
            inputField.disabled = false;
        }
    });
});

// Handling customer input
const inputField = document.getElementById("customerAmount");
const formattedNumber = document.getElementById("formattedNumber");
const totalAmountElement = document.getElementById("totalAmount");
const changeAmount = document.getElementById("changeAmount");
const statusMessage = document.getElementById("statusMessage");

const totalAmount = parseInt(totalAmountElement.textContent.replace(/\D/g, ""), 10) || 0;
totalAmountElement.textContent = formatCurrency(totalAmount);

inputField.addEventListener("input", () => {
    const numericValue = parseInt(inputField.value.replace(/[^0-9]/g, ""), 10) || 0;
    formattedNumber.textContent = formatCurrency(numericValue);
    inputField.value = numericValue;
    const change = numericValue - totalAmount;
    statusMessage.textContent = numericValue >= totalAmount ? "" : "Chưa đủ tiền";
    changeAmount.textContent = formatCurrency(change < 0 ? 0 : change);
});

// Confirm and process order
function confirmOrder(orderId) {
    const customerAmount = parseInt(inputField.value.replace(/\D/g, ""), 10) || 0;
    const totalPay = totalAmount;
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;

    Swal.fire({
        title: 'Xác nhận thanh toán?',
        text: "Bạn có chắc chắn muốn thanh toán!",
        icon: 'info',
        showCancelButton: true,
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Hủy',
    }).then((result) => {
        if (result.isConfirmed) {
            if (paymentMethod === 'cash') {
                if (customerAmount < totalPay) {
                    Swal.fire({title: 'Cảnh báo', text: 'Tiền khách đưa chưa đủ!', icon: 'warning'});
                    return;
                }
                updateOrderStatus1(orderId, 3, totalPay, 1);
            } else if (paymentMethod === 'vnpay') {
                window.location.href = `/admin/shopping-offline/checkout/vnpay/submitOrder?amount=${totalPay}&orderInfo=thanh-toan-hoa-don`;
                const i= parseInt(document.getElementById('checkoutStatus'));
                if(i===1){
                    updateOrderStatus1(orderId, 3, totalPay, 2);
                }
            }
        }
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
        paymentStatus: 2,
        order_type: 'OFFLINE'
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
    // Lấy danh sách hóa đơn còn lại
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
        // Nếu có hóa đơn tiếp theo, hiển thị
        viewInvoice(nextOrderId);
    } else {
        Swal.fire({
            title: 'Thông báo',
            text: 'Không còn hóa đơn nào, bạn có muốn tạo hóa đơn mới không?',
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


// thông tin khách đặt hàng
// $(document).ready(function () {
//     // Validate name input
//     $('#name').on('input', function () {
//         const nameLength = $(this).val().length;
//         $('#nameStatus').text(nameLength >= 8 ? 'Hợp lệ' : 'Tên cần ít nhất 8 ký tự').css('color', nameLength >= 8 ? 'green' : 'red');
//     });
//
//     // Validate phone input
//     $('#phone').on('input', function () {
//         const phoneNumber = $(this).val();
//         $('#phoneStatus').text(/^\d{10}$/.test(phoneNumber) ? 'Hợp lệ' : 'Số điện thoại phải có 10 chữ số').css('color', /^\d{10}$/.test(phoneNumber) ? 'green' : 'red');
//     });
//
//     // Validate address input
//     $('#to_address').on('input', function () {
//         const address = $(this).val().trim();
//         $('#addressStatus').text(address ? 'Hợp lệ' : 'Hãy nhập địa chỉ').css('color', address ? 'green' : 'red');
//     });
// });