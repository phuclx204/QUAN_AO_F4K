$(document).ready(function () {
    // Hàm định dạng tiền tệ Việt Nam (VND)
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
        loadSelect(); // Gọi hàm load dữ liệu chọn sản phẩm
        fetchProductDetails(); // Gọi API khi mở modal
    };

    // Đóng modal khi bấm vào dấu "x"
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
    loadOptions('/admin/category/active', categorySelect, 'Tất cả', categoryId);
    loadOptions('/admin/brand/active', brandSelect, 'Tất cả', brandId);
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
                    <button class="new-btn-buy" onclick="addProductToInvoice(${product.id}, ${product.price})">Chọn vào giỏ</button>
                </td>
            `;

            productList.appendChild(productRow); // Thêm sản phẩm vào bảng
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
        search = $('input[name="search"]').val(); // Lấy giá trị tìm kiếm từ input

        fetch(`/admin/products/product-detail/list?page=${page}&size=${size}&sort=${sort}&search=${search}`)
            .then(response => response.json())
            .then(data => {
                renderProductList(data.content); // Hiển thị sản phẩm
                setupPagination(data.totalPages, page); // Cập nhật phân trang
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
