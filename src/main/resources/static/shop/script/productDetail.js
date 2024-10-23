(function () {
    const {convert2Vnd} = getCommon();
    const price = document.getElementById('product-price').dataset.price;
    document.getElementById('product-price').innerText = convert2Vnd(price);

    let priceList = document.getElementsByClassName("product-price-list")

    priceList.forEach(el => {
        el.innerHTML = convert2Vnd(el.innerHTML);
    })

    $(".btn-color-picker").on("click", function (e) {
        const objSearch = {
            color: $(this).val()
        }

        const href = $ajax.createUrl(window.location.pathname, objSearch);

        const a = document.createElement("a");
        a.href = href;
        a.click()
    })
})()