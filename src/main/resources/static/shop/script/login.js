$(document).ready(function () {
    $("#login-username").on('input blur', function () {
        validateField("#login-username", "#usernameError", "Username is not null");
    });

    $("#login-password").on('input blur', function () {
        validateField("#login-password", "#passwordError", "Password is not null");
    });

    // Khi nhấn submit form
    $(document).on('submit', '#loginForm', function (e) {
        e.preventDefault();

        const username = $("#login-username").val().trim();
        const password = $("#login-password").val().trim();

        // Reset tất cả các lỗi
        $(".invalid-feedback, #serverError").text("").removeClass("d-block");

        // Kiểm tra trường hợp lỗi và hiển thị
        if (!username) return showError("#usernameError", "Username is not null");
        if (!password) return showError("#passwordError", "Password is not null");

        const formData = {username, password};

        $.ajax({
            url: "/auth/authen",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (data) {
                setTokens(data.accessToken, data.refreshToken);
                // sendAuthenticatedRequest();
                $("#login-username, #login-password").val("");
            },
            error: function (xhr) {
                const errors = xhr.responseJSON;
                showError("#serverError", errors?.message || "An unexpected error occurred. Please try again later.");
            }
        });
    });

    // Hàm hiển thị lỗi
    function showError(selector, message) {
        $(selector).text(message).addClass("d-block");
    }

    // Hàm kiểm tra từng trường
    function validateField(inputSelector, errorSelector, errorMessage) {
        const value = $(inputSelector).val().trim();
        if (!value) {
            $(errorSelector).text(errorMessage).addClass("d-block");
        } else {
            $(errorSelector).text("").removeClass("d-block");
        }
    }

    // Lưu token vào localStorage
    function setTokens(accessToken, refreshToken) {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);
    }

// Giải mã token JWT để lấy thông tin payload
    function decodeToken(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));

            return JSON.parse(jsonPayload);
        } catch (error) {
            console.error("Token decoding error:", error);
            return null;
        }
    }

// Gửi yêu cầu đã xác thực và điều hướng dựa trên vai trò người dùng
//     function sendAuthenticatedRequest() {
//         const accessToken = localStorage.getItem("accessToken");
//
//         if (accessToken) {
//             const tokenPayload = decodeToken(accessToken);
//             const isAdmin = tokenPayload && tokenPayload.role && tokenPayload.role.includes("ROLE_ADMIN");
//             const isUser = tokenPayload && tokenPayload.role && tokenPayload.role.includes("ROLE_USER");
//
//
//             $.ajax({
//                 url: isAdmin ? "/dashboard/admin/" : "/api/v1/shop/home",
//                 type: "GET",
//                 headers: {
//                     "Authorization": "Bearer " + accessToken
//                 },
//                 success: function (data) {
//                     if (isAdmin) {
//                         window.location.href = "/dashboard/admin/";
//                         alert('Admin is ' + isAdmin);
//                     } else if (isUser) {
//                         window.location.href = "/api/v1/shop/home";
//                         window.location.reload(); //
//                         alert('User is ' + isUser);
//                     } else {
//                         alert('No valid role found, redirecting to login');
//                         window.location.href = "/auth/login";
//                     }
//                 },
//                 error: function (xhr, status, error) {
//                     if (xhr.status === 401) {
//                         console.error("Unauthorized access, please login again.");
//                         window.location.href = "/auth/login";
//                     } else {
//                         console.error("Error accessing protected resource:", error);
//                     }
//                 }
//             });
//         } else {
//             console.log("No access token available, redirecting to login.");
//             window.location.href = "/auth/login";
//         }
//     }

});


