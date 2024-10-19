const {validateForm, clearValidation} = getValidate();
const {getFormValuesByName} = getCommon();

const idFormLogin = 'loginForm';

const URL = "/auth"

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

// $(document).on('submit', '#loginForm', async function (e) {
//     e.preventDefault();
//
//     const isValid = validateForm('loginForm', validationLogin);
//     if (isValid) {
//         try {
//             loading().showLoading()
//             const href = await callApi(URL + "/login", POST, getFormValuesByName(idFormLogin));
//             clearValidation(idFormLogin);
//
//             if (href) {
//                 const a = document.createElement("a");
//                 a.href = href;
//                 a.click()
//             }
//         } catch (e) {
//             console.log(e)
//         } finally {
//             loading().hideLoading()
//         }
//     }
// })

