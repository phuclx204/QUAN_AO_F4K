import {$ajax, buttonSpinner, getCommon, syncFormWithDataObject, validateForm} from "/common/public.js";

const {getValidate, clearValidation} = validateForm;

(function () {
    const idFormRegister = 'registerForm';
    const objDefault = {
        username: '',
        email: '',
        password: '',
        roleId: 3
    }
    const model = Object.assign({}, objDefault);
    const ruleForm = {
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
                rule: (value) => value.length >= 6,
                message: "Mật khẩu phải có ít nhất 6 ký tự."
            }
        ],
        'register-repeat-password': [
            {
                rule: (value) => value === document.getElementById('register-password').value,
                message: "Mật khẩu không khớp."
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

    $(document).on('submit', '#registerForm', async function (e) {
        e.preventDefault();

        const isValid = await getValidate(idFormRegister, ruleForm);
        if (!isValid) return;

        console.log(model)

        try {
            await $ajax.post("/authentication/register", model)
            // Lưu accessToken vào localStorage hoặc sessionStorage
            // localStorage.setItem('accessToken', response.accessToken);
            // // Lưu refreshToken vào cookie (với HttpOnly và Secure)
            // document.cookie = `refreshToken=${response.refreshToken}; path=/; HttpOnly; Secure; SameSite=Strict`;
            //
            showAlert("Thông báo", "Đăng ký thành công", "success", "/authentication/login")
        } catch (e) {
            console.log(e)
        }
    })

    syncFormWithDataObject({
        selectorParent: idFormRegister,
        dataObject: model,
        initialValues: objDefault,
    })

    const response = {
        accessToken: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZW1vIiwiaWF0IjoxNzMyODY1ODg3LCJleHAiOjE3MzI5NTIyODd9.UOB7_ckIooAjWJyZyesPWBd-XAmpRXadgPwUWqFu6F8",
        refreshToken: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZW1vIiwiaWF0IjoxNzMyODY1ODg3LCJleHAiOjE3MzM0NzA2ODd9.Wwni3ImTye1Bh_CoZZ0tu2N5AsmVXNK_W7oAEg3dZnY"
    };

// Lưu accessToken vào localStorage hoặc sessionStorage
    localStorage.setItem('accessToken', response.accessToken);

// Lưu refreshToken vào cookies (ví dụ với httpOnly để bảo mật)
    document.cookie = `refreshToken=${response.refreshToken}; path=/; HttpOnly; Secure; SameSite=Strict`;
})()