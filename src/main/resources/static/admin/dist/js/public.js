const POST = 'POST';
const GET = 'GET';
const PUT = 'PUT';
const DELETE = 'DELETE';

const createUrl = (action, controller, params = {}) => {
    const baseUrl = window.location.origin;

    let url = `${baseUrl}/${controller}/${action}`;

    const queryString = new URLSearchParams(params).toString();
    if (queryString) {
        url += `?${queryString}`;
    }

    return url;
}

function validate(formClass) {
    return new Promise((resolve, reject) => {
        const forms = document.getElementsByClassName(formClass);
        const validation = Array.prototype.filter.call(forms, function (form) {
            form.addEventListener('submit', function (event) {
                event.preventDefault();
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
            return form.checkValidity();
        });
        if (validation.length) {
            resolve(true)
        } else {
            reject(false)
        }
    })
}

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
                const objectError = xhr.responseJSON || { message: "An unknown error occurred" };

                $alterTop('error', objectError.message);
                reject(objectError);
            }
        });
    });
}