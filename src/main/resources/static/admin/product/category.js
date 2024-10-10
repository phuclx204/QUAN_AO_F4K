const URL_SEARCH = '/api/category';
const URL = '/admin/category';
const idTbl = '#jqGrid';
const queryTable = $('#jqGrid');
const querySearch = $('#searchForm');
const queryModal = $('#staticBackdrop');

$(function () {
    queryTable.jqGrid({
        ...girdOptionDefault,
        url: URL_SEARCH,
        pager: "#jqGridPager",
        autowidth: true,
        // multiselect: true,
        loadonce: false,
        // shrinkToFit: false,
        // forceFit: true,
        colModel: [
            {label: 'ID', name: 'id', index: 'id', width: 50, key: true, hidden: true},
            {label: 'STT', name: '_stt', index: 'stt', width: 25, resizable: false, align: 'center'},
            {label: 'Tên', name: 'name', index: 'name'},
            {label: 'Mô tả', name: 'description', index: 'description'},
            {
                name: 'actions',
                label: 'Thao tác',
                width: 100,
                align: 'center',
                sortable: false,
                resizable: false,
                formatter: (c, o, r) => addButtonIcon(r)
            }
        ],
        // thêm số thứ tự cho bản ghi trả về
        beforeProcessing: (data) => addStt(data),
        gridComplete: () => {
            // initCss from common.js
            initCss(queryTable, 'table-sm', false)
            // resize
            new ResizeObserverManager($(".card"), queryTable);
            // vì trong cột phải khai báo tại đây
            // onDelete();
            $(BTN.DELETE).click(function (e) {
                e.preventDefault();
                const rowId = $(this).data("id");

                $confirm('warning', 'Bạn có chắc chắn muốn xóa?')
                    .then(async rs => {
                        if (rs.isConfirmed) {
                            try {
                                await callApi(URL + '?id=' + rowId, DELETE)
                                $alterTop('success', 'Xóa bản ghi thành công');
                                reload();
                            } catch (err) {
                                console.log(err)
                            }
                        }
                    }).catch(err => {
                    console.log(err)
                })
            });

            // onUpdate();
            $(BTN.UPDATE).click(function (e) {
                e.preventDefault();
                const rowId = $(this).data("id");

                openModalUpdate(rowId);
            });
        }
    });

});


const openModalUpdate = (id) => {
    // get row data
    const ret = queryTable.jqGrid('getRowData', id);

    //overriding in modal
    const form = $('.form-update-insert');
    form.removeClass('was-validated');
    form.find('#model_id').val(ret.id);
    form.find('#model_name').val(ret.name);
    form.find('#model_description').val(ret.description);

    // open modal
    queryModal.modal('show')
}

const closeModal = ($this) => {
    $this.modal('hide')
}

function reload() {
    const page = queryTable.jqGrid('getGridParam', 'page');
    queryTable.jqGrid('setGridParam', {
        page: page
    }).trigger("reloadGrid");
}

// on create
$(BTN.CREATE).click(() => {
    validate('needs-validation form-update-insert')
        .then(async () => {
            const form = $('.form-update-insert');
            const data = {};
            const id = form.find('#model_id').val();
            const method = id ? PUT : POST;
            if (id) {
                data.id = id;
            }
            data.name = form.find('#model_name').val();
            data.description = form.find('#model_description').val();
            try {
                await callApi(URL, method, data);
                $alterTop('success', id ? 'Cập nhật thành công' : 'Thêm mới thành công');
                closeModal(queryModal);
                reload();
            } catch (err) {
                console.log(err)
            }
        })
})

// btn search - Lắng nghe sự kiện submit của form tìm kiếm
querySearch.on('submit', function (e) {
    // Chặn submit
    e.preventDefault();
    const searchValue = $('#searchInput').val();
    console.log(searchValue, ' searchValue')
    // set url and reload
    queryTable.jqGrid('setGridParam', {
        url: URL_SEARCH + '?search=' + encodeURIComponent(searchValue),
    }).trigger('reloadGrid');
});

// refresh when modal close
queryModal.on('hidden.bs.modal', function (event) {
    const form = $('.form-update-insert');
    form.removeClass('was-validated');
    form.find('#model_id').val('');
    form.find('#model_name').val('');
    form.find('#model_description').val('');
})