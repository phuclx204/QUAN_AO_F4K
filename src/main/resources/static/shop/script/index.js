import { getCommon } from "/common/public.js";

const URL = "/shop"
const imageBlank = "https://firebasestorage.googleapis.com/v0/b/clothes-f4k.appspot.com/o/common%2Fdata_not_found.png?alt=media&token=36148ded-ba2c-4207-8525-2da16e7a8557";

(function () {

    const { convert2Vnd } = getCommon();
    const drawerCart = document.getElementById('offcanvasCart')

    drawerCart.addEventListener('show.bs.offcanvas', async event => {
        try {
            const result = await $ajax.callApi(URL + "/cart/list-cart", GET);
            $("#show-cart-item").empty();
            result.listData.forEach(el => {
                addCartItem(el)
            })
            replaceSubtotal(result.subtotal)

        } catch (e) {
            console.log(e)
        }
    })

    const addCartItem = (item) => {
        const srcImg = item.image ? item.image.path : imageBlank;
        const id = item.productDetail.idEncore;
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
                                ${item.productDetail.product.name}
                                <a class="ri-close-line btn-remover-cart" href="#" data-id="${id}"></a>
                            </h6>
                            <small class="d-block text-muted fw-bolder">Kích cỡ: ${item.productDetail.size.name}</small>
                            <small class="d-block text-muted fw-bolder">Số lượng: ${item.quantity}</small>
                        </div>
                        <p class="fw-bolder text-end m-0">${convert2Vnd(item.total + '')}</p>
                    </div>
            </div>`
        $("#show-cart-item").append(htmlCartItem);
    }
    const replaceSubtotal = (value) => {
        $("#show-sub-total").replaceWith(convert2Vnd(value))
    }

    $(document).on("click", ".btn-remover-cart", function (e) {
        e.preventDefault();

        const id = $(this).data("id")
        console.log("call remove cart and reload cart: ", id)
    })
})()

