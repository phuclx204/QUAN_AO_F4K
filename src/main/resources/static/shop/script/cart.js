import {getCommon, $ajax} from "/common/public.js";

const {convert2Vnd} = getCommon();

(function () {
    const $tbody = $("#cart-items-body");

    const trangThaiSp = {
        conHang: 1,
        hetHang: 0
    }

    const getListCart = async () => {
        let storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));

        const res = await $ajax.get("/shop/cart/list-cart", {username: storedUserInfo.username});
        if (!res.items.length) {
            $('#btn-payment').addClass("disabled", true);
        } else {
            $('#btn-payment').removeClass("disabled", false);
        }
        return res;
    }
    const updateQuantity = async (id, quantity) => {
        await $ajax.get(`/shop/product/update-quantity/${id}`, {quantity: quantity});
        $alterTop("success", "Cập nhật thành công")

        await updateHtmlCart();
    }
    const deleteProductFormCart = async (id) => {
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn xóa không?");
        if (isConfirmed.isConfirmed) {
            await $ajax.post(`/shop/product/remove-cart/${id}`);
            $alterTop("success", "Đã xóa khỏi giỏ hàng!");
            await updateHtmlCart();
        }

    }

    const addProductCart = (items) => {
        items.forEach(item => {
            const product = item.productDetailDto.product;
            const productDetail = item.productDetailDto;

            const urlProductDetail = `/shop/product/${product.slug}?color` + productDetail.color.hex.replace("#", "%23") + `&size=${productDetail.size.name}`;
            const productName = productDetail.status === trangThaiSp.conHang ? `<a href="${urlProductDetail}" class="text-decoration-none">${product.name}</a>` : `${product.name}`;

            const htmlTdQuantity = productDetail.status === trangThaiSp.conHang ?
                `<div style="padding-left: 15px">Có sẵn ${productDetail.quantity} sản phẩm</div>
                <div class="px-3 d-flex">
                    <button class="btn btn-custom border btn-cart-sub" data-id="${productDetail.id}" data-value="${item.quantity - 1}"><span class="mdi mdi-minus"></span></button>
                         <span class="border pe-3 ps-3 pt-1 pb-1">${item.quantity}</span>
                    <button class="btn btn-custom border btn-cart-plus" data-id="${productDetail.id}" data-value="${item.quantity + 1}"><span class="mdi mdi-plus"></span></button>
                </div>`
                :
                `<span class="text-muted mt-1"><strike>${item.quantity}</strike></span>`;

            const htmlTdDetailProduct = productDetail.status === trangThaiSp.conHang ?
                `<div class="ps-sm-3">
                     <h6 class="mb-2 fw-bolder">${productName}</h6>
                     <small class="d-block text-muted"> ${productDetail.color.name} / ${productDetail.size.name}</small>
                </div>`
                :
                `<div class="ps-sm-3">
                     <h6 class="mb-2 fw-bolder">${product.name}</h6>
                     <small class="d-block text-muted mb-3"> ${productDetail.color.name} / ${productDetail.size.name}</small>
                     <h5 class="text-danger">Hết hàng</h5>
                </div>`

            const htmlTdPrice = productDetail.status === trangThaiSp.conHang ?
                `<div class="d-flex justify-content-between flex-column align-items-end h-100">
                     <button class="cursor-pointer bg-transparent border-0 btn-remove" data-id="${productDetail.id}"><i class="ri-close-circle-line ri-lg"></i></button>
                     <p class="fw-bolder mt-3 m-sm-0">${convert2Vnd(item.total + '')}</p>
                </div>`
                :
                `<div class="d-flex justify-content-between flex-column align-items-end h-100">
                     <button class="cursor-pointer bg-transparent border-0 btn-remove" data-id="${productDetail.id}"><i class="ri-close-circle-line ri-lg"></i></button>
                     <p class="fw-bolder mt-3 m-sm-0"><strike>${convert2Vnd(item.total + '')}</strike></p>
                </div>`

            const discountPercent = productDetail.promotion ? `<span class="badge card-badge bg-secondary">-${productDetail.promotion.discountValue}%</span>` : '';

            const $row = $(`
                <tr>
                    <td class="d-none d-sm-table-cell">
                        <picture class="d-block bg-light p-3 f-w-20 position-relative">
                            ${discountPercent}
                            <img class="img-fluid" src="${product.image.fileUrl}" alt="">
                        </picture>
                    </td>
                    <td>
                        ${htmlTdDetailProduct}
                    </td>
                    <td>
                        ${htmlTdQuantity}
                    </td>
                    <td class="f-h-0">
                        ${htmlTdPrice}
                    </td>
                </tr>
            `);
            $tbody.append($row);
        });
    }

    const updateHtmlCart = async () => {
        const data = await getListCart();
        $tbody.empty();
        addProductCart(data.items);

        // convert sô tiền => vnđ
        $("#showSubtotal").text(convert2Vnd(data.subtotal + ''))
        // set lại số lượng trong thông báo giỏ hàng
        $("#nar-cart").text(data.itemCount ?? 0);
    }

    // btn remove quantity
    $(document).on("click", ".btn-remove", async function (e) {
        const id = $(this).data("id");
        await deleteProductFormCart(id);
    })
    // btn add quantity
    $(document).on("click", ".btn-cart-sub", async function (e) {
        const productDetailId = $(this).data("id");
        const value = $(this).data("value");
        if (value === 0) {
            await deleteProductFormCart(productDetailId);
        } else {
            await updateQuantity(productDetailId, value);
        }
    })
    // btn sub quantity
    $(document).on("click", ".btn-cart-plus", async function (e) {
        const productDetailId = $(this).data("id");
        const value = $(this).data("value");

        await updateQuantity(productDetailId, value);
    })

    $(document).ready(async () => {
        await updateHtmlCart();
    })
})()