$(document).ready(function () {
    function formatCurrency(amount) {
        return amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    }

    // Duyệt qua tất cả các thẻ có id là 'productPrice' và 'totalPrice' để định dạng tiền tệ
    $('#productPrice, #totalPrice, #total').each(function () {
        var price = parseFloat($(this).text().replace(/[^0-9.-]+/g, "")); // Lấy giá trị số và loại bỏ ký tự không phải số

        if (!isNaN(price)) {
            $(this).text(formatCurrency(price)); // Định dạng lại giá trị
        }
    });

    // Modal actions
    const modal = document.getElementById("productModal");
    const btnAddProduct = document.querySelector(".new-btn-add");
    const spanClose = document.querySelector(".new-close");
    const productList = document.getElementById("productList");

    // Mở modal khi bấm nút "Làm mới"
    btnAddProduct.onclick = function () {
        modal.style.display = "block";
        loadSelect();
        fetchProductDetails();
    };

    spanClose.onclick = function () {
        modal.style.display = "none";
    };

    // Đóng modal khi bấm ra ngoài modal
    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };

function loadOptions(endpoint, selectElement, defaultOption, selectedId = null) {
    $.get(endpoint, function (data) {
        selectElement.empty().append(`<option value="">${defaultOption}</option>`);
        data.forEach(item => {
            const selected = item.id == selectedId ? 'selected' : '';
            selectElement.append(`<option value="${item.id}" ${selected}>${item.name}</option>`);
        });
    });
}


function loadSelect(categoryId = null, brandId = null) {
    const categorySelect = $('#categorySelect');
    const brandSelect = $('#brandSelect');
    const colorSelect = $('#colorSelect');
    const sizeSelect = $('#sizeSelect');
    loadOptions('/api/v1/admin/category/active', categorySelect, 'Tất cả', categoryId);
    loadOptions('/api/v1/admin/brand/active', brandSelect, 'Tất cả', brandId);
}
    // Hàm hiển thị danh sách sản phẩm
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
                <td>${formatCurrency(product.price)}</td>
                <td>
                    ${product.color ? `<span class="color-circle" style="background-color: ${product.color.hex}; display: inline-block; width: 20px; height: 20px; border-radius: 50%;"></span>` : ''}
                </td>
                <td>${product.quantity}</td>
                <td>
                    <button class="new-btn-buy" onclick="addProductToInvoice(${product.id},${product.price})">Chọn vào giỏ</button>
                </td>
            `;

            productList.appendChild(productRow);
        });
    }

    // Hàm tìm kiếm sản phẩm
    $('.search-form').on('submit', function (event) {
        event.preventDefault();
        const search = $('input[name="search"]').val();
        fetchProductDetails(1, 5, 'id,desc', search);
    });

    // Fetch sản phẩm từ API
    function fetchProductDetails(page = 1, size = 5, sort = 'id,desc', search = '') {
        search = $('input[name="search"]').val();

        fetch(`/api/v1/admin/products/product-detail/list?page=${page}&size=${size}&sort=${sort}&search=${search}`)
            .then(response => response.json())
            .then(data => {
                renderProductList(data.content);
                setupPagination(data.totalPages, page);
            })
            .catch(error => {
                console.error('Error fetching product details:', error);
            });
    }

    // Hàm thiết lập phân trang (cần phải có phần logic phân trang bên dưới)
    function setupPagination(totalPages, currentPage) {
        $('#totalPagesText').text(`Trang ${currentPage} / ${totalPages}`);
        // Cập nhật nút "Trước" và "Tiếp theo" nếu có
        $('#prevPage').prop('disabled', currentPage === 1);
        $('#nextPage').prop('disabled', currentPage === totalPages);
    }
});

function updateProductStock(productId, quantityToSubtract) {
    $.ajax({
        url: `/api/v1/admin/shopping-offlinee/${productId}/quantity`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({ quantity: quantityToSubtract }),
        success: function(response) {
            console.log("Số lượng kho đã được cập nhật thành công.");
        },
        error: function(xhr) {
            console.error("Không thể cập nhật số lượng kho.", xhr);
            Swal.fire('Lỗi', 'Không thể cập nhật số lượng kho.', 'error');
        }
    });
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
        modal.style.display = "none";
        Swal.fire('Lỗi', 'Vui lòng chọn hóa đơn.', 'error');
        return;
    }

    // Kiểm tra nếu sản phẩm đã có trong hóa đơn
    const existingRow = $(`tr`).filter(function() {
        return $(this).find(`input[type="hidden"][id="productDetailId"]`).val() == productId;
    });

    if (existingRow.length > 0) {
        // Nếu sản phẩm đã có, cập nhật số lượng
        const quantityInput = existingRow.find('.quantity-input');
        const currentQuantity = parseInt(quantityInput.val());
        const newQuantity = currentQuantity + 1; // Tăng số lượng lên 1
        quantityInput.val(newQuantity);

        // Gửi yêu cầu cập nhật hóa đơn qua AJAX
        const updateData = {
            orderId: currentOrderId,
            productDetailId: productId,
            quantity: newQuantity,
            price: productPrice
        };

        $.ajax({
            url: `/api/v1/admin/shopping-offline/${currentOrderId}/${productId}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(updateData),
            success: function(response) {
                // Gọi API cập nhật kho sau khi cập nhật hóa đơn thành công
                updateProductStock(productId, 1); // Trừ 1 sản phẩm từ kho

  modal.style.display = "none";
                Swal.fire({
                    title: 'Thêm thành công vào giỏ hàng!',
                    text: "Sản Phẩm đã được thêm vào Giỏ",
                    icon: 'success',
                    timer: 2000,
                    timerProgressBar: true,
                    willClose: () => {
                        location.reload();
                    }
                });

            },
            error: function(xhr) {
                let errorMessage = 'Không thể cập nhật số lượng.';
                if (xhr.status === 400 && xhr.responseJSON) {
                    errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                }
                Swal.fire('Lỗi', errorMessage, 'error');
            }
        });
    } else {
        // Nếu sản phẩm chưa có, thêm mới vào hóa đơn
        const orderDetailData = {
            orderId: currentOrderId,
            productDetailId: productId,
            quantity: 1,
            price: productPrice
        };

        $.ajax({
            url: '/api/v1/admin/shopping-offline/add',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(orderDetailData),
            success: function(response) {
                // Gọi API cập nhật kho sau khi thêm sản phẩm vào hóa đơn thành công
                updateProductStock(productId, 1); // Trừ 1 sản phẩm từ kho

                modal.style.display = "none";
                Swal.fire({
                    title: 'Thêm sản phẩm thành công!',
                    text: "Sản phẩm đã được thêm vào hóa đơn.",
                    icon: 'success',
                    timer: 2000,
                    timerProgressBar: true,
                    willClose: () => {
                        location.reload();
                    }
                });
            },
            error: function(xhr) {
                let errorMessage = 'Không thể thêm sản phẩm vào hóa đơn.';
                if (xhr.status === 400 && xhr.responseJSON) {
                    errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
                }
                Swal.fire('Lỗi', errorMessage, 'error');
            }
        });
    }
}

