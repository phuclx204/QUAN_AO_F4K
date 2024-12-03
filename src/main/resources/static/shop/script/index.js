import {getCommon, $ajax} from "/common/public.js";

const URL = "/shop"
const imageBlank = "https://firebasestorage.googleapis.com/v0/b/clothes-f4k.appspot.com/o/common%2Fdata_not_found.png?alt=media&token=36148ded-ba2c-4207-8525-2da16e7a8557";

export {URL, imageBlank}
(function () {
    // fix cứng user
    const addUserLocalStorage = () => {
        const userInfo = {
            username: "user",
            portrait: "...",
        };
        const userInfoString = JSON.stringify(userInfo);
        localStorage.setItem("@f4k/account-basic-info", userInfoString);
    }
    const getUser = () => {
        let storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
        if (!storedUserInfo) {
            addUserLocalStorage();
            storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
        }
        return storedUserInfo;
    }

    const {convert2Vnd} = getCommon();

    /** Xử lý drawer cart **/
    const drawerCart = document.getElementById('offcanvasCart')

    const trangThaiSp = {
        conHang: 1,
        hetHang: 0
    }

    const getDataCart = async () => {
        const user = getUser();
        const result = await $ajax.get(URL + "/cart/list-cart", {username: user.username});
        localStorage.setItem("@f4k/cart", JSON.stringify(result));
        localStorage.setItem("@f4k/cart_total", result.itemCount);

        $("#nar-cart").text(result.itemCount ?? 0);
        return result;
    }

    const updateHtmlCart = async () => {
        const result = await getDataCart();

        $("#show-cart-item").empty();
        result.items.forEach(el => {
            updateHtmlCartItem(el)
        })

        replaceSubtotal(result.subtotal)
    }
    const updateHtmlCartItem = (item) => {
        console.log(item)
        const productDetail = item.productDetailDto;
        const product = productDetail.product;

        const id = productDetail.id;
        const srcImg = !product.image ? imageBlank : product.image.fileUrl;
        const total = convert2Vnd(item.total ? item.total : 0 + '');

        const urlProductDetail = `/shop/product/${product.slug}?color` + productDetail.color.hex.replace("#", "%23") + `&size=${productDetail.size.name}`;
        const productName = productDetail.status === trangThaiSp.conHang ? `<a href="${urlProductDetail}" class="text-decoration-none">${product.name}</a>` : `${product.name}`;

        const discountPercent = productDetail.promotion ? `<span class="badge card-badge bg-secondary">-${productDetail.promotion.discountValue}%</span>` : '';

        let htmlFooter = ``
        if (item.status === 1) {
            htmlFooter += `<div class="d-flex" style="font-size: 1.1rem">
                                <button class="btn btn-custom border btn-cartIndex-sub" data-id="${productDetail.id}" data-value="${item.quantity - 1}"><span class="mdi mdi-minus"></span></button>
                                    <span class="border pe-3 ps-3">${item.quantity}</span>
                                <button class="btn btn-custom border btn-cartIndex-plus" data-id="${productDetail.id}" data-value="${item.quantity + 1}"><span class="mdi mdi-plus"></span></button>
                            </div>
                            <p class="fw-bolder text-end m-0">${total}</p>`
        } else {
            htmlFooter += `<span class="text-danger">Hết hàng</span><strike class="fw-bolder text-end m-0">${total}</strike>`
        }

        const htmlCartItem =
            `<div class="row mx-0 pb-4 mb-4 border-bottom">
                    <div class="col-3 position-relative">
                        ${discountPercent}
                        <picture class="d-block bg-light">
                            <img class="img-fluid" src="${srcImg}"
                                 alt="Bootstrap 5">
                        </picture>
                    </div>
                    <div class="col-9">
                        <div>
                            <h6 class="justify-content-between d-flex align-items-start mb-2">
                                ${productName}
                                <a class="ri-close-line btn-remover-cart cursor-pointer" th:href="@{/shop/product/remove-cart/{id}(id=${productDetail.id})}" data-id="${id}"></a>
                            </h6>
                            <small class="d-block text-muted fw-bolder">Màu: ${productDetail.color.name}</small>
                            <small class="d-block text-muted fw-bolder">Kích cỡ: ${productDetail.size.name}</small>
                        </div>
                        <div class="d-flex mt-2 justify-content-between">
                            ${htmlFooter}
                        </div>
                    </div>
            </div>`
        $("#show-cart-item").append(htmlCartItem);
    }
    const replaceSubtotal = (value) => {
        $("#show-sub-total").text(convert2Vnd(value))
    }

    // btn delete cart
    $(document).on("click", ".btn-remover-cart", async function (e) {
        e.preventDefault();
        const id = $(this).data("id")
        await deleteProductFormCart(id);
    })

    const deleteProductFormCart = async (id) => {
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn xóa không?");
        if (isConfirmed.isConfirmed) {
            await $ajax.post(`/shop/product/remove-cart/${id}`).then(response => {
                $alterTop("success", "Đã xóa sản phẩm")
                getDataCart();
                $('#offcanvasCart').offcanvas('hide');
            })
            await updateHtmlCart();
        }

    }
    const updateQuantity = async (id, quantity) => {
        await $ajax.get(`/shop/product/update-quantity/${id}`, {quantity: quantity});
        $alterTop("success", "Cập nhật thành công")

        await updateHtmlCart();
        // $('#offcanvasCart').offcanvas('hide');
    }

    // btn add quantity
    $(document).on("click", ".btn-cartIndex-sub", async function (e) {
        const productDetailId = $(this).data("id");
        const value = $(this).data("value");
        if (value === 0) {
            await deleteProductFormCart(productDetailId);
        } else {
            await updateQuantity(productDetailId, value);
        }
    })

    $(document).on("click", ".btn-cartIndex-plus", async function (e) {
        const productDetailId = $(this).data("id");
        const value = $(this).data("value");

        await updateQuantity(productDetailId, value);
    })

    if (drawerCart != null) {
        drawerCart.addEventListener('show.bs.offcanvas', async event => {
            await updateHtmlCart();
        })
    }

    /** get list promotion **/
    const getListPromotion = async () => {
        const res = await $ajax.get("/shop/list-promotion");

        const $promotionDropdown = $('#promotion-dropdown');
        let dropDown = ``;
        res.forEach(el => {
            dropDown += `<li><a class="dropdown-item" href="/shop/promotion/${el.id}">${el.name}</a></li>`
        })
        $promotionDropdown.empty();
        $promotionDropdown.append(dropDown);
    }

    $(document).ready(async () => {
        await getListPromotion();
        await getDataCart();
    })
})()

