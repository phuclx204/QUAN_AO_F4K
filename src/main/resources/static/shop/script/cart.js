import {getCommon, $ajax} from "/common/public.js";

const {convert2Vnd} = getCommon();

(function () {
    const $tbody = $("#cart-items-body");

    const imageBlank = "https://firebasestorage.googleapis.com/v0/b/clothes-f4k.appspot.com/o/common%2Fdata_not_found.png?alt=media&token=36148ded-ba2c-4207-8525-2da16e7a8557";

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
            console.log(item)
            const product = item.productDetailDto.product;
            const productDetail = item.productDetailDto;

            const ttHetHang = item.status === 0;
            const ttNgungKinhDoanh = product.status === 0;

            const urlProductDetail = `/shop/product/${product.slug}?color` + productDetail.color.hex.replace("#", "%23") + `&size=${productDetail.size.name}`;
            const fileImg = product.image?.fileUrl ? product.image?.fileUrl : imageBlank;

            let htmlProductName = ``; // Cột chi tiết
            let htmlTdQuantity = ``; // Cột số lượng
            let htmlTdPrice = ``; // Cột thao tác

            const discountPercent = productDetail.promotion ? `<span class="badge card-badge bg-orange">-${productDetail.promotion.discountValue}%</span>` : '';
            const discountPrice = productDetail.promotion ?
                `<div class="d-flex flex-column">
                    <s class="fw-bolder mt-3 m-sm-0 text-muted">${convert2Vnd(productDetail.price + '')}</s>
                    <p class="fw-bolder mt-3 m-sm-0">${convert2Vnd(productDetail.discountValue + '')}</p>
                </div>` :
                `<p class="fw-bolder mt-3 m-sm-0">${convert2Vnd(productDetail.price + '')}</p>`

            if (ttNgungKinhDoanh) {
                htmlProductName = `<div><span class="badge bg-danger">Sản phẩm đã ngừng kinh doanh</span></div><div class="fw-bolder">${product.name}</div>`

                htmlTdQuantity = `<span class="text-muted mt-1"><strike>${item.quantity}</strike></span>`;

                htmlTdPrice =
                    `<div class="d-flex justify-content-between flex-column align-items-end h-100">
                        <button class="cursor-pointer bg-transparent border-0 btn-remove text-danger" data-id="${productDetail.id}"><i class="ri-close-circle-line ri-lg"></i></button>
                    </div>`;
            } else if (ttHetHang) {
                htmlProductName = `<div><span class="badge bg-orange">Sản phẩm đang hết hàng</span></div><div class="fw-bolder">${product.name}</div>`

                htmlTdQuantity = `<span class="text-muted mt-1"><strike>${item.quantity}</strike></span>`;

                htmlTdPrice =
                    `<div class="d-flex justify-content-between flex-column align-items-end h-100">
                        <button class="cursor-pointer bg-transparent border-0 btn-remove text-danger" data-id="${productDetail.id}"><i class="ri-close-circle-line ri-lg"></i></button>
                        <s>${discountPrice}</s>
                    </div>`;
            } else {
                htmlProductName = `<a href="${urlProductDetail}" class="text-decoration-none">${product.name}</a>`

                htmlTdQuantity =
                    `<div style="padding-left: 15px">Có sẵn ${productDetail.quantity} sản phẩm</div>
                    <div class="px-3 d-flex">
                        <button class="btn btn-custom border btn-cart-sub" data-id="${productDetail.id}" data-value="${item.quantity - 1}"><span class="mdi mdi-minus"></span></button>
                             <span class="border pe-3 ps-3 pt-1 pb-1">${item.quantity}</span>
                        <button class="btn btn-custom border btn-cart-plus" data-id="${productDetail.id}" data-value="${item.quantity + 1}"><span class="mdi mdi-plus"></span></button>
                    </div>`

                htmlTdPrice =
                    `<div class="d-flex justify-content-between flex-column align-items-end h-100">
                        <button class="cursor-pointer bg-transparent border-0 btn-remove text-danger" data-id="${productDetail.id}"><i class="ri-close-circle-line ri-lg"></i></button>
                        ${discountPrice}
                    </div>`
            }

            const $row = $(`
                <tr>
                    <td class="d-none d-sm-table-cell">
                        <picture class="d-block bg-light p-3 f-w-20 position-relative">
                            ${discountPercent}
                            <img class="img-fluid" src="${fileImg}" alt="">
                        </picture>
                    </td>
                    <td>
                        <div class="ps-sm-3">
                            <div class="mb-2 d-flex flex-column">
                               ${htmlProductName}
                            </div>
                            <small class="d-block">Màu: ${productDetail.color.name} / size: ${productDetail.size.name}</small>
                        </div>
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