import {$ajax, getCommon, ref, buttonSpinner} from "/common/public.js";

(function () {
    const {convert2Vnd} = getCommon();

    // định dạng lại loại tiền
    const price = document.getElementById('product-price').dataset.price;
    document.getElementById('product-price').innerText = convert2Vnd(price);
    let priceList = document.getElementsByClassName("product-price-list")
    priceList.forEach(el => {
        el.innerHTML = convert2Vnd(el.innerHTML);
    })
    // end

    const color = ref(null);

    color.value = $('input[name="color"]:checked').data("id");
    $(".btn-color-picker").on("click", function (e) {
        color.value = $(this).data('id');
        window.location.href = $ajax.createUrl(window.location.pathname, {color: color.value});
    })

    $(".btn-size-picker").on("click", function (e) {
        window.location.href = $ajax.createUrl(window.location.pathname, {color: color.value, size: $(this).data('id')});
    })

    $(document).on("submit", "#formAddCart", async function (e) {
        e.preventDefault();
        const quantity = $("#inputQuantity").val() || 1
        const formAction = $('#formAddCart').attr('action');
        try {
            buttonSpinner.show()
            await $ajax.post(formAction, null, {quantity: quantity}).then(rs => {
                $("#nar-cart").text(rs);
            })
            $alterTop("success", "Đã thêm trong giỏ hàng");
        } catch (e) {
            console.log(e)
        } finally {
            buttonSpinner.hidden()
        }
    })
})()