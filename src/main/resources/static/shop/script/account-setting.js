import {$ajax, buttonSpinner, getCommon, syncFormWithDataObject, validateForm} from "/common/public.js";

const {getValidate, clearValidation} = validateForm;
const {formatNumberByDot, getFormValuesByName} = getCommon()

$(document).ready(async function () {
    "use strict";

    /** Biến toàn cục  **/
    const userId = document.querySelector('meta[name="user-id"]').getAttribute("content");
    const username = document.querySelector('meta[name="user-name"]').getAttribute("content");

    const idFormCreate = 'formCreate';

    const $actionSave = $('#action-save');

    const objDefault = {
        id: userId,
        username: username,
        fullName: '',
        numberPhone: '',
        email: '',
        gender: '',
        addressDetail: '',
    }
    const objectCreateProduct = Object.assign({}, {...objDefault});

    const rules = {
        numberPhone: [
            {rule: (value) => value.trim() !== "", message: "Số điện thoại bắt buộc"},
            {rule: (value) => /^(0[3|5|7|8|9])[0-9]{8}$/.test(value), message: "Số điện thoại không đúng định dạng"},
        ],
        email: [
            {
                rule: (value) => /^\S+@\S+\.\S+$/.test(value),
                message: "Email không hợp lệ."
            }
        ]
    };

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

    const getDataUser = async () => {
        const data = await $ajax.get("/shop/account-setting/get-info", {id: userId, username: username});

        objDefault.email = data.email;
        objDefault.gender = data.gender ? data.gender : 0;
        objDefault.fullName = data.fullName;
        objDefault.addressDetail = data.addressDetail;
        objDefault.numberPhone = data.numberPhone;
    }

    $("#formCreate").on("submit", async function (e) {
        e.preventDefault();

        const isValid = await getValidate(idFormCreate, rules);
        if (!isValid) return;

        await $ajax.put("/shop/account-setting/update-info", objectCreateProduct);

        showAlert("Thông báo", "Cập nhật thông tin thành công", "success", "/shop/account-setting");
    })

    // change pass
    const idFormChangePass = 'formChangePass';

    const rulesChangePass = {
        oldPassword: [
            {
                rule: (value) => value.trim() !== "",
                message: "Mật khẩu cũ là bắt buộc."
            }
        ],
        newPassword: [
            {
                rule: (value) => value.trim() !== "",
                message: "Mật khẩu mới bắt buộc"
            },
            {
                rule: (value) => value.length >= 6,
                message: "Mật khẩu phải có ít nhất 6 ký tự."
            }
        ],
        rePassword: [
            {
                rule: (value) => value === document.getElementById('newPassword').value,
                message: "Mật khẩu không khớp."
            }
        ]
    };

    $('#formChangePass').on("submit", async function (e) {
        e.preventDefault();

        const isValid = await getValidate(idFormChangePass, rulesChangePass);
        if (!isValid) return;
        const data = getFormValuesByName(idFormChangePass)
        await $ajax.get("/shop/account-setting/update-password/" + userId + "?oldPassword=" + data.oldPassword + "&newPassword=" + data.newPassword);
        showAlert("Thông báo", "Cập nhật mật khẩu thành công", "success", "/shop/account-setting");
    })

    /** Gọi cuối khi tải xong bên trên **/
    $(document).ready(async function () {
        await getDataUser();

        syncFormWithDataObject({
            selectorParent: idFormCreate,
            dataObject: objectCreateProduct,
            initialValues: objDefault,
        });
    })
});

