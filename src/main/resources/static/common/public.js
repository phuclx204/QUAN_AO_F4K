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
        const $button = $('.has-spinner button');
        $button.prop('disabled', true);
        $button.prepend('<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>');
    }

    const hidden = () => {
        const $button = $('.has-spinner button');
        $button.prop('disabled', false);
        $button.find('.spinner-border').remove();
    }

    return {show, hidden};
})();
const buttonSpinner2 = (() => {
    const show = ($button) => {
        $button.prop('disabled', true);
        $button.prepend('<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>');
    }

    const hidden = ($button) => {
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

    const transformData = (mapBackendToFrontend, data) => {
        const transformedData = {};
        for (const key in data) {
            const newKey = mapBackendToFrontend[key] || key;
            transformedData[newKey] = data[key];
        }
        return transformedData;
    }

    return {
        getFormValuesByName,
        removeNullProperties,
        formatNumberByDot,
        getPagination,
        convert2Vnd,
        transformData
    }
}
const validateForm = (() => {
    const addEventElement = (elementForm, field, elementInput, options) => {
        if (options.type === 'select2' && $(elementInput).hasClass('select2')) {
            $(elementInput).on('change', () => {
                validate(elementForm, field, options);
            });
        } else {
            elementInput.addEventListener('input', () => {
                validate(elementForm, field, options);
            });
        }
    }
    const getFeedback = (elementForm, options) => {
        let invalidFeedback = null;
        if (options?.feedBackDiv) {
            invalidFeedback = elementForm.closest('.valid-div').querySelector('.invalid-feedback');
        } else {
            invalidFeedback = elementForm.parentElement.querySelector('.invalid-feedback');
        }
        return invalidFeedback;
    }
    const getInputVal = (elementForm, options) => {
        let inputValue = elementForm.value;
        if (options?.type === "select2") {
            inputValue = $(elementForm).val()
        }
        return inputValue;
    }

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
                const validationRule = validationRules[field][0];

                addEventElement(form, field, input, validationRule);
            }

            for (const el of validationRules[field]) {
                const validTmp = validate(form, field, el);
                if (!validTmp) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    };
    const validate = (form, selectorId, options) => {
        let isValid = true;
        const input = form.querySelector(`#${selectorId}`);
        const rules = options.rule;
        let invalidFeedback = null;

        try {
            invalidFeedback = getFeedback(input, options)
        } catch (_e) {
            console.log(`Thiếu thẻ <div class="invalid-feedback"></div> dưới phần tử có id = ${selectorId}`);
        }

        if (invalidFeedback == null) return true

        if (rules) {
            let inputValue = getInputVal(input, options)

            if (!rules(inputValue)) {
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

            if ($(input).hasClass('select2')) {
                $(input).val(null).trigger('change');
            }

            const invalidFeedback = input.parentElement.querySelector('.invalid-feedback');
            if (invalidFeedback) {
                invalidFeedback.style.display = "none";
            }
        });
    }
    return {getValidate, clearValidation}
})()

// for ajax
const POST = 'POST';
const GET = 'GET';
const PUT = 'PUT';
const DELETE = 'DELETE';
const PATCH = 'PATCH';
const $ajax = (() => {
    const createUrl = (url, params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        if (queryString) {
            url += `?${queryString}`;
        }

        return url;
    }

    function clearNullAndEmptyArrayFields(obj) {
        for (const key in obj) {
            if (obj[key] === null || (Array.isArray(obj[key]) && obj[key].length === 0)) {
                delete obj[key];
            }
        }
        return obj;
    }

    /**
     *
     * @Param url is api name
     * @Param method is rest method | post | put | delete | get
     * @Param data is data send to controller, in body
     * @param params
     * @param showErrMess
     */
    function callApi(url, method = 'POST', data = null, params = null, showErrMess = true) {
        return new Promise((resolve, reject) => {
            if (!url || typeof url !== 'string') {
                return reject(new Error("URL is string"));
            }
            if (![GET, POST, DELETE, PUT, PATCH].includes(method.toUpperCase())) {
                return reject(new Error("Invalid HTTP method"));
            }

            let urlPath = url;
            if (params) {
                urlPath = createUrl(url, clearNullAndEmptyArrayFields(params));
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
                    console.log(xhr.responseText)
                    const objectError = xhr.responseJSON || {message: xhr.responseText} || {message: "Không thể thao tác"};
                    if (showErrMess) {
                        if (Array.isArray(objectError)) {
                            $alterTop('error', objectError[0].defaultMessage);
                            reject(objectError);
                        } else {
                            $alterTop('error', objectError.message);
                            reject(objectError);
                        }
                    }
                }
            });
        });
    }

    // function buildFormData(formData, data, parentKey = '') {
    //     if (data && typeof data === 'object' && !(data instanceof File)) {
    //         Object.keys(data).forEach(key => {
    //             const value = data[key];
    //             const fullKey = parentKey ? `${parentKey}[${key}]` : key;
    //
    //             if (Array.isArray(value)) {
    //                 value.forEach((v, index) => {
    //                     buildFormData(formData, v, `${fullKey}[${index}]`);
    //                 });
    //             } else {
    //                 buildFormData(formData, value, fullKey);
    //             }
    //         });
    //     } else {
    //         formData.append(parentKey, data);
    //     }
    // }

    function buildFormData(formData, data, parentKey = '') {
        if (data && typeof data === 'object' && !(data instanceof File)) {
            Object.keys(data).forEach(key => {
                const value = data[key];
                const fullKey = parentKey ? `${parentKey}.${key}` : key;

                if (Array.isArray(value)) {
                    value.forEach((v, index) => {
                        buildFormData(formData, v, `${fullKey}[${index}]`);
                    });
                } else {
                    buildFormData(formData, value, fullKey);
                }
            });
        } else {
            formData.append(parentKey, data);
        }
    }


    function callWithMultipartFile(url, method = 'POST', data = null, params = null) {
        return new Promise((resolve, reject) => {
            if (!url || typeof url !== 'string') {
                return reject(new Error("URL is string"));
            }
            if (![GET, POST, DELETE, PUT, PATCH].includes(method.toUpperCase())) {
                return reject(new Error("Invalid HTTP method"));
            }

            let urlPath = url;
            if (params) {
                urlPath += createUrl(url, params);
            }

            const dataForm = new FormData();
            if (data) {
                buildFormData(dataForm, data);
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

    const post = (url, data = null, params = null, showErrMess = true) => {
        return callApi(url, "POST", data, params, showErrMess)
    }
    const put = (url, data = null, params = null, showErrMess = true) => {
        return callApi(url, "PUT", data, params, showErrMess)
    }
    const get = (url, params = null, data = null, showErrMess = true) => {
        return callApi(url, "GET", data, params, showErrMess)
    }
    const remove = (url, params = null, data = null, showErrMess = true) => {
        return callApi(url, "DELETE", data, params, showErrMess)
    }
    const patch = (url, data = null, params = null, showErrMess = true) => {
        return callApi(url, "PATCH", data, params, showErrMess)
    }
    return {
        createUrl, callApi, callWithMultipartFile, get, post, put, remove, patch
    }
})()

// dùng khi  có một selector bọc ngoài thông qua id
export const twoWayBinding = ({selectorParent, dataObject, initialValues}) => {
    const $selectorParent = document.getElementById(selectorParent);
    console.log($selectorParent, ' - $selectorParent')
    $selectorParent.addEventListener('input', (e) => {
        const target = e.target;
        const name = target.name;

        if (!name) return;

        if (target.type === 'checkbox') {
            dataObject[name] = target.checked;
        } else if (target.type === 'radio' && target.checked) {
            dataObject[name] = target.value;
        } else if (target.type === 'file') {
            dataObject[name] = target.files;
        } else {
            dataObject[name] = target.value;
        }
    });

    Object.keys(initialValues).forEach((key) => {
        const element = $selectorParent.querySelector(`[name="${key}"]`);
        if (element) {
            if (element.type === 'checkbox') {
                element.checked = initialValues[key];
            } else if (element.type === 'radio' && element.value === initialValues[key]) {
                element.checked = true;
            } else {
                element.value = initialValues[key];
            }
            dataObject[key] = initialValues[key];
        }
    });
};

export const syncFormWithDataObject = ({selectorParent, dataObject, initialValues}) => {
    const $selectorParent = document.getElementById(selectorParent);

    $selectorParent.addEventListener('input', (e) => {
        const target = e.target;
        const name = target.name;

        if (!name) return;

        if (target.type === 'checkbox') {
            dataObject[name] = target.checked;
        } else if (target.type === 'radio' && target.checked) {
            dataObject[name] = target.value;
        } else if (target.type === 'file') {
            dataObject[name] = target.files;
        } else {
            dataObject[name] = target.value;
        }
    });

    Object.keys(initialValues).forEach((key) => {
        const element = $selectorParent.querySelector(`[name="${key}"]`);
        if (element) {
            if (element.type === 'checkbox') {
                element.checked = initialValues[key];
            } else if (element.type === 'radio' && element.value === initialValues[key]) {
                element.checked = true;
            } else {
                element.value = initialValues[key];
            }
            dataObject[key] = initialValues[key];
        }
    });

    const updateFormUI = () => {
        Object.keys(dataObject).forEach((key) => {
            const element = $selectorParent.querySelector(`[name="${key}"]`);
            if (element) {
                if (element.type === 'checkbox') {
                    element.checked = dataObject[key];
                } else if (element.type === 'radio' && element.value === dataObject[key]) {
                    element.checked = true;
                } else {
                    element.value = dataObject[key];
                }
            }
        });
    };

    updateFormUI();
};

export {buttonSpinner, $ajax, ref, getCommon, validateForm, buttonSpinner2};