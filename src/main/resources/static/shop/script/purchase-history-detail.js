import {$ajax} from "/common/public.js";

(function () {

    let orderId = null;
    const $exampleModal = $("#exampleModal");

    /** Function scope **/
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

    function convertDate($this) {
        const date = new Date($this.text().trim());

        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0"); // Tháng trong JS bắt đầu từ 0
        const year = date.getFullYear();

        const formattedDate = `${hours}:${minutes} ${day}-${month}-${year}`;
        $this.text(formattedDate);
    }

    /** Event **/
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
        e.preventDefault();

        orderId = $(this).data("value");
        console.log(orderId, ' - orderId')
        $exampleModal.modal("show");
    })

    $(document).ready(function () {
        $('.format-time').each(function () {
            convertDate($(this))
        });
    })
})()