const URL = "/shop";
const GET_LIST_API = URL + "/list-product";
const queryShowProduct = $('#product-show-list');
const queryPagination = $('#pagination-product')
const queryBtnNext = $('#btn-pagination-next')
const queryBtnPre = $('#btn-pagination-pre')

let objPagination = {
    currentPage: null,
    totalPages: null,
    size: null
};

const getListProduct = async (objSearch = {}) => {
    try {
        const result = await callApi(createUrl(GET_LIST_API, objSearch), GET);
        queryShowProduct.empty();
        objPagination.totalPages = result.totalPages;
        objPagination.size = result.size;
        objPagination.currentPage = result.number;
        handleData(result.content);
        addDomPagination(result);
    } catch (e) {
        console.log(e)
    }
}

const handleData = (lstData = []) => {
    for (let item of lstData) {
        const objCustom = {
            id: item.id,
            name: item.name,
            listImg: [],
            listSize: [],
            price: null,
            discount: null,
        }

        const listDetail = item.listDetail;
        if (listDetail.length) {
            const productDetail = listDetail[listDetail.length - 1];
            objCustom.listImg = productDetail.listImage;
            objCustom.price = productDetail.price;
        }

        addDom(objCustom);
    }
}

const addDom = (item) => {
    const srcImg1 = item.listImg[0];
    const srcImg2 = item.listImg[1];
    const productCardHTML = `
        <div class="col-12 col-sm-6 col-md-4">
                        <!-- Card Product-->
                        <div class="card position-relative h-100 card-listing hover-trigger">
                            <div class="card-header h-100">
                                ${getDomPicture(srcImg1)}
                                ${getDomPicture(srcImg2, false)}
                                <div class="card-actions">
                                    <a class="small text-uppercase tracking-wide fw-bolder text-center d-block btn-add-cart" href="#" data-id="${item.id}">Quick Add</a>
                                    <div class="d-flex justify-content-center align-items-center flex-wrap mt-3"></div>
                                </div>
                            </div>
                            <div class="card-body px-0 text-center">
                                <a class="mb-0 mx-2 mx-md-4 fs-p link-cover text-decoration-none d-block text-center"
                                   href="/shop/category#">${item.name}</a>
                                <p class="fw-bolder m-0 mt-2">${item.price} VNĐ</p>
                            </div>
                        </div>
                        <!--/ Card Product-->
                    </div>
    `
    queryShowProduct.append(productCardHTML);
}

const getDomPicture = (srcImg, isFirst = true) => {
    if (!isFirst && !srcImg) return ''

    let path
    if (!srcImg) {
        path = imageBlank;
    } else {
        path = srcImg ? srcImg.path : imageBlank;
    }

    return `
        <picture class="${isFirst ? "position-relative overflow-hidden d-block bg-light h-100" : "position-absolute z-index-20 start-0 top-0 hover-show bg-light h-100"}">
            <img class="${isFirst ? "w-100 img-fluid position-relative z-index-10 custom-img-product h-100" : "w-100 img-fluid custom-img-product h-100"}"
                 alt=""
                 title=""
                 style="object-fit: cover"
                 src="${path}">
        </picture>
    `;
}

const addDomPagination = (object = {totalPages: null, number: null}) => {
    queryPagination.empty();
    const currentPage = object.number;
    const totalPages = object.totalPages;

    const prevHtml = `
        <ul class="pagination">
            <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link ${currentPage === 0 ? '' : 'c-black'}" href="#" id="btn-pagination-pre">
                    <i class="ri-arrow-left-line align-bottom"></i> Prev
                </a>
            </li>
        </ul>
    `;

    let pagesHtml = '<ul class="pagination">';
    const pages = getPagination(currentPage + 1, totalPages);
    pages.forEach((page, index) => {
        let pages = page - 1;
        pagesHtml += `
            <li class="page-item ${currentPage === pages ? 'active mx-1' : 'mx-1'}">
                <a class="page-link btn-page" href="#" data-id="${pages}">${page}</a>
            </li>
        `;
    });
    pagesHtml += '</ul>';

    const nextHtml = `
        <ul class="pagination">
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link ${currentPage === totalPages - 1 ? '' : 'c-black'}" href="#" id="btn-pagination-next">
                    Next <i class="ri-arrow-right-line align-bottom"></i>
                </a>
            </li>
        </ul>
    `;

    const paginationHtml = prevHtml + pagesHtml + nextHtml;
    queryPagination.append(paginationHtml);
}

(function () {
    'use strict';

    getListProduct();
})();

const getParamPagination = (currentPage) => {
    return {
        page: currentPage,
        size: objPagination.size
    }
};

// btn pagination
$(document).on('click', '#btn-pagination-pre', async function (e) {
    e.preventDefault();
    const obj = getParamPagination(objPagination.currentPage - 1);
    await getListProduct(obj);
})

$(document).on('click', '#btn-pagination-next', async function (e) {
    e.preventDefault();
    const obj = getParamPagination(objPagination.currentPage + 1);
    await getListProduct(obj);
})

$(document).on('click', '.btn-page', async function (e) {
    e.preventDefault();
    const page = $(this).data("id");
    console.log(page, ' == > page')
    await getListProduct(getParamPagination(page));
})
// btn pagination

// add cart
$(document).on('click', '.btn-add-cart', function (e) {
    e.preventDefault();
    const rowId = $(this).data("id");
    console.log(rowId, ' - rowId');
});
//