//update
$(document).ready(function () {
    // Lắng nghe sự kiện 'keydown' trên ô nhập số lượng
    $('.quantity-input').on('keydown', function (event) {
        // Kiểm tra nếu phím nhấn là Enter
        if (event.key === 'Enter') {
            event.preventDefault();

            // Lấy giá trị số lượng
            const quantity = parseInt($(this).val());

            // Lấy ID order và productDetail từ các trường hidden
            const row = $(this).closest('tr'); // Lấy dòng cha chứa input
            const orderId = row.find(`input[type="hidden"][id="orderId"]`).val(); // Tìm giá trị order ID
            const productDetailId = row.find(`input[type="hidden"][id="productDetailId"]`).val(); // Tìm giá trị product detail ID

            const priceText = row.find('#productPrice').text().trim(); // Sử dụng jQuery để lấy giá
            const productPrice = parseFloat(priceText.replace(/,/g, '').replace('₫', '').trim()); // Chuyển đổi thành số thực

            console.log("Order ID:", orderId);
            console.log("Product Detail ID:", productDetailId);
            console.log("Product Price:", productPrice);
            console.log("Quantity:", quantity);

            // Kiểm tra số lượng hợp lệ
            if (isNaN(quantity) || quantity <= 0) {
                Swal.fire({
                    title: 'Lỗi',
                    text: 'Vui lòng nhập một số lượng hợp lệ.',
                    icon: 'error',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    location.reload();
                });
                return;
            }

            // Kiểm tra xem các ID có giá trị hợp lệ không
            if (!orderId || !productDetailId) {
                Swal.fire('Lỗi', 'Không tìm thấy thông tin đơn hàng hoặc chi tiết sản phẩm.', 'error');
                return;
            }

            // Tạo dữ liệu cần gửi đi
            const updateData = {
                orderId: orderId,
                productDetailId: productDetailId,
                quantity: quantity,
                price: productPrice // Nếu bạn cần gửi giá sản phẩm
            };

            // Gửi dữ liệu qua API PUT để cập nhật số lượng sản phẩm
            $.ajax({
                url: `/api/v1/admin/shopping-offline/${orderId}/${productDetailId}`,
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(updateData),
                success: function (response) {
                    Swal.fire({
                        title: 'Thành công',
                        text: 'Số lượng đã được cập nhật',
                        icon: 'success',
                        timer: 1000, // Thời gian hiển thị thông báo (2000ms = 2 giây)
                        showConfirmButton: false // Ẩn nút xác nhận
                    }).then(() => {
                        location.reload(); // Tải lại trang sau khi thông báo đã ẩn
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
    });
});
