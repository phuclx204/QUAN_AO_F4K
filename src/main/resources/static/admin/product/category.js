const URL_SEARCH = '/api/category';
const idTbl = '#jqGrid';
const queryTable = $('#jqGrid');
const querySearch = $('#searchForm');

$(function () {
    queryTable.jqGrid({
        ...girdOptionDefault,
        url: URL_SEARCH,
        pager: "#jqGridPager",
        autowidth: true,
        // multiselect: true,
        loadonce: false,
        colModel: [
            {label: 'ID', name: 'id', index: 'id', width: 50, key: true, hidden: true},
            {label: 'STT', name: '_stt', index: 'stt', width: 25, align: 'center'},
            {label: 'Tên', name: 'name', index: 'name'},
            {label: 'Mô tả', name: 'description', index: 'description'},
            {
                name: 'actions',
                label: 'Thao tác',
                width: 100,
                align: 'center',
                sortable: false,
                formatter: (c, o, r) => addButtonIcon(r)
            }
        ],
        // thêm số thứ tự cho bản ghi trả về
        beforeProcessing: (data) => addStt(data),
        // initCss from common.js
        gridComplete: () => {
            initCss(queryTable, 'table-sm')

            $(BTN.CREATE).click(function (e) {
                onCreate()
            });

            // onDelete();
            $(BTN.DELETE).click(function (e) {
                e.preventDefault();
                var selectedRow = queryTable.find(".jqgrow.active");
                console.log(selectedRow)
                var rowId = $(this).data("id");
                console.log(rowId)
                onDelete(rowId);
            });
        }
    });
});

// Lắng nghe sự kiện submit của form tìm kiếm
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

function reload() {
    const page = queryTable.jqGrid('getGridParam', 'page');
    queryTable.jqGrid('setGridParam', {
        page: page
    }).trigger("reloadGrid");
}

function onDelete(id) {
    $alterTop('success', 'Đã xóa thành công')
    // $confirm('warning', 'question', 'banana')
    //     .then(rs => {
    //         console.log(rs.isConfirmed)
    //     })

    // const ids = getSelectedRows();
    // console.log(ids)
    // if (ids == null) {
    //     return;
    // }
    // swal({
    //     title: "Cảnh báo",
    //     text: "Có chắc chắn muốn xóa danh mục không?",
    //     icon: "warning",
    //     buttons: true,
    //     dangerMode: true,
    // }).then((flag) => {
    //     swal("Xóa thành công", {
    //         icon: "success",
    //     });
    //     console.log(flag)
    //         // if (flag) {
    //         //     $.ajax({
    //         //         type: "POST",
    //         //         url: "/admin/categories/delete",
    //         //         contentType: "application/json",
    //         //         data: JSON.stringify(ids),
    //         //         success: function (r) {
    //         //             if (r.resultCode == 200) {
    //         //                 swal("删除成功", {
    //         //                     icon: "success",
    //         //                 });
    //         //                 $("#jqGrid").trigger("reloadGrid");
    //         //             } else {
    //         //                 swal(r.message, {
    //         //                     icon: "error",
    //         //                 });
    //         //             }
    //         //         }
    //         //     });
    //         // }
    //     }
    // );
}

function onCreate() {
    console.log('vao')
    var forms = document.getElementsByClassName('needs-validation');
    var validation = Array.prototype.filter.call(forms, function (form) {
        form.addEventListener('submit', function (event) {
            if (form.checkValidity() === false) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    console.log(validation, ' - validation')
    $.ajax({
        type: "POST",
        url: "/admin/category",
        contentType: "application/json",
        data: JSON.stringify(''),
        success: function (r) {
            console.log(r)
            // if (r.resultCode == 200) {
            //     swal("删除成功", {
            //         icon: "success",
            //     });
            //     $("#jqGrid").trigger("reloadGrid");
            // } else {
            //     swal(r.message, {
            //         icon: "error",
            //     });
            // }
        },
        error: function (xhr, status, error) {
            // Xử lý lỗi từ phía server
            const objectError = xhr.responseJSON

            $alterTop('error', objectError.message)
            // swal("Error: " + xhr.responseText, {
            //     icon: "error",
            // });
        }
    });
}

// (function() {
//     'use strict';
//     window.addEventListener('load', function() {
//         // Fetch all the forms we want to apply custom Bootstrap validation styles to
//         var forms = document.getElementsByClassName('needs-validation');
//         // Loop over them and prevent submission
//         var validation = Array.prototype.filter.call(forms, function(form) {
//             form.addEventListener('submit', function(event) {
//                 if (form.checkValidity() === false) {
//                     event.preventDefault();
//                     event.stopPropagation();
//                 }
//                 form.classList.add('was-validated');
//             }, false);
//         });
//     }, false);
// })();