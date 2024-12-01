import {$ajax, syncFormWithDataObject, buttonSpinner, validateForm, ref} from "/common/public.js";
const {getValidate, clearValidation} = validateForm;

(function () {
    "use strict";

    // Biến toàn cục
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
    const dateRanger = [moment().startOf('hour'), moment().startOf('hour')]
    const dayStart = ref(dateRanger[0].format("YYYY-MM-DD"));
    const dayEnd = ref(dateRanger[1].format("YYYY-MM-DD"));

    // setUp
    // init table
    const $table = $('#products-datatable').DataTable({
        info: false,
        searching: true,
        bLengthChange: false,
        pageLength: 5,
        ordering: false,
        'columnDefs': [
            {
                'targets': 0,
                'checkboxes': {
                    'selectRow': true
                }
            }
        ],
        'select': {
            'style': 'multi'
        },
        ajax: {
            url: "/admin/products/search-list",
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
            {data: 'name', title: 'Tên sản phẩm',
                render: (data, type, row, meta) => {
                    const fileImg = row.image?.fileUrl ? row.image?.fileUrl : imageBlank;
                    $('[data-bs-toggle="tooltip"]').tooltip();
                    return `<div class="d-flex align-items-center">
                            <img src="${fileImg}" alt="contact-img" title="contact-img" class="rounded me-3" height="48" />
                            <span class="d-inline-block" tabindex="0" data-bs-toggle="tooltip" data-bs-title="${data}" style="max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                ${data}
                            </span>
                            </div>`
                }
            },
            {data: 'category.name', title: 'Danh mục'},
            {data: 'brand.name', title: 'Thương hiệu'},
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

    // function scope
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
    // event
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

        // const selectedData = $table.rows({ selected: true }).data().toArray();
        // console.log(selectedData);
        // // $table.rows([0, 1]).select();
        // $table.rows(function(idx, data, node) {
        //     return data.status === 1; // Select rows where status is 1 (active)
        // }).select();
    });


    $("#action-save").on("click", async function (e) {
        e.preventDefault();

        const isValidate = await getValidate(idFormPromotion, ruleForm);
        if (!isValidate) return;

        model.productIds = getSelectValue();
        model.dayStart = dayStart.value;
        model.dayEnd = dayEnd.value;

        openLoading()
        buttonSpinner.show()
        try {
            await $ajax.post("/admin/promotion", model);

            await closeLoading()
            buttonSpinner.hidden()
            showAlert("Thông báo", "Thêm thành công", "success", "/admin/promotion")
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

    syncFormWithDataObject({
        selectorParent: idFormPromotion,
        dataObject: model,
        initialValues: objDefault
    })
    $(document).ready(async function () {
        $createDateRanger.daterangepicker({
            startDate: dateRanger[0],
            endDate: dateRanger[1],
            locale: {
                format: 'DD/MM/YYYY'
            }
        });

        $createDateRanger.on('apply.daterangepicker', function(ev, picker) {
            dayStart.value =  picker.startDate.format('YYYY-MM-DD');
            dayEnd.value = picker.endDate.format('YYYY-MM-DD');
        });
    })
})();





