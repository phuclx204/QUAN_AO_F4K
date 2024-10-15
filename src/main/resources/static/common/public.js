const POST = 'POST';
const GET = 'GET';
const PUT = 'PUT';
const DELETE = 'DELETE';

const imageBlank = "https://firebasestorage.googleapis.com/v0/b/clothes-f4k.appspot.com/o/common%2Fdata_not_found.png?alt=media&token=36148ded-ba2c-4207-8525-2da16e7a8557";

const createUrl = (url, params = {}) => {
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

const getPagination = (currentPage, totalPage) => {
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