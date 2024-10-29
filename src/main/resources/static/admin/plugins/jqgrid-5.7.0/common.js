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

function initCss($this, tableSize = 'table-md', haveBorder = false) {
    // $this.closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
    //
    // $this.closest(".ui-jqgrid").find('.ui-jqgrid-hdiv').css({
    //     background: '#e9ecef!important'
    // });
    // $this.closest('.ui-jqgrid').find('thead').addClass('thead-light');
    const table = $this.closest('.ui-jqgrid').find('.ui-jqgrid-btable')
    table.addClass('table-hover ' + tableSize)
    if (!haveBorder) table.removeClass('table-bordered');
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

function addButtonIcon(rowObject = {}, hasDelete = true, haseDetail = true) {
    let divStart = '<div class="d-flex">'
    let divEnd = '</div>'
    const btnUpdate = '<button title="Chỉnh sửa" class="btn-update btn btn-icon text-primary" ' +
        'data-id="' + rowObject.id + '"><i class="icon-sm menu-icon ti-pencil-alt"></i></button>'
    const btnDelete = '<button title="Xóa" class="btn-remove btn btn-icon text-danger" ' +
        'data-id="' + rowObject.id + '"><i class="icon-sm menu-icon ti-trash"></i></button>'
    const btnDetail = '<button title="Xem chi tiết" class="btn-detail btn btn-icon text-success" ' +
        'data-id="' + rowObject.id + '"><i class="icon-3xl menu-icon bx bx-show"></i></button>';

    return divStart + btnUpdate + (hasDelete ? btnDelete : '') + (haseDetail ? btnDetail : '') + divEnd
}

class ResizeObserverManager {
    constructor($selector, $gird) {
        const updateGridWidth = () => {
            const newWidth = this.selector.width();
            this.gird.jqGrid('setGridWidth', newWidth - 55, true);
        }

        this.selector = $selector
        this.gird = $gird

        const resizeObserver = new ResizeObserver(function (entries) {
            for (let entry of entries) {
                if (entry.contentBoxSize) {
                    updateGridWidth(); // Khi thẻ card thay đổi kích thước, cập nhật jqGrid
                }
            }
        });

        // Bắt đầu theo dõi thẻ card
        if (this.selector) {
            resizeObserver.observe(document.querySelector('.card'));
        }

        updateGridWidth();
    }

    disconnect() {
        // Dừng theo dõi nếu cần
        resizeObserver.unobserve(this.selector);
    }
}