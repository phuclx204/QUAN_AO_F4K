const imageBlank = "https://firebasestorage.googleapis.com/v0/b/clothes-f4k.appspot.com/o/common%2Fdata_not_found.png?alt=media&token=36148ded-ba2c-4207-8525-2da16e7a8557";

const getCommon = () => {
    /**
     * @Param formId is name id <form></form>
     */
    function getFormValuesByName(formId) {
        if (formId.startsWith(".")) {
            formId = formId.substring(1)
        }
        const form = document.getElementById(formId);

        // Chọn tất cả các input có thuộc tính `name`
        const inputs = form.querySelectorAll('input[name]');

        const values = {};
        inputs.forEach(input => {
            values[input.name] = input.value;
        });
        return values;
    }

    function removeNullProperties(obj) {
        return Object.fromEntries(
            Object.entries(obj).filter(([key, value]) => value !== null)
        );
    }

    function formatNumberByDot(value) {
        let numericValue = value.replace(/\D/g, '');
        return numericValue.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    }

    const getPagination = (currentPage, totalPage) => {
        if (currentPage === 1 && totalPage === 0) return []
        const pages = [];

        if (currentPage < 1 || currentPage > totalPage) {
            console.error('currentPage không hợp lệ.');
            return;
        }

        if (totalPage <= 3) {
            for (let i = 1; i <= totalPage; i++) {
                pages.push(i);
            }
        } else {
            if (currentPage === 1) {
                pages.push(1, 2, 3);
            } else if (currentPage === totalPage) {
                pages.push(totalPage - 2, totalPage - 1, totalPage);
            } else if (currentPage === totalPage - 1) {
                pages.push(totalPage - 2, totalPage - 1, totalPage);
            } else {
                pages.push(currentPage - 1, currentPage, currentPage + 1);
            }
        }
        return pages;
    };

    return {
        getFormValuesByName,
        removeNullProperties,
        formatNumberByDot,
        getPagination
    }
}

// for validate
const getValidate = () => {
    /**
     * @Param formId is name id <form></form>
     * @Param validationRules is object { name:id-input: list object [{ rule: rule check, mess: mess when validate }] }
     */
    const validateForm = (formId, validationRules = {}) => {
        const form = document.getElementById(formId);
        const inputs = form.querySelectorAll('input');
        let isValid = true;

        inputs.forEach(input => {
            const rules = validationRules[input.id];
            const invalidFeedback = input.parentElement.querySelector('.invalid-feedback');

            if (rules) {
                for (let {rule, message} of rules) {
                    if (!rule(input.value)) {
                        invalidFeedback.textContent = message;
                        input.setCustomValidity("Invalid");
                        invalidFeedback.style.display = "block";
                        isValid = false;
                        break;
                    } else {
                        invalidFeedback.style.display = "none";
                        invalidFeedback.textContent = "";
                        input.setCustomValidity("");
                    }
                }
            }
        });

        return isValid;
    };

    /**
     *
     * @Param formId is id form name
     */
    function clearValidation(formId) {
        if (!formId.startsWith(".")) {
            formId = "." + formId
        }
        const form = document.querySelector(formId)

        form.classList.remove('was-validated');

        const inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.value = '';
            input.classList.remove('is-invalid', 'is-valid');

            const invalidFeedback = input.parentElement.querySelector('.invalid-feedback');
            if (invalidFeedback) {
                invalidFeedback.style.display = "none";
            }
        });
    }

    return {
        validateForm,
        clearValidation
    }
}

// for ajax
const POST = 'POST';
const GET = 'GET';
const PUT = 'PUT';
const DELETE = 'DELETE';

const $ajax = (function() {
    const createUrl = (url, params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        if (queryString) {
            url += `?${queryString}`;
        }

        return url;
    }

    /**
     *
     * @Param url is api name
     * @Param method is rest method | post | put | delete | get
     * @Param data is data send to controller, in body
     */
    function callApi(url, method = 'POST', data = null) {
        return new Promise((resolve, reject) => {
            if (!url || typeof url !== 'string') {
                return reject(new Error("URL is string"));
            }

            if (![GET, POST, DELETE, PUT].includes(method.toUpperCase())) {
                return reject(new Error("Invalid HTTP method"));
            }

            $.ajax({
                type: method.toUpperCase(),
                url: url,
                contentType: "application/json",
                data: data ? JSON.stringify(data) : null,
                success: function (response) {
                    resolve(response);
                },
                error: function (xhr) {
                    const objectError = xhr.responseJSON || {message: "An unknown error occurred"};

                    $alterTop('error', objectError.message);
                    reject(objectError);
                }
            });
        });
    }

    return {
      createUrl, callApi
    }
})()

