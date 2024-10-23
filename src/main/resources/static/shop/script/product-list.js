const { removeNullProperties, getPagination, formatNumberByDot } = getCommon();

const GET_LIST_API = URL + "/collections/list-product";

const queryShowProduct = $('#product-show-list');
const queryPagination = $('#pagination-product')
const querySearchForm = $('#filter-search-product')

let objPagination = {
    currentPage: null,
    totalPages: null,
    pageSize: null
};

const defaultObjSearch = {
    pageSize: 6,
    page: 0,
    name: null,
    brand: null,
    priceFrom: null,
    priceTo: null,
    category: null,
    size: null,
    color: null,
    orderBy: 'asc'
};
const objSearch = {...defaultObjSearch}

const getListProduct = async (objSearch = {}) => {
    try {
        const result = await $ajax.callApi($ajax.createUrl(GET_LIST_API, objSearch), GET);
        queryShowProduct.empty();

        objPagination.totalPages = result.totalPages;
        objPagination.pageSize = result.size;
        objPagination.currentPage = result.number;

        // mapping data
        const data = result.content.map(el => {
            return {
                id: el.id,
                idParent: el.idParent,
                name: el.product.name,
                color: el.color.name,
                size: el.size.name,
                price: el.price,
                discount: null,
                listImg: el.listImage
            }
        })

        data.forEach(el => addDomListProduct(el));
        addDomPagination(result);
    } catch (e) {
        console.log(e);
    }
};
getListProduct().catch(e => console.log(e))

const resetSearchObject = () => {
    Object.assign(objSearch, defaultObjSearch);
};

const addDomListProduct = (item) => {

    const hrefProduct = '/shop/product/' + item.idParent + `?color=${item.color}`;
    const hrefQuickAdd = '/shop/cart/add/' + item.id;

    const productCardHTML = `
        <div class="col-12 col-sm-6 col-md-4">
            <div class="card position-relative h-100 card-listing hover-trigger">
                <div class="card-header h-100">
                    ${getDomPicture(item.listImg[0])}
                    ${getDomPicture(item.listImg[1], false)}
                    <div class="card-actions">
                        <a class="small text-uppercase tracking-wide fw-bolder text-center d-block btn-add-cart" href="${hrefQuickAdd}" data-id="${item.id}">Quick Add</a>
                        <div class="d-flex justify-content-center align-items-center flex-wrap mt-3"></div>
                    </div>
                </div>
                <div class="card-body px-0 text-center">
                    <a class="mb-0 mx-2 mx-md-4 fs-p link-cover text-decoration-none d-block text-center link-name-product" href="${hrefProduct}">${item.name}</a>
                    <p class="fw-bolder m-0 mt-2">${item.price} VNĐ</p>
                </div>
            </div>
        </div>
    `;
    queryShowProduct.append(productCardHTML);
};
const getDomPicture = (srcImg, isFirst = true) => {
    const path = srcImg?.path || imageBlank;
    return `
        <picture class="${isFirst ? "position-relative overflow-hidden d-block bg-light h-100" : "position-absolute z-index-20 start-0 top-0 hover-show bg-light h-100"}">
            <img class="${isFirst ? "w-100 img-fluid position-relative z-index-10 custom-img-product h-100" : "w-100 img-fluid custom-img-product h-100"}"
                 alt=""
                 style="object-fit: cover"
                 src="${path}">
        </picture>
    `;
};
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
    if (!pages.length) return

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
const getParamPagination = (currentPage) => {
    return {
        page: currentPage,
        pageSize: objPagination.pageSize
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

    await getListProduct(getParamPagination(page));
})
// btn pagination


// Lưu trữ các phần tử DOM
const queryMinValue = $('.filter-min-price');
const queryMaxValue = $('.filter-max-price');
const queryInputSearch = $('.filter-search-input');

const queryCbColor = () => $('.cb-color:checked');
const queryCbSize = () => $('.cb-sizes:checked');
const queryCbCategory = () => $('.cb-category:checked');
const queryCbBrand = () => $('.cb-brand:checked');

// Sự kiện submit cho form tìm kiếm
querySearchForm.on("submit", async function (e) {
    e.preventDefault();
    objSearch.name = queryInputSearch.val();
    await getListProduct(removeNullProperties({...objSearch, ...objPagination}));
});

// Hàm để clear tất cả các filter
const clearAllFilters = async () => {
    queryMinValue.val(null);
    queryMaxValue.val(null);
    queryInputSearch.val(null);
    queryCbBrand().prop('checked', false);
    queryCbColor().prop('checked', false);
    queryCbSize().prop('checked', false);
    queryCbCategory().prop('checked', false);

    resetSearchObject();
    objPagination.currentPage = 0;

    await getListProduct(removeNullProperties({...objSearch, ...objPagination}));
};

// clear all
$(document).on("click", '.btn-clear-all', async function () {
    await clearAllFilters();
});

// filter
const updateSearchAndFetch = async (key, selector) => {
    objSearch[key] = $(selector + ':checked').map(function () {
        return $(this).data("id");
    }).get();
    await getListProduct(removeNullProperties({...objSearch, ...objPagination}));
};
$(document).on('click', '.cb-brand', async function () {
    await updateSearchAndFetch('brand', '.cb-brand');
});
$(document).on('click', '.cb-category', async function () {
    await updateSearchAndFetch('category', '.cb-category');
});
$(document).on('click', '.cb-sizes', async function () {
    await updateSearchAndFetch('size', '.cb-sizes');
});
$(document).on('click', '.cb-color', async function () {
    await updateSearchAndFetch('color', '.cb-color');
});

const formatInputCash = ($query) => {
    let cleanedInput = $query.val().replace(/[^0-9]/g, '').replace(/^0+/, '');

    let numValue = parseInt(cleanedInput, 10);
    if (numValue > 10000000) {
        cleanedInput = 10000000;
    }

    $query.val(formatNumberByDot(cleanedInput.toString()));
}
$(document).on('input', '.filter-min-price, .filter-max-price', function () {
    formatInputCash($(this));
});
const handlePriceBlur = async (type, minSelector, maxSelector) => {
    let min = parseInt($(minSelector).val().replace(/[^0-9]/g, ''), 10);
    let max = parseInt($(maxSelector).val().replace(/[^0-9]/g, ''), 10);

    if (type === 'min' && max && min >= max) {
        min = max;
    } else if (type === 'max' && min && max <= min) {
        max = min;
    }

    objSearch[type === 'min' ? 'priceForm' : 'priceTo'] = parseInt(type === 'min' ? min : max) || null;
    $(type === 'min' ? minSelector : maxSelector).val(formatNumberByDot((type === 'min' ? min : max).toString()));

    await getListProduct(removeNullProperties({...objSearch, ...objPagination}));
};
$(document).on('blur', '.filter-min-price', function () {
    handlePriceBlur('min', '.filter-min-price', '.filter-max-price');
});
$(document).on('blur', '.filter-max-price', function () {
    handlePriceBlur('max', '.filter-min-price', '.filter-max-price');
});
$('.filter-list-price').on('click', async function (e) {
    e.preventDefault();

    const val = $(this).data("id")
    if (val === 'desc' || val === 'asc') {
        objSearch.orderBy = val;

        await getListProduct(removeNullProperties({...objSearch, ...objPagination,}))
    }
})