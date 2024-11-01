const ref = (initialValue) => {
    const obj = {
        value: initialValue
    };
    return obj;
}
// disable và loading những button có trong thẻ div calss = modal-footer has-spinner, dùng với bostrap 4 hoặc 5 đều được
// dùng với jquery
const buttonSpinner = (() => {
    const show = () => {
        const $button = $('.modal-footer.has-spinner button');
        $button.prop('disabled', true);
        $button.prepend('<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>');
    }

    const hidden = () => {
        const $button = $('.modal-footer.has-spinner button');
        $button.prop('disabled', false);
        $button.find('.spinner-border').remove();
    }

    return {show, hidden};
})();
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
        const selects = form.querySelectorAll('select[name]');
        const texts = form.querySelectorAll('textarea[name]');

        const values = {};

        inputs.forEach(input => {
            values[input.name] = input.value;
        });

        selects.forEach(el => {
            values[el.name] = el.value
        })

        texts.forEach(el => {
            values[el.name] = el.value
        })
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

    const convert2Vnd = (value = "") => {
        let price = parseFloat(value);
        price = price.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});
        price = price.replaceAll("₫", "VNĐ")
        return price;
    }
    return {
        getFormValuesByName,
        removeNullProperties,
        formatNumberByDot,
        getPagination,
        convert2Vnd
    }
}
const validateForm = (() => {
    const getValidate = (formId, validationRules = {}) => {
        const form = document.getElementById(formId);
        if (form === null) {
            console.log("ID form is wrong!");
            return true;
        }
        let isValid = true;

        for (const field in validationRules) {
            const input = form.querySelector(`#${field}`);

            if (input) {
                input.addEventListener('input', () => {
                    validate(form, field, '', validationRules[field][0]);
                });
            }

            for (const el of validationRules[field]) {
                const validTmp = validate(form, field, '', el);
                if (!validTmp) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    };

    const validate = (form, selectorId, type, options) => {
        let isValid = true;
        const input = form.querySelector(`#${selectorId}`);
        const rules = options.rule;
        let invalidFeedback = null;

        try {
            invalidFeedback = input.parentElement.querySelector('.invalid-feedback');
        } catch (_e) {
            console.log(`Thiếu thẻ <div class="invalid-feedback"></div> dưới phần tử có id = ${selectorId}`);
        }

        if (invalidFeedback == null) {
            console.log('=== ERRRRRROR ===');
            return true;
        }

        if (rules) {
            if (!rules(input.value)) {
                invalidFeedback.textContent = options.message;
                input.setCustomValidity("Invalid");
                invalidFeedback.style.display = "block";
                isValid = false;
            } else {
                invalidFeedback.style.display = "none";
                invalidFeedback.textContent = "";
                input.setCustomValidity("");
            }
        }
        return isValid;
    };

    const clearValidation = (formId) => {
        if (!formId.startsWith("#")) {
            formId = "#" + formId
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
    return { getValidate, clearValidation}
})()

// for ajax
const POST = 'POST';
const GET = 'GET';
const PUT = 'PUT';
const DELETE = 'DELETE';
const $ajax = (() => {
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
     * @param params
     */
    function callApi(url, method = 'POST', data = null, params = null) {
        return new Promise((resolve, reject) => {
            if (!url || typeof url !== 'string') {
                return reject(new Error("URL is string"));
            }
            if (![GET, POST, DELETE, PUT].includes(method.toUpperCase())) {
                return reject(new Error("Invalid HTTP method"));
            }

            let urlPath = url;
            if (params) {
                urlPath = createUrl(url, params);
            }
            $.ajax({
                type: method.toUpperCase(),
                url: urlPath,
                contentType: "application/json",
                data: data ? JSON.stringify(data) : null,
                success: function (response) {
                    resolve(response);
                },
                error: function (xhr) {
                    const objectError = xhr.responseJSON || {message: "An unknown error occurred"};
                    if (Array.isArray(objectError)) {
                        $alterTop('error', objectError[0].defaultMessage);
                        reject(objectError);
                    } else {
                        $alterTop('error', objectError.message);
                        reject(objectError);
                    }
                }
            });
        });
    }

    function callWithMultipartFile(url, method = 'POST', data = null, params = null) {
        return new Promise((resolve, reject) => {
            if (!url || typeof url !== 'string') {
                return reject(new Error("URL is string"));
            }
            if (![GET, POST, DELETE, PUT].includes(method.toUpperCase())) {
                return reject(new Error("Invalid HTTP method"));
            }

            let urlPath = url;
            if (params) {
                urlPath += createUrl(url, params);
            }

            const dataForm = new FormData();
            if (data) {
                for (const filed in data) {
                    if (!data[filed]) continue;
                    if (Array.isArray(data[filed])) {
                        data[filed].forEach(el => {
                            dataForm.append(filed, el);
                        });
                    } else {
                        dataForm.append(filed, data[filed])
                    }
                }
            }

            $.ajax({
                type: method.toUpperCase(),
                url: urlPath,
                enctype: 'multipart/form-data',
                processData: false,
                contentType: false,
                cache: false,
                data: data ? dataForm : null,
                success: function (response) {
                    resolve(response);
                },
                error: function (xhr) {
                    const objectError = xhr.responseJSON || {message: xhr.responseText || "An unknown error occurred"};
                    if (Array.isArray(objectError)) {
                        $alterTop('error', objectError[0].defaultMessage);
                        reject(objectError);
                    } else {
                        $alterTop('error', objectError.message);
                        reject(objectError);
                    }
                }
            });
        });
    }

    const post = (url, params = null, data = null) => {
        return callApi(url, "POST", data)
    }
    const put = (url, params = null, data = null) => {
        return callApi(url, "PUT", data)
    }
    const get = (url, params = null, data = null) => {
        return callApi(url, "GET", data, params)
    }
    const remove = (url, params = null, data = null) => {
        return callApi(url, "DELETE", data, params)
    }
    return {
        createUrl, callApi, callWithMultipartFile, get, post, put, remove
    }
})()

export { buttonSpinner, $ajax, ref, getCommon, validateForm };