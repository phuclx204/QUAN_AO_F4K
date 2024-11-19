import {getCommon, $ajax} from "/common/public.js";

const {convert2Vnd} = getCommon();

(function () {
    const $tbody = $("#cart-items-body");

    const getListCart = async () => {
        let storedUserInfo = JSON.parse(localStorage.getItem("@f4k/account-basic-info"));
        if (!storedUserInfo) {
            alert("LOG: người dùng chưa đăng nhập")
        }
        await $ajax.get("/shop/cart/list-cart", {username: storedUserInfo.username}).then(rs => {
            if (!rs.items.length) {
                $('#btn-payment').addClass("disabled", true);
            } else {
                $('#btn-payment').removeClass("disabled", false);
            }
            // convert sô tiền => vnđ
            $("#showSubtotal").text(convert2Vnd(rs.subtotal + ''))
            // set lại số lượng trong thông báo giỏ hàng
            $("#nar-cart").text(rs.itemCount ?? 0);
            // hiện thị giỏ hàng
            $tbody.empty();
            addProductCart(rs.items)
        });
    }

    getListCart().then();

    const addProductCart = (items) => {
        items.forEach(item => {
            const product = item.productDetailDto.product;
            const productDetail = item.productDetailDto;
            const $row = $(`
                <tr>
                    <td class="d-none d-sm-table-cell">
                        <picture class="d-block bg-light p-3 f-w-20">
                            <img class="img-fluid" src="${product.image.fileUrl}" alt="">
                        </picture>
                    </td>
                    <td>
                        <div class="ps-sm-3">
                            <h6 class="mb-2 fw-bolder">${product.name}</h6>
                            <small class="d-block text-muted"> ${productDetail.color.name} / ${productDetail.size.name}</small>
                        </div>
                    </td>
                    <td>
                        <div class="px-3">
                            <span class="small text-muted mt-1">${item.quantity}</span>
                        </div>
                    </td>
                    <td class="f-h-0">
                        <div class="d-flex justify-content-between flex-column align-items-end h-100">
                             <button class="cursor-pointer bg-transparent border-0 btn-remove" data-id="${productDetail.id}"><i class="ri-close-circle-line ri-lg"></i></button>
                             <p class="fw-bolder mt-3 m-sm-0">${convert2Vnd(item.total + '')}</p>
                        </div>
                    </td>
                </tr>
            `);
            $tbody.append($row);
        });
    }

    $(document).on("click", ".btn-remove", async function (e) {
        const id = $(this).data("id");
        await $confirm("info", "Nhắc nhở", "Bạn có chắc muốn xóa không?").then(rs => {
            if (!rs.isConfirmed) return;
            try {
                $ajax.post("/shop/product/remove-cart/" + id).then(_rs => {
                    getListCart();
                });
                $alterTop("success", "Đã xóa khỏi giỏ hàng!")
            } catch (e) {
                console.log(e)
            }
        })
    })
})()