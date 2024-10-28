// Giả định rằng bạn có hàm này để lấy số lượng hóa đơn hiện tại
function getOrderCount() {
    return $('.new-invoice-container button').length; // Lấy số lượng hóa đơn từ các nút trong invoice-container
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

function loadSelect(categoryId = null, brandId = null) {
    const categorySelect = $('#categorySelect');
    const brandSelect = $('#brandSelect');
    const colorSelect = $('#colorSelect');
    const sizeSelect = $('#sizeSelect');
    loadOptions('/admin/category/active', categorySelect, 'Tất cả', categoryId);
    loadOptions('/admin/brand/active', brandSelect, 'Tất cả', brandId);
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
    const orderData = {
        userId: 61,
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
        // Điều hướng đến trang chi tiết hóa đơn với ID tương ứng
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

function addProductToInvoice(productId) {
    const currentOrderId = getCurrentOrderId(); // Lấy ID hóa đơn hiện tại

    if (!currentOrderId) {
        modal.style.display = "none";
        Swal.fire('Lỗi', 'Vui lòng chọn hóa đơn.', 'error');
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
            modal.style.display = "none";
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
            modal.style.display = "none";
            let errorMessage = 'Không thể thêm sản phẩm vào hóa đơn.';
            if (xhr.status === 400 && xhr.responseJSON) {
                errorMessage = xhr.responseJSON.map(error => error.defaultMessage).join(', ');
            }
            Swal.fire('Lỗi', errorMessage, 'error');
        }
    });
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

    products.forEach((product,index) => {
        const productRow = document.createElement('tr'); // Tạo hàng mới

        productRow.innerHTML = `
            <td>${index + 1}</td>
            <td><img src="${product.product.imageUrl}" alt="${product.product.name}" style="width: 50px; height: 50px;"></td>
            <td>${product.product.name}</td>
            <td>${product.price} VND</td>
            <td>
                ${product.color ? `<span class="color-circle" style="background-color: ${product.color.hex}; display: inline-block; width: 20px; height: 20px; border-radius: 50%;"></span>` : ''}
            </td>
            <td>
                <button class="new-btn-buy" onclick="addProductToInvoice(${product.id})">Chọn Vào giỏ</button>
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

