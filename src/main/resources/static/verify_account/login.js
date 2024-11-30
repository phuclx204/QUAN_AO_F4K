import {$ajax, buttonSpinner, getCommon, syncFormWithDataObject, validateForm} from "/common/public.js";

const {getValidate, clearValidation} = validateForm;

(function () {
    const idForm = 'loginForm';
    const objDefault = {
        username: '',
        password: '',
        remember: false
    }
    const model = Object.assign({}, objDefault);
    const ruleForm = {
        'login-username': [
            {
                rule: (value) => value.trim() !== "",
                message: "Username là bắt buộc."
            }
        ],
        'login-password': [
            {
                rule: (value) => value.trim() !== "",
                message: "Mật khẩu bắt buộc"
            },
            {
                rule: (value) => value.length >= 6,
                message: "Mật khẩu phải có ít nhất 6 ký tự."
            }
        ]
    };

    const closeLoading = () => {}
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

    $(document).on('submit', '#loginForm', async function (e) {
        e.preventDefault();

        const isValid = await getValidate(idForm, ruleForm);
        if (!isValid) return;

        console.log(model)

        try {
            const response = await $ajax.post("/authentication/login", model)
        } catch (e) {
            console.log(e)
        }
    })

    syncFormWithDataObject({
        selectorParent: idForm,
        dataObject: model,
        initialValues: objDefault,
    })

})()