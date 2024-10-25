function confirmCreateInvoice() {
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
        addressId: null, // Thay thế giá trị này nếu cần
        userId: null, // Thay thế giá trị này nếu cần
        toName: document.querySelector('input[placeholder="Tên khách"]').value,
        toAddress: document.querySelector('input[placeholder="Địa chỉ chi tiết"]').value,
        toPhone: document.querySelector('input[placeholder="Điện thoại (F4)"]').value,
        totalPay: 0, // Cập nhật giá trị này nếu cần
        paymentMethodType: 1, // Thay thế giá trị này nếu cần
        note: document.querySelector('textarea[placeholder="Thêm ghi chú"]').value
    };

    // Gửi yêu cầu POST để tạo hóa đơn
    $.ajax({
        url: '/admin/shopping-offline', // URL của endpoint tạo hóa đơn
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(orderData),
        success: function (response) {
            Swal.fire({
                title: 'Tạo hóa đơn thành công!',
                text: "Hóa đơn đã được tạo.",
                icon: 'success',
                timer: 2000,
                timerProgressBar: true
            });
            // Có thể gọi hàm để cập nhật danh sách hóa đơn nếu cần
            loadDatas(); // Thay thế hoặc tạo hàm loadDatas() để lấy lại danh sách hóa đơn
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

     // Function to render product details in the modal
     function renderProductList(products) {
         productList.innerHTML = ''; // Clear previous content

         products.forEach(product => {
             const productItem = document.createElement('div');
             productItem.classList.add('product-item');

             productItem.innerHTML = `
                 <img src="${product.product.imageUrl}" alt="${product.product.name}">
                 <h3>${product.product.name}</h3>
                 <p class="price">${product.price} VND</p>
                 <div class="actions">
                     <div class="select-color">
                         ${product.color ? `<span class="color-circle" style="background-color: ${product.color.code};"></span>` : ''}
                     </div>
                     <button class="btn-add-cart">Nút j đây</button>
                     <button class="btn-buy">Chọn</button>
                 </div>
             `;

             productList.appendChild(productItem);
         });
     }
