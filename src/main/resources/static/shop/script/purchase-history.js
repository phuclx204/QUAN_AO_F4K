import {$ajax} from "/common/public.js";


(function () {

    let orderId = $(this).data("id");
    let productId = $(this).data("value");

    function showAlert(title, text, icon, href) {
        Swal.fire({
            title,
            text,
            icon,
            timer: 2000,
            showConfirmButton: false
        }).then(() => {
            window.location.href = href;
        });
    }

    $("#formCancel").on("submit", async function (e) {
        e.preventDefault();
        const selectedId = $('input[name="flexRadioDefault"]:checked').attr('id');
        let value = '';
        if (selectedId === '4') {
            value = $('#note').val();
        } else if (selectedId === '1') {
            value = 'Thay đổi địa chỉ giao hàng';
        }  else if (selectedId === '2') {
            value = 'Không muốn mua nữa';
        }  else if (selectedId === '3') {
            value = 'Thay đổi số lượng';
        }

        await $ajax.get("/shop/cancel-order", {orderId: orderId, note: value});
        showAlert("Thông báo", "Huỷ đơn hàng thành công", "success", "/shop/purchase-history");
    })

    $(document).on("click", ".btn-cancel", function (e) {
        orderId = $(this).data("id");
        productId = $(this).data("value");
    })
})()