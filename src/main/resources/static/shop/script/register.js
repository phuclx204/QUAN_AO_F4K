const {validateForm, clearValidation} = getValidate();
const {getFormValuesByName} = getCommon();

const idFormRegister = 'registerForm';

const URL = "/auth"

const validationRegister = {
    'register-username': [
        {
            rule: (value) => value.trim() !== "",
            message: "Username là bắt buộc."
        }
    ],
    'register-email': [
        {
            rule: (value) => /^\S+@\S+\.\S+$/.test(value),
            message: "Email không hợp lệ."
        }
    ],
    'register-password': [
        {
            rule: (value) => value.trim() !== "",
            message: "Mật khẩu bắt buộc"
        },
        {
            rule: (value) => value.length >= 8,
            message: "Mật khẩu phải có ít nhất 8 ký tự."
        }
    ],
    'register-repeat-password': [
        {
            rule: (value) => value === document.getElementById('register-password').value,
            message: "Mật khẩu không khớp."
        }
    ]
};

$(document).on('submit', '#registerForm', async function (e) {
    e.preventDefault();

    const isValid = validateForm('registerForm', validationRegister);
    if (isValid) {
        try {
            e.target.submit();
            clearValidation(idFormRegister)
        } catch (e) {
            console.log(e)
        }
    } else {
        console.log('Form không hợp lệ, vui lòng kiểm tra lại.');
    }
})