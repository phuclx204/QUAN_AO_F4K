// Giả định rằng bạn có hàm này để lấy số lượng hóa đơn hiện tại
function getOrderCount() {
    return $('.invoice-container button').length; // Lấy số lượng hóa đơn từ các nút trong invoice-container
}

function confirmCreateInvoice() {
    const orderCount = getOrderCount(); // Lấy số lượng hóa đơn hiện tại

    if (orderCount >= 10) {
        Swal.fire({
            title: 'Giới hạn hóa đơn',
            text: "Bạn đã đạt giới hạn 10 hóa đơn. Không thể tạo thêm hóa đơn mới.",
            icon: 'warning',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Đóng'
        });
        return; // Ngăn không cho tiếp tục tạo hóa đơn
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
    // Dữ liệu cần gửi để tạo hóa đơn
    const orderData = {
        userId: 61, // Thay thế giá trị này nếu cần
        code: generateUniqueCode(), // Tạo mã duy nhất
        status: 1,
        order_type: 'OFFLINE' // Đặt loại đơn hàng là OFFLINE
    };

    // Gửi yêu cầu POST để tạo hóa đơn
    $.ajax({
        url: '/admin/shopping-offline',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(orderData),
        success: function (response) {
            Swal.fire({
                title: 'Tạo hóa đơn thành công!',
                text: "Hóa đơn đã được tạo.",
                icon: 'success',
                timer: 2000,
                timerProgressBar: true,
                willClose: () => {
                    // Tải lại trang sau khi thông báo đóng
                    location.reload();
                }
            });
        },
        error: function (xhr) {
            let errorMessage = 'Không thể tạo hóa đơn.';
            if (xhr.status === 400 && xhr.responseJSON) {
                // Lấy thông báo lỗi từ phản hồi JSON
                errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
            }
            Swal.fire('Lỗi', errorMessage, 'error');
        }
    });
}

// Giả định rằng bạn có một hàm để tạo mã duy nhất


// Hàm để tạo mã duy nhất cho hóa đơn
function generateUniqueCode() {
    return 'INV-' + Date.now(); // Cách đơn giản để tạo mã duy nhất
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
    const updateData = {
        status: status
    };

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
        // Điều hướng đến trang chi tiết hóa đơn với ID tương ứng
        window.location.href = '/admin/shopping-offline/' + id;
    }



    const modal = document.getElementById("productModal");
     const btnAddProduct = document.querySelector(".btn-add");
     const spanClose = document.querySelector(".close");
     const productList = document.getElementById("productList");

     // Mở modal khi bấm nút "Thêm sản phẩm"
     btnAddProduct.onclick = function () {
         modal.style.display = "block";
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

     // Function to fetch product details from the API
     function fetchProductDetails() {
         const page = 1;
         const size = 5;
         const sort = "id,desc";
         const search = document.getElementById("searchInput").value;

         fetch(`/admin/products/product-detail/list?page=${page}&size=${size}&sort=${sort}&search=${search}`)
             .then(response => response.json())
             .then(data => {
                 renderProductList(data.content);
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

function addProductToInvoice(productId) {
    const currentOrderId = getCurrentOrderId(); // Lấy ID hóa đơn hiện tại

    if (!currentOrderId) {
        Swal.fire('Lỗi', 'Không tìm thấy ID hóa đơn hiện tại.', 'error');
        return; // Thoát nếu không tìm thấy ID hóa đơn
    }

    // Chuẩn bị dữ liệu để gửi tới máy chủ
    const orderDetailData = {
        orderId: currentOrderId, // ID hóa đơn hiện tại
        productDetailId: productId, // ID sản phẩm từ nút nhấn
        quantity: 1, // Đặt số lượng mặc định
        price: 3000000 // Lấy giá sản phẩm
    };

    // Gửi yêu cầu AJAX POST để thêm sản phẩm vào hóa đơn
    $.ajax({
        url: '/admin/shopping-offline/add', // Đường dẫn tới endpoint để tạo OrderDetail
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(orderDetailData),
        success: function (response) {
            Swal.fire({
                title: 'Thêm sản phẩm thành công!',
                text: "Sản phẩm đã được thêm vào hóa đơn.",
                icon: 'success',
                timer: 2000,
                timerProgressBar: true,
                willClose: () => {
                    location.reload(); // Tải lại hoặc cập nhật giao diện khi cần
                }
            });
        },
        error: function (xhr) {
            let errorMessage = 'Không thể thêm sản phẩm vào hóa đơn.';
            if (xhr.status === 400 && xhr.responseJSON) {
                errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
            }
            Swal.fire('Lỗi', errorMessage, 'error');
        }
    });
}
function renderProductList(products) {
    productList.innerHTML = ''; // Xóa nội dung trước đó

    products.forEach(product => {
        const productItem = document.createElement('div');
        productItem.classList.add('product-item');

        // Thêm một định danh duy nhất cho sản phẩm
        const productId = product.id; // Giả sử sản phẩm có trường 'id'

        productItem.innerHTML = `
            <img src="${product.product.imageUrl}" alt="${product.product.name}">
            <h3>${product.product.name}</h3>
            <p class="price">${product.price} VND</p>
            <div class="actions">
                <div class="select-color">
                    ${product.color ? `<span class="color-circle" style="background-color: ${product.color.code};"></span>` : ''}
                </div>
                <button class="btn-buy" onclick="addProductToInvoice(${product.id})">Chọn Vào giỏi</button>
            </div>
        `;

        productList.appendChild(productItem);
    });
}
