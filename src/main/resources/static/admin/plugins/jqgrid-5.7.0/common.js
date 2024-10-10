const BTN = {
    UPDATE: '.btn-update',
    DELETE: '.btn-delete',
    CREATE: '.btn-create'
}

const girdOptionDefault = {
    datatype: "json",
    height: 300,
    rowNum: 10,
    rowList: [10, 20, 50],
    styleUI: 'Bootstrap',
    loadtext: 'Đang tải dữ liệu...',
    rownumWidth: 20,
    jsonReader: {
        root: "content",
        page: "page",
        total: "totalPages",
        records: "totalElements"
    },
    prmNames: {
        page: "page",
        rows: "size",
        order: "order",
    },
    loadonce: false,
}

function initCss($this, tableSize = 'table-md') {
    $this.closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
    $this.closest(".ui-jqgrid").find('.ui-jqgrid-hbox').css({
        background: '#e9ecef'
    });
    $this.closest('.ui-jqgrid').find('thead').addClass('thead-light');
    $this.closest('.ui-jqgrid').find('.ui-jqgrid-btable').addClass('table table-hover ' + tableSize).removeClass('table-bordered');
}

function addStt(data) {
    if (data.content.length) {
        data.content = data.content.map((el, index) => {
            return {
                ...el,
                _stt: index + 1
            }
        })
    }
}

function addButtonIcon(rowObject = {}, hasDelete = true) {
    let divStart = '<div class="d-flex">'
    let divEnd = '</div>'
    const btnUpdate = '<a class="btn-update btn btn-icon text-primary" data-id="' + rowObject.id + '"><i class="icon-sm menu-icon ti-pencil-alt"></i></a>'
    const btnDelete = '<a class="btn-delete btn btn-icon text-danger" data-id="' + rowObject.id + '"><i class="icon-sm menu-icon ti-trash"></i></a>'

    return divStart + btnUpdate + (hasDelete ? btnDelete : '') + divEnd
}