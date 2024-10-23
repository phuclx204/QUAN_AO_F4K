let price = parseFloat(document.getElementById('product-price').dataset.price);
price = price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
price = price.replaceAll("₫", "VNĐ")
document.getElementById('product-price').innerText = price;

let priceList = document.getElementsByClassName("product-price-list")
console.log(priceList, ' -- priceList')

priceList.forEach(el => {
    let priceTmp = parseFloat(el.innerHTML);
    priceTmp = priceTmp.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    priceTmp = priceTmp.replaceAll("₫", "VNĐ")
    el.innerHTML = priceTmp
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