const {validateForm, clearValidation} = getValidate();

const validationLogin = {
    "login-username": [
        {
            rule: (value) => value.trim() !== "",
            message: "Tên người dùng hoặc email bắt buộc"
        }
    ],
    "login-password": [
        {
            rule: (value) => value.trim() !== "",
            message: "Mật khẩu bắt buộc"
        },
        {
            rule: (value) => value.length >= 8,
            message: "Mật khẩu phải có ít nhất 8 ký tự."
        }
    ]
}

$(document).on('submit', '#loginForm', async function (e) {
    e.preventDefault();

    const isValid = validateForm('loginForm', validationLogin);
    if (isValid) {
        try {
            e.target.submit();
            clearValidation(idFormRegister)
        } catch (e) {
            console.log(e)
        }
    }
})

