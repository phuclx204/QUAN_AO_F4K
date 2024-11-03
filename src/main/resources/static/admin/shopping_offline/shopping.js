
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
    loadOptions('/admin/category/active', categorySelect, 'Tất cả', categoryId);
    loadOptions('/admin/brand/active', brandSelect, 'Tất cả', brandId);
}
function getOrderCount() {
    return $('.new-invoice-container button').length; // Lấy số lượng hóa đơn từ các nút trong invoice-container
}
function confirmCreateInvoice() {
    alert("Create Invoice button clicked!"); // Placeholder for actual implementation

    const orderCount = getOrderCount(); // Get the current invoice count

    if (orderCount >= 10) {
        Swal.fire({
            title: 'Giới hạn hóa đơn',
            text: "Bạn đã đạt giới hạn 10 hóa đơn. Không thể tạo thêm hóa đơn mới.",
            icon: 'warning',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Đóng'
        });
        return; // Prevent further invoice creation
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
        }
    });
}

function createInvoice() {
    const orderData = {
        userId: 61, // User ID
        code: generateUniqueCode(), // Generate unique invoice code
        status: 1, // Invoice status
        order_type: 'OFFLINE' // Invoice type is "OFFLINE"
    };

    // Send AJAX request to create the invoice
    $.ajax({
        url: '/admin/shopping-offline', // API URL
        method: 'POST', // Use POST method
        contentType: 'application/json', // Sending data as JSON
        data: JSON.stringify(orderData), // Convert invoice data to JSON string
        success: function (response) {
            const newInvoiceId = response.id; // Assuming response contains the new invoice ID

            // Show success message
            Swal.fire({
                title: 'Tạo hóa đơn thành công!',
                text: "Hóa đơn đã được tạo.",
                icon: 'success',
                timer: 2000,
                timerProgressBar: true,
                willClose: () => {
                    // Redirect to the new invoice page
                    window.location.href = `/admin/shopping-offline/${newInvoiceId}`;
                }
            });
        },
        error: function (xhr) {
            // Handle error if invoice creation fails
            let errorMessage = 'Không thể tạo hóa đơn.';
            if (xhr.status === 400 && xhr.responseJSON) {
                // Get error message from JSON response
                errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
            }
            Swal.fire('Lỗi', errorMessage, 'error'); // Display error message
        }
    });
}

// Hàm để tạo mã duy nhất cho hóa đơn
function generateUniqueCode() {
    return 'HD ' + Date.now(); // Cách đơn giản để tạo mã duy nhất
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
            // Gửi yêu cầu cập nhật trạng thái đơn hàng
            updateOrderStatus(orderId, 0); // Gọi hàm updateOrderStatus với status = 0

        }
    });
}
function updateOrderStatus(orderId, status) {
    const orderCode = document.getElementById('orderCode').innerText;

    const updateData = {
        status: status,
        code: orderCode
    }
    // Gửi yêu cầu PUT để cập nhật trạng thái đơn hàng
    $.ajax({
        url: '/admin/shopping-offline/' + orderId, // URL cập nhật đơn hàng
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(updateData),
        success: function (response) {
            Swal.fire({
                title: 'Hủy đơn thành công!',
                text: "Đơn hàng đã được hủy.",
                icon: 'success',
                timer: 2000,
                timerProgressBar: true,
                willClose: () => {
                    location.reload(); // Tải lại trang
                }
            });
        },
        error: function (xhr) {
            let errorMessage = 'Không thể hủy đơn.';
            if (xhr.status === 400 && xhr.responseJSON) {
                errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
            }
            Swal.fire('Lỗi', errorMessage, 'error');
        }
    });
}



function viewInvoice(id) {
        window.location.href = '/admin/shopping-offline/' + id;
 }

    const modal = document.getElementById("productModal");
     const btnAddProduct = document.querySelector(".new-btn-add");
     const spanClose = document.querySelector(".new-close");
     const productList = document.getElementById("productList");

     // Mở modal khi bấm nút "Thêm sản phẩm"
     btnAddProduct.onclick = function () {
         modal.style.display = "block";
         loadSelect();
         fetchProductDetails(); // Call API when the modal opens
     }

     // Đóng modal khi bấm vào dấu "x"
     spanClose.onclick = function () {
         modal.style.display = "none";
     }

     // Đóng modal khi bấm ra ngoài modal
     window.onclick = function (event) {
         if (event.target == modal) {
             modal.style.display = "none";
         }
     }

$('.search-form').on('submit', function (event) {
    event.preventDefault();
    const search = $('input[name="search"]').val();
    fetchProductDetails(1, 5, 'id,desc', search);
});
     // Function to fetch product details from the API
     function fetchProductDetails(page=1,size=5,sort='id,desc',search='') {
         search = document.getElementById("searchInput").value;

         fetch(`/admin/products/product-detail/list?page=${page}&size=${size}&sort=${sort}&search=${search}`)
             .then(response => response.json())
             .then(data => {
                 renderProductList(data.content);
                 setupPagination(data.totalPages,page)
             })
             .catch(error => {
                 console.error('Error fetching product details:', error);
             });
     }

function getCurrentOrderId() {
    const orderIdInput = document.getElementById('currentOrderId');
    const orderId = orderIdInput ? orderIdInput.value : null; // Lấy giá trị từ input ẩn
    console.log('Current Order ID:', orderId); // Ghi lại để kiểm tra
    return orderId; // Trả về ID hóa đơn
}


function updateProductStock(productId, quantityToSubtract) {
    $.ajax({
        url: `/admin/shopping-offlinee/${productId}/quantity`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({ quantity: quantityToSubtract }), // JSON đúng với DTO
        success: function(response) {
            console.log("Số lượng kho đã được cập nhật thành công.");
        },
        error: function(xhr) {
            console.error("Không thể cập nhật số lượng kho.", xhr);
            Swal.fire('Lỗi', 'Không thể cập nhật số lượng kho.', 'error');
        }
    });
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
            url: `/admin/shopping-offline/${currentOrderId}/${productId}`,
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
            url: '/admin/shopping-offline/add',
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





function renderProductList(products) {
    const productList = document.getElementById('productList');
    productList.innerHTML = ''; // Xóa nội dung trước đó

    if (products.length === 0) {
        productList.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; color: red;">Không có dữ liệu!</td>
            </tr>
        `;
        return;
    }

    products.forEach((product, index) => {
        const productRow = document.createElement('tr'); // Tạo hàng mới

        productRow.innerHTML = `
            <td>${index + 1}</td>
            <td><img src="${product.product.imageUrl}" alt="${product.product.name}" style="width: 50px; height: 50px;"></td>
            <td>${product.product.name}</td>
            <td>${product.price} VND</td>
            <td>
                ${product.color ? `<span class="color-circle" style="background-color: ${product.color.hex}; display: inline-block; width: 20px; height: 20px; border-radius: 50%;"></span>` : ''}
            </td>
            <td>${product.quantity}</td>
            <td>
                <button class="new-btn-buy" onclick="addProductToInvoice(${product.id}, ${product.price})">Chọn Vào giỏ</button>
            </td>
        `;

        productList.appendChild(productRow); // Thêm hàng vào bảng
    });
}

function setupPagination(totalPages, currentPage) {
    const pagination = $('#pagination');
    if (totalPages === 0) {
        pagination.hide();
        return;
    } else {
        pagination.show();
    }
    pagination.empty();

    pagination.append(`
        <button class="page-button" ${currentPage === 1 ? 'disabled' : ''} data-page="${currentPage - 1}">
            Trước
        </button>
        `);

    pagination.append(`
        <input type="text" id="pageInput" value="${currentPage}" style="width: 50px; text-align: center;" />
        <span> / ${totalPages}</span>
        `);

    pagination.append(`
        <button class="page-button" ${currentPage === totalPages ? 'disabled' : ''} data-page="${currentPage + 1}">
            Tiếp theo
        </button>
        `);

    $('.page-button').on('click', function () {
        const page = $(this).data('page');
        fetchProductDetails(page);
    });

    $('#pageInput').on('input', function () {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    $('#pageInput').on('keypress', function (e) {
        if (e.key === 'Enter') {
            let inputPage = parseInt($(this).val());

            if (isNaN(inputPage) || inputPage < 1) {
                inputPage = 1;
            } else if (inputPage > totalPages) {
                inputPage = totalPages;
            }

            fetchProductDetails(inputPage);
        }
    });
}
$(document).ready(function () {
    // Lấy tổng tiền từ `totalAmount`
    const totalAmount = parseInt($('#totalAmount').text().replace(/\D/g, ''), 10);
    $('#amountDue').text(totalAmount.toLocaleString() + ' ₫');

    // Khi nhập vào ô "Tiền khách đưa"
    $('#customerAmount').on('input', function () {
        const customerAmount = parseInt($(this).val().replace(/\D/g, ''), 10) || 0; // Remove non-numeric characters
        const changeAmount = customerAmount - totalAmount;

        if (customerAmount >= totalAmount) {
            // Đủ tiền
            $('#statusMessage').text('Đủ tiền').css('color', 'green');
            $('#changeAmount').text(changeAmount.toLocaleString() + ' ₫');
        } else {
            // Chưa đủ tiền
            $('#statusMessage').text('Chưa đủ tiền').css('color', 'red');
            $('#changeAmount').text('0 ₫');
        }
    });

});


$(document).ready(function() {
    $('.delete-btn').on('click', function() {
        const orderId = $(this).data('order-id');
        const productDetailId = $(this).data('product-detail-id');

        // Kiểm tra lại giá trị của orderId và productDetailId
        console.log("Order ID:", orderId, "Product Detail ID:", productDetailId);

        confirmDelete(orderId, productDetailId);
    });
});



// Hàm confirmDelete sử dụng SweetAlert2
function confirmDelete(orderId, productDetailId) {
    console.log("Order ID:", orderId, "Product Detail ID:", productDetailId); // Kiểm tra log
    Swal.fire({
        title: 'Xác nhận xóa sản phẩm khỏi giỏ',
        text: "Bạn có chắc chắn muốn bỏ sản phẩm này không?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Có, xóa!',
        cancelButtonText: 'Hủy'
    }).then((result) => {
        if (result.isConfirmed) {
            deleteOrderDetail(orderId, productDetailId);
        }
    });
}
function deleteOrderDetail(orderId, productDetailId) {
    fetch('/admin/shopping-offlinee/order-detail/delete', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ orderId: orderId, productDetailId: productDetailId })
    })
    .then(response => {
        console.log('Response:', response); // Log phản hồi
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text) });
        }
        return response.text();
    })
    .then(data => {
        Swal.fire('Đã xóa!', data, 'success').then(() => {
            location.reload(); // Load lại trang sau khi xóa thành công
        });
    })
    .catch(error => {
        Swal.fire('Lỗi!', error.message, 'error');
        console.error('Error:', error); // Log lỗi
    });
}
function updateOrderStatus1(orderId, status, totalPay) {
    const orderCode = document.getElementById('orderCode').innerText;
    const toName = document.getElementById('name').value;
    const toPhone = document.getElementById('phone').value;
    const toAddress = document.getElementById('to_address').value;

    const updateData = {
        status: status,
        code: orderCode,
        toName: toName,
        toPhone: toPhone,
        toAddress: toAddress,
        totalPay: totalPay, // Add totalPay to update data
        paymentStatus: 3, // Example status for successful payment
        order_type: 'OFFLINE'
    };

    $.ajax({
        url: '/admin/shopping-offline/' + orderId,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(updateData),
        success: function (response) {
            Swal.fire({
                title: 'Thanh toán thành công!',
                text: "Đơn hàng đã được thanh toán.",
                icon: 'success',
                timer: 2000,
                timerProgressBar: true,
                willClose: () => {
                    location.reload();
                }
            });
        },
        error: function (xhr) {
            let errorMessage = 'Không thể thanh toán đơn hàng.';
            if (xhr.status === 400 && xhr.responseJSON) {
                errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
            }
            Swal.fire('Lỗi', errorMessage, 'error');
        }
    });
}

function comfirmOrder(orderId) {
    const totalAmount = parseInt($('#totalAmount').text().replace(/\D/g, ''), 10);
    const customerAmount = parseInt($('#customerAmount').val().replace(/\D/g, ''), 10) || 0;

    // Check if total amount is valid (greater than 0)
    if (totalAmount <= 0) {
        Swal.fire({
            title: 'Lỗi',
            text: 'Hãy thêm ít nhất 1 sản phẩm để thanh toán.',
            icon: 'error',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Đóng'
        });
        return;
    }

    // Validate if required fields are filled correctly
    const toName = $('#name').val();
    const toPhone = $('#phone').val();
    const toAddress = $('#to_address').val();

    // Check if name, phone, and address are valid
    let validationMessage = '';
    if (toName.length < 8) {
        validationMessage += 'Tên cần ít nhất 8 ký tự. ';
        $('#nameStatus').text('Tên cần ít nhất 8 ký tự').css('color', 'red');
    }
    if (!/^\d{10}$/.test(toPhone)) {
        validationMessage += 'Số điện thoại phải có 10 chữ số. ';
        $('#phoneStatus').text('Số điện thoại phải có 10 chữ số').css('color', 'red');
    }
    if (toAddress.trim() === '') {
        validationMessage += 'Hãy nhập địa chỉ. ';
        $('#addressStatus').text('Hãy nhập địa chỉ').css('color', 'red');
    }

    if (validationMessage) {
        Swal.fire({
            title: 'Thông tin không hợp lệ',
            text: validationMessage,
            icon: 'error',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Đóng'
        });
        return;
    }

    // Check if customerAmount is enough
    if (customerAmount < totalAmount) {
        Swal.fire({
            title: 'Lỗi',
            text: 'Số tiền khách đưa không đủ để thanh toán.',
            icon: 'error',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Đóng'
        });
        return;
    }

    // Confirmation dialog
    Swal.fire({
        title: 'Xác nhận thanh toán',
        text: "Bạn có chắc chắn muốn thanh toán đơn này không?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Có, thanh toán!',
        cancelButtonText: 'Hủy'
    }).then((result) => {
        if (result.isConfirmed) {
            updateOrderStatus1(orderId, 3, totalAmount); // Call update with total amount as totalPay
        }
    });
}

// Validation Logic for Name, Phone, and Address on Input
$(document).ready(function () {
    // Validate name input for at least 8 characters
    $('#name').on('input', function () {
        const nameLength = $(this).val().length;
        if (nameLength >= 8) {
            $('#nameStatus').text('Hợp lệ').css('color', 'green');
        } else {
            $('#nameStatus').text('Tên cần ít nhất 8 ký tự').css('color', 'red');
        }
    });

    // Validate phone input for exactly 10 digits
    $('#phone').on('input', function () {
        const phoneNumber = $(this).val();
        if (/^\d{10}$/.test(phoneNumber)) {
            $('#phoneStatus').text('Hợp lệ').css('color', 'green');
        } else {
            $('#phoneStatus').text('Số điện thoại phải có 10 chữ số').css('color', 'red');
        }
    });

    // Validate address input for non-empty value
    $('#to_address').on('input', function () {
        const address = $(this).val().trim();
        if (address !== '') {
            $('#addressStatus').text('Hợp lệ').css('color', 'green');
        } else {
            $('#addressStatus').text('Hãy nhập địa chỉ').css('color', 'red');
        }
    });
});
//update
$(document).ready(function () {
    // Lắng nghe sự kiện 'keydown' trên ô nhập số lượng
    $('.quantity-input').on('keydown', function (event) {
        // Kiểm tra nếu phím nhấn là Enter
        if (event.key === 'Enter') {
            event.preventDefault(); // Ngăn chặn hành vi mặc định của Enter

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
                Swal.fire('Lỗi', 'Vui lòng nhập một số lượng hợp lệ.', 'error');
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
                url: `/admin/shopping-offline/${orderId}/${productDetailId}`,
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(updateData),
                success: function (response) {
                    Swal.fire('Thành công', 'Số lượng đã được cập nhật', 'success');
                    location.reload(); // Tải lại trang để cập nhật thay đổi
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
