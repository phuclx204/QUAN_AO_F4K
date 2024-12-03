import {$ajax, syncFormWithDataObject, buttonSpinner, validateForm, ref} from "/common/public.js";
const {getValidate, clearValidation} = validateForm;

(function () {
    "use strict";

    /** Biến toàn cục  **/
    const promotionId = document.querySelector('meta[name="promotion-id"]').getAttribute("content");

    const idFormPromotion = 'formCreatePromotion';
    const imageBlank = "https://firebasestorage.googleapis.com/v0/b/clothes-f4k.appspot.com/o/common%2Fdata_not_found.png?alt=media&token=36148ded-ba2c-4207-8525-2da16e7a8557";

    const $createDateRanger = $('#createDateRanger');

    const objDefault = {
        name: '',
        dayStart: '',
        dayEnd: '',
        status: 1,
        discountValue: ''
    }
    const model = Object.assign({}, objDefault);

    const ruleForm = {
        'createName': [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên bắt buộc",
                type: 'text'
            }
        ],
        'createValue': [
            {
                rule: (value) => value.trim() !== "",
                message: "Giá trị giảm bắt buộc",
                type: 'text',
                feedBackDiv: true
            }
        ]
    };
    const dayStart = ref(moment().startOf('hour').format("YYYY-MM-DD"));
    const dayEnd = ref(moment().startOf('hour').format("YYYY-MM-DD"));

    // init table
    const $table = $('#products-datatable').DataTable({
        info: false,
        searching: true,
        bLengthChange: false,
        pageLength: 5,
        // ordering: false,
        order: [[0, 'asc']],
        'columnDefs': [
            {
                'targets': 0,
                'checkboxes': {
                    'selectRow': true
                },
                orderable: true,
                orderData: 0,
                render: (data, type, row) => row.selected ? 1 : 0
            },
            {
                targets: '_all', // Tất cả các cột còn lại
                orderable: false // Tắt tính năng sắp xếp
            }
        ],
        'select': {
            'style': 'multi'
        },
        ajax: {
            url: "/admin/products/product-detail/get-list",
            type: 'GET',
            data: function (data) {
                return {
                    page: 1,
                    size: 1000,
                    status: 1,
                    search: ""
                }
            },
            dataFilter: (data) => {
                const json = jQuery.parseJSON( data );
                json.recordsTotal = json.totalElements;
                json.recordsFiltered = json.totalElements;
                json.data = json.content;
                return JSON.stringify( json );
            },
            error: function (xhr, error, thrown) {
                console.log("Error: ", error);
            }
        },
        columns: [
            {
                data: null,
                render: DataTable.render.select()
            },
            {
                data: 'product.name', title: 'Tên sản phẩm',
                render: (data, type, row, meta) => {
                    const fileImg = row.product.image?.fileUrl ? row.product.image?.fileUrl : imageBlank;
                    $('[data-bs-toggle="tooltip"]').tooltip();
                    return `<div class="d-flex align-items-center">
                            <img src="${fileImg}" alt="contact-img" title="contact-img" class="rounded me-3" height="48" />
                            <span class="d-inline-block" tabindex="0" data-bs-toggle="tooltip" data-bs-title="${data}" style="max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                ${data}
                            </span>
                            </div>`
                }
            },
            {
                data: 'color', title: 'Màu sắc',
                render: (data, type, row, meta) => {
                    return `<div class="d-flex">${data.name} - <span class="ms-1" style="width: 20px; height: 20px; background-color: ${data.hex}"></span></div>`;
                }
            },
            {data: 'size.name', title: 'Kích thước'},
            {
                data: 'status',
                title: 'Trạng thái',
                render: function (data, type, row) {
                    if (data === 1) {
                        return '<span class="badge bg-success">Đang kinh doanh</span>';
                    } else {
                        return '<span class="badge bg-danger">Ngừng kinh doanh</span>';
                    }
                }
            }
        ],
    });

    /** Function scope **/
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

    const getSelectValue = () => {
        const selectedData = $table.rows({ selected: true }).data().toArray();
        return selectedData.map(el => (el.id))
    }

    const setSelectValue = (listData = []) => {
        $table.rows(function(idx, data, node) {
            return listData.includes(data.id);
        }).select();

        $table.order([0, 'asc']).draw();
    }

    const formatDate = (dateString) => {
        return moment(dateString, "DD/MM/YYYY").format("YYYY-MM-DD");
    };
    /** Event scope **/
    $('#filterSearch').on('keyup', function () {
        $table.search(this.value).draw();
    });

    $("#createValue").on("input", function (e) {
        let cleanedInput = $(this).val().replace(/[^0-9]/g, '').replace(/^0+/, '');

        let numValue = parseInt(cleanedInput, 10);
        if (numValue > 100) {
            cleanedInput = 100;
        } else if (numValue < 0) {
            cleanedInput = 0;
        }

        $(this).val(cleanedInput);
    });

    $("#btnRefresh").on("click", function () {
        $table.search("").draw();
        $("#filterSearch").val("");
    });

    $("#action-save").on("click", async function (e) {
        e.preventDefault();

        const isValidate = await getValidate(idFormPromotion, ruleForm);
        if (!isValidate) return;

        model.productIds = getSelectValue();
        model.dayStart = formatDate(dayStart.value);
        model.dayEnd = formatDate(dayEnd.value);
        model.id = promotionId;
        openLoading()
        buttonSpinner.show()
        try {
            await $ajax.put("/admin/promotion", model);

            await closeLoading()
            buttonSpinner.hidden()
            showAlert("Thông báo", "Cập nhật thành công", "success", "/admin/promotion")
        } catch (e) {
            console.log(e)
        } finally {
            await closeLoading()
            buttonSpinner.hidden()
        }
    })

    $('#action-close').on("click", function (e) {
        window.location.href = "/admin/promotion";
    })

    $(document).on("submit", ".formCreatePromotion", async function (e) {
        e.preventDefault();
    })

    // get promotion detail
    const getData = async () => {
        const res = await $ajax.get("/admin/promotion/detail", {id: promotionId})

        objDefault.dayEnd = res.dayEnd
        objDefault.dayStart = res.dayStart
        objDefault.name = res.name
        objDefault.discountValue = res.discountValue

        syncFormWithDataObject({
            selectorParent: idFormPromotion,
            dataObject: model,
            initialValues: objDefault,
        });
        return res;
    }

    $(document).ready(async function () {
        const data = await getData();
        console.log(data)
        setTimeout(() => setSelectValue(data.products.map(el => el.productDetail.id)), 350)
        dayStart.value =  moment(data.dayStart).format("DD/MM/YYYY")
        dayEnd.value =  moment(data.dayEnd).format("DD/MM/YYYY")

        $createDateRanger.daterangepicker({
            startDate: dayStart.value,
            endDate: dayEnd.value,
            locale: {
                format: 'DD/MM/YYYY'
            }
        });

        $createDateRanger.on('apply.daterangepicker', function(ev, picker) {
            console.log(picker.startDate.format('YYYY-MM-DD'))
            console.log(picker.endDate.format('YYYY-MM-DD'))
            dayStart.value =  picker.startDate.format('DD/MM/YYYY');
            dayEnd.value = picker.endDate.format('DD/MM/YYYY');
        });
    })
})();





