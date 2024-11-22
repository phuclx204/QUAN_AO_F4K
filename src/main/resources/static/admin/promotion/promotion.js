import {$ajax, buttonSpinner, getCommon, ref, validateForm} from "/common/public.js";

const {getFormValuesByName} = getCommon();
const {getValidate, clearValidation} = validateForm;

$(document).ready(async function () {
    "use strict";

    const promotionId = ref(null);
    const listProducts = JSON.parse(localStorage.getItem("listData"));

    const URL = '/api/v1/admin/promotion';
    const STATUS_ON = 1;
    const STATUS_OFF = 0;
    const TYPE_CASH = 1;
    const TYPE_PERCENT = 2;

    // init table
    const table = $('#products-table').DataTable({
        info: false,
        serverSide: true,
        searching: false,
        bLengthChange: false,
        pageLength: 10,
        ajax: {
            url: URL + "/list",
            type: 'GET',
            data: function (data) {
                const filterSearch = document.querySelector('input[data-f4k-filter="search"]').value;
                const filterStatus = document.querySelector('select[data-f4k-filter="selectStatus"]').value;
                const filterEffectiveDate = document.querySelector('select[data-f4k-filter="selectEffectiveDate"]').value;
                return {
                    page: Math.floor(data.start / data.length) + 1,
                    size: data.length,
                    sort: 'id,desc',
                    search: filterSearch,
                    status: filterStatus,
                    effectiveDate: filterEffectiveDate
                }
            }
        },
        columns: [
            {
                data: null,
                title: "STT",
                render: (data, type, full, meta) => {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {data: 'name', title: 'Tên kỳ giảm giá'},
            {data: 'dayStart', title: 'Thời gian bắt đầu'},
            {data: 'dayEnd', title: 'Thời gian kết thúc'},
            {
                data: 'status',
                title: 'Trạng thái',
                render: function (data, type, row) {
                    if (data === STATUS_ON) {
                        return '<span class="badge bg-success">Hoạt động</span>';
                    } else {
                        return '<span class="badge bg-danger">Vô hiệu hóa</span>';
                    }
                }
            },
            {
                data: null,
                title: 'Thao tác',
                render: function (data, type, row) {
                    return `<td class="table-action">
                             <a href="/api/v1/admin/products/product-detail/${row.id}" class="action-icon action-view" data-id="${row.id}"> <i class="mdi mdi-eye"></i></a>
                             <a href="javascript:void(0);" class="action-icon action-update" data-id="${row.id}"> <i class="mdi mdi-square-edit-outline"></i></a>
<!--                             <a href="javascript:void(0);" class="action-icon action-delete" data-id="${row.id}"> <i class="mdi mdi-delete"></i></a>-->
                             </td>`;
                }
            }
        ]
    });
    $(document).on("submit", "#formSearch", e => {
        e.preventDefault();
        table.search('').draw();
    })
    $(document).on("change", "#select-effectiveDate, #select-status", () => {
        table.search('').draw();
    })
    const reloadTable = () => {
        table.ajax.reload(null, false);
    }

    // modal
    const setLabelModal = (text) => {
        const label = $('#standard-modalCreateLabel');
        label.empty()
        label.text(text)
    }
    const closeModal = () => {
        $('#modal-create').modal('hide');
    }

    const setDateRange = (startDate, endDate) => {
        const dateRanger = $('#createDateRanger').data('daterangepicker');
        dateRanger.setStartDate(moment(startDate).format("DD/MM/YYYY"));
        dateRanger.setEndDate(moment(endDate).format("DD/MM/YYYY"));
    }
    const getSelectedDates = () => {
        const dateRanger = $('#createDateRanger').data('daterangepicker');
        const startDate = dateRanger.startDate.format('YYYY-MM-DD');
        const endDate = dateRanger.endDate.format('YYYY-MM-DD');
        return {start: startDate, end: endDate};
    }
    const openModal = (mode, $this) => {
        $('#createDateRanger').daterangepicker({
            // timePicker: true,
            // startDate: moment().startOf('hour'),
            // endDate: moment().startOf('date'),
            locale: {
                format: 'DD/MM/YYYY'
            }
        });

        if (mode === "create") {
            setLabelModal("Thêm mới")
            console.log(moment().startOf('day').format('DD/MM/YYYY'))
            setDateRange(moment().startOf('day'), moment().startOf('day'));
        } else if (mode === "update") {
            setLabelModal("Cập nhật")
            const id = $this.data("id");
            promotionId.value = id;
            $ajax.get("/api/v1/admin/promotion/detail", {id: id}).then(data => {
                $('#createName').val(data.name)
                if (data.status === 1) {
                    $('#createStatus1').prop("checked", true)
                } else {
                    $('#createStatus2').prop("checked", true)
                }
                setDateRange(data.dayStart, data.dayEnd)
                fillDynamicInput(data.products)
            })
        }

        $('#modal-create').modal('show');
    }

    const modalSelector = document.getElementById('modal-create')
    modalSelector.addEventListener('hide.bs.modal', event => {
        $('#createName').val("");
        $('#createStatus1').prop("checked", true)
        resetDynamicInput()
        promotionId.value = null;
        clearValidation("formCreate")
    })

    // event button
    $(document).on("click", ".action-create", function (e) {
        e.preventDefault();
        openModal("create")
    })
    $(document).on("click", ".action-update", function (e) {
        e.preventDefault();
        openModal("update", $(this))
    })
    // $(document).on("click", ".action-delete", async function (e) {
    //     e.preventDefault();
    //     const id = $(this).data("id");
    //     try {
    //         openLoading();
    //         await $ajax.remove("/admin/products/" + id).then(_ => {
    //             $alterTop("success", "Xóa thành công")
    //             reloadTable();
    //         });
    //     } finally {
    //         closeLoading()
    //     }
    // })

    const validationRules = {
        'createName': [
            {
                rule: (value) => value.trim() !== "",
                message: "Tên sản phẩm bắt buộc",
                type: 'text'
            }
        ]
    };
    $(document).on("submit", "#formCreate", async e => {
        e.preventDefault();
        const isValid = await getValidate('formCreate', validationRules);
        if (!isValid) return

        const confirm = await $confirm("warning",
            "Những sản phẩm trùng nhau sẽ lấy theo giá trị đầu tiên, bạn có chắc chắn muốn lưu không?",
            "Nhắc nhở")
        if (!confirm.isConfirmed) return
        const object = {
            name: $("#createName").val(),
            dayStart: getSelectedDates().start,
            dayEnd: getSelectedDates().end,
            status: $('#createStatus1').is(":checked") ? STATUS_ON : STATUS_OFF,
            products: getDataDynamic()
        }
        try {
            buttonSpinner.show();
            if (promotionId.value) {
                object.id = promotionId.value;
                await $ajax.put(URL, object).then(() => {
                    $alter("success", "Note", "Cập nhật thành công")
                    reloadTable();
                    closeModal();
                });
            } else {
                await $ajax.post(URL, object).then(() => {
                    $alter("success", "Note", "Thêm mới thành công")
                    reloadTable();
                    closeModal();
                });
            }
        } catch (e) {
            console.log(e)
        } finally {
            buttonSpinner.hidden()
        }
    })


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
            let product = $(this).find('select[name="products[]"]').val();
            let discount = $(this).find('input[name="discount[]"]').val();
            let discountType = $(this).find('select[name="discountType[]"]').val();

            if (product && discount && discountType) {
                data.push({
                    product: product,
                    productId: product,
                    discountValue: discount,
                    type: discountType
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
                              <option value="${TYPE_CASH}" selected>VNĐ</option>
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
                         <select class="form-select" name="discountType[]">
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
            $alterTop("error", "Vui lòng nhập đầy đủ thông tin trước khi thêm dòng mới.")
            // alert("Vui lòng nhập đầy đủ thông tin trước khi thêm dòng mới.");
            return;
        }
        let newRow = getDynamicInputDefault();
        $("#dynamicTable tbody").append(newRow);
    });
});





