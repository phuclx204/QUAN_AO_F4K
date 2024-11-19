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

    const {convert2Vnd} = getCommon();
    const drawerCart = document.getElementById('offcanvasCart')

    const getCart = async () => {
        let storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
        if (!storedUserInfo) {
            addUserLocalStorage();
            storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
        }
        const result = await $ajax.get(URL + "/cart/list-cart", {username: storedUserInfo.username});
        localStorage.setItem("@f4k/cart", JSON.stringify(result));
        localStorage.setItem("@f4k/cart_total", result.itemCount);

        $("#nar-cart").text(result.itemCount ?? 0);
        return result;
    }
    getCart().then();

    drawerCart.addEventListener('show.bs.offcanvas', async event => {
        try {
            const result = await getCart();
            $("#show-cart-item").empty();
            result.items.forEach(el => {
                addCartItem(el)
            })
            replaceSubtotal(result.subtotal)
        } catch (e) {
            console.log(e)
        }
    })
    const addCartItem = (item) => {
        const productDetail = item.productDetailDto;
        const product = productDetail.product;
        const srcImg = !product.image ? imageBlank : product.image.fileUrl;
        const id = productDetail.id;
        const total = convert2Vnd(item.total ? item.total : 0 + '');
        const htmlCartItem =
            `<div class="row mx-0 pb-4 mb-4 border-bottom">
                    <div class="col-3">
                        <picture class="d-block bg-light">
                            <img class="img-fluid" src="${srcImg}"
                                 alt="Bootstrap 5 Template by Pixel Rocket">
                        </picture>
                    </div>
                    <div class="col-9">
                        <div>
                            <h6 class="justify-content-between d-flex align-items-start mb-2">
                                ${productDetail.product.name}
                                <a class="ri-close-line btn-remover-cart cursor-pointer" th:href="@{/shop/product/remove-cart/{id}(id=${productDetail.id})}" data-id="${id}"></a>
                            </h6>
                            <small class="d-block text-muted fw-bolder">Màu: ${productDetail.color.name}</small>
                            <small class="d-block text-muted fw-bolder">Kích cỡ: ${productDetail.size.name}</small>
                            <small class="d-block text-muted fw-bolder">Số lượng: ${item.quantity}</small>
                        </div>
                        <p class="fw-bolder text-end m-0">${total}</p>
                    </div>
            </div>`
        $("#show-cart-item").append(htmlCartItem);
    }
    const replaceSubtotal = (value) => {
        $("#show-sub-total").text(convert2Vnd(value))
    }

    $(document).on("click", ".btn-remover-cart", async function (e) {
        e.preventDefault();
        const isConfirmed = await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn xóa không?");
        if (isConfirmed.isConfirmed) {
            const id = $(this).data("id")
            await $ajax.post(`/shop/product/remove-cart/${id}`).then(response => {
                $alterTop("success", "Đã xóa sản phẩm")
                getCart();
                $('#offcanvasCart').offcanvas('hide');
            })
        }
    })
})()

