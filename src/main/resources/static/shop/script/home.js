import {$ajax, buttonSpinner, getCommon, ref, syncFormWithDataObject, validateForm} from "/common/public.js";

const {convert2Vnd, transformData} = getCommon();
const {getValidate, clearValidation} = validateForm;

(function () {

    $(document).ready(function () {
        let priceList = document.getElementsByClassName("product-price-list")
        priceList.forEach(el => {
            el.innerHTML = convert2Vnd(el.innerHTML);
        })
    })
})()