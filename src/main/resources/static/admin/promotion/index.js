import {$ajax, buttonSpinner, getCommon, ref, validateForm} from "/common/public.js";

const {getFormValuesByName} = getCommon();
const {getValidate, clearValidation} = validateForm;


(function () {
    'use strict'

    const {validateForm} = getValidate();
    const {convert2Vnd} = getCommon();

    const URL = '/admin/promotion';
    const URL_SEARCH = URL + '/get-list-promotion';
    const URL_DETAIL = URL + "/promotion-detail"

    const queryTable = $('#jqGrid');
    const querySearch = $('#searchForm');
    const queryModal = $('#modalPromotion');

    const STATUS_ON = 1;
    const STATUS_OFF = 0;

    const TYPE_CASH = 1;
    const TYPE_PERCENT = 2;

    // init element
    const createDatePicker = document.getElementById("createDate");
    if (createDatePicker) {
        const options = {
            locale: 'en-US',
            startDate: new Date(),
            endDate: new Date()
        }

        new coreui.DateRangePicker(createDatePicker, options)
    }

    // getProductList
    const listProducts = JSON.parse(localStorage.getItem("listProducts"));

    // init grid table
    $(function () {
        queryTable.jqGrid({
            ...girdOptionDefault,
            url: URL_SEARCH,
            pager: "#jqGridPager",
            autowidth: true,
            loadonce: false,
            colModel: [
                {label: 'ID', name: 'id', index: 'id', width: 50, key: true, hidden: true, sortable: false},
                {
                    label: 'STT',
                    name: '_stt',
                    index: 'stt',
                    width: 50,
                    resizable: false,
                    align: 'center',
                    sortable: false
                },
                {label: 'Tên chương trình', name: 'name', index: 'name', sortable: false, width: 250},
                {label: 'Ngày bắt đầu', name: 'dayStart', index: 'dayStart', sortable: false},
                {label: 'Ngày kết thúc', name: 'dayEnd', index: 'dayEnd', sortable: false},
                {
                    label: 'Trạng thái', name: 'status', index: 'status', sortable: false,
                    formatter: (c, o, r) => {
                        if (`${c}` === '1') return 'Kích hoạt'
                        else return 'Chưa kích hoạt'
                    }
                },
                {
                    name: 'actions',
                    label: 'Thao tác',
                    width: 100,
                    align: 'center',
                    sortable: false,
                    resizable: false,
                    formatter: (c, o, r) => addButtonIcon(r, false)
                }
            ],
            beforeRequest: function () {
                const postData = $(this).jqGrid('getGridParam', 'postData');
                postData.page = postData.page - 1
                $(this).jqGrid('setGridParam', {postData: postData});
            },
            // thêm số thứ tự cho bản ghi trả về
            beforeProcessing: (data) => addStt(data),
            gridComplete: () => {
                initCss(queryTable, 'table-md', false)
                new ResizeObserverManager($(".card"), queryTable);

                $('.btn-update').on('click', function (e) {
                    showUpdate($(this).data("id"));
                })
                $('.btn-detail').on('click', function (e) {
                    showDetail($(this).data("id"));
                })
            }
        });

    });

    // watching
    querySearch.on('submit', function (e) {
        e.preventDefault();
        const searchValue = $('#searchInput').val();
        queryTable.jqGrid('setGridParam', {
            url: URL_SEARCH + '?name=' + encodeURIComponent(searchValue),
        }).trigger('reloadGrid');
    });
    queryModal.on('hidden.coreui.modal', function (event) {
        $('#form-update-insert').removeClass('was-validated');
        $('#createId').val("")
        $('#createName').val("")
        $('#createStatus1').prop("checked", true)
        coreui.DateRangePicker.getInstance(document.getElementById("createDate")).reset()
        resetDynamicInput();
    })

    // method
    const reload = () => {
        const page = queryTable.jqGrid('getGridParam', 'page');
        queryTable.jqGrid('setGridParam', {
            page: page
        }).trigger("reloadGrid");
    }
    const convertDate = (date) => {
        if (!date) return null;
        return dayjs(date).format('YYYY-MM-DD')
    }

    const validationDemo = {
        "create-name": [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên chương trình bắt buộc",
                lib: 'boostrap',
                type: 'text'
            }
        ],
        "create-products": [
            {
                rule: (value) => value.length,
                message: "Sản phẩm bắt buộc",
                lib: 'coreUi',
                type: 'select'
            }
        ]
    }
    $("#form-create").on("submit", async function (e) {
        e.preventDefault()

        $confirm("warning",
            "Những sản phẩm trùng nhau sẽ lấy thjeo giá trị đầu tiên, bạn có chắc chắn muốn lưu không?",
            "Nhắc nhở")
            .then(async rs => {
            if (rs.isConfirmed) {
                const id = $('#createId').val();

                const datePicker = coreui.DateRangePicker.getInstance(document.getElementById("createDate"))
                const name = $('#createName').val();
                const status = $('#createStatus1').is(":checked")
                const listProducts = getDataDynamic()

                const object = {
                    name: name,
                    dayStart: datePicker._startDate,
                    dayEnd: datePicker._endDate,
                    status: status ? STATUS_ON : STATUS_OFF,
                    products: listProducts
                }

                object.dayStart = convertDate(object.dayStart);
                object.dayEnd = convertDate(object.dayEnd);

                if (id) {
                    object.id = id;
                }

                try {
                    await $ajax.callApi(URL, object.id ? 'PUT' : 'POST', object);
                    $alter("success", object.id ? 'Cập nhật thành công' : 'Thêm mới thành công')
                    $('#modalPromotion').modal("hide")
                    reload();
                } catch (e) {
                    console.log(e)
                }
            }
        }).catch(err => {
            console.log(err)
        })
    })

    const showUpdate = async (data) => {
        try {
            const result = await $ajax.callApi(URL_DETAIL + "?id=" + data, "GET");

            $('#createId').val(result.id);
            $('#createName').val(result.name);
            if (`${result.status}` === '1') {
                $('#createStatus1').prop('checked', true);
            } else {
                $('#createStatus2').prop('checked', true);
            }
            const datePicker = coreui.DateRangePicker.getInstance(document.getElementById("createDate"))
            datePicker.update({
                startDate: new Date(result.dayStart),
                endDate: new Date(result.dayEnd)
            })
            fillDynamicInput(result.products)
            queryModal.modal('show')
        } catch (e) {
            console.log(e)
        }
    };

    const showDetail = async (data) => {
        try {
            const result = await $ajax.callApi(URL_DETAIL + "?id=" + data, "GET");
            $('#detailName').text(result.name);
            if (`${result.status}` === '1') {
                $('#detailStatus').text("Kích hoạt")
            } else {
                $('#detailStatus').text("Huỷ kích hoạt")
            }

            $('#detailDate').text(`${dayjs(result.dayStart).format('DD-MM-YYYY HH:mm:ss')} - ${dayjs(result.dayEnd).format('DD-MM-YYYY HH:mm:ss')}`)
            fillDynamicInputDetail(result.products)
            $('#modalPromotionDetail').modal('show')
        } catch (e) {
            console.log(e)
        }
    }

    // create dynamic input
    function allRowsFilled() {
        let allFilled = true;
        $("#dynamicTable tbody tr").each(function () {
            let product = $(this).find('select[name="products[]"]').val();
            let discount = $(this).find('input[name="discount[]"]').val();
            if (!product || !discount) {
                allFilled = false;
                return false;
            }
        });
        return allFilled;
    }
    const getDataDynamic = () => {
        let data = [];
        $("#dynamicTable tbody tr").each(function () {
            let productId = $(this).find('input[name="id[]"]').val();
            let product = $(this).find('select[name="products[]"]').val();
            let discount = $(this).find('input[name="discount[]"]').val();
            let discountType = $(this).find('select[name="discountType[]"]').val();

            if (product && discount && discountType) {
                data.push({
                    productId: productId,
                    product: product,
                    discount: discount,
                    discountType: discountType
                });
            }
        });
        return data;
    };
    const getDynamicInputDefault = () => {
        return `<tr>
                <input type="hidden" name="id[]" value="" />
                <td>${getHtmlSelectProduct()}</td>
                <td>
                    <div class="input-group">
                         <input type="text" name="discount[]" class="form-control" placeholder="Giá trị giảm" style="width: 65%">
                         <select class="form-select" name="discountType[]">
                              <option value="${TYPE_CASH}">VNĐ</option>
                              <option value="${TYPE_PERCENT}">%</option>
                         </select>
                    </div>
                </td>
                <td>
                    <button type="button" class="btn btn-danger remove-row">
                         <i class='bx bx-trash'></i>
                    </button>
                </td>
            </tr>`
    }
    const getHtmlSelectProduct = (value = null) => {
        const listItem = listProducts;
        const select = `<select class="form-select" name="products[]">`;
        let optionHtml = `<option value="" disabled selected>Chọn sản phẩm</option>`;
        listItem.forEach(el => {
            optionHtml += `<option value="${el.id}" ${value === el.id ? 'selected' : ''}>${el.name}</option>`
        })

        return (select + optionHtml + "</select>");
    }
    const fillDynamicInput = (items) => {
        let newRow = items.length ? `` : getDynamicInputDefault();
        items.forEach(el => {
            newRow += `<tr>
                <input type="hidden" name="id[]" value="${el.product?.id}" />
                <td>${getHtmlSelectProduct(el.product?.id)}</td>
                <td>
                    <div class="input-group">
                         <input type="text" name="discount[]" class="form-control" placeholder="Giá trị giảm" style="width: 65%" value="${el.discountValue}">
                         <select class="form-select" name="discountType[]" value="">
                              <option value="${TYPE_CASH}" ${el.type === TYPE_CASH ? 'selected' : ''}>VNĐ</option>
                              <option value="${TYPE_PERCENT}" ${el.type === TYPE_PERCENT ? 'selected' : ''}>%</option>
                         </select>
                    </div>
                </td>
                <td>
                    <button type="button" class="btn btn-danger remove-row">
                         <i class='bx bx-trash'></i>
                    </button>
                </td>
            </tr>`
        })

        $("#dynamicTable tbody").html(newRow)
    }
    const fillDynamicInputDetail = (items) => {
        let newRow = ``;
        items.forEach(el => {
            newRow += `<tr>
                <td><span>${el.product.name}</span></td>
                <td>
                    <span>${el.type === TYPE_CASH ? convert2Vnd(el.discountValue) : el.type === TYPE_PERCENT ? el.discountValue + " %" : ""}</span> 
                </td>
            </tr>`
        })

        $("#dynamicTableDetail tbody").html(newRow)
    }
    const resetDynamicInput = () => {
        $("#dynamicTable tbody").html(getDynamicInputDefault())
    };

    $(document).on("click", ".remove-row", function () {
        $(this).closest("tr").remove();
    });
    $("#addRow").click(function () {
        if (!allRowsFilled()) {
            alert("Vui lòng nhập đầy đủ thông tin trước khi thêm dòng mới.");
            return;
        }
        let newRow = getDynamicInputDefault();
        $("#dynamicTable tbody").append(newRow);
    });
})()

