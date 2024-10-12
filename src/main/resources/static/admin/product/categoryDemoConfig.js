$(function () {
    $("#jqGrid").jqGrid({
        ...girdOptionDefault,
        url: '/api/category',
        pager: "#jqGridPager",
        autowidth: true,
        multiselect: true,
        loadonce: false,
        colModel: [
            {label: 'ID', name: 'id', index: 'id', width: 50, key: true, hidden: true},
            {
                name: 'stt', label: 'STT', width: 50, formatter: function (cellvalue, options, rowObject) {
                    // Lấy rowId từ options
                    // var rowId = options.rowId;
                    //
                    // console.log(options)
                    // console.log(rowId)
                    // // // Lấy vị trí của bản ghi trong trang hiện tại
                    // var rowIndex = $('#jqGrid').jqGrid('getInd', rowId);
                    //
                    // console.log(rowIndex)
                    // // Lấy trang hiện tại và số bản ghi mỗi trang
                    // var currentPage = $("#yourGridId").jqGrid('getGridParam', 'page');
                    // var rowPerPage = $("#yourGridId").jqGrid('getGridParam', 'rowNum');
                    //
                    // // Tính chỉ số tổng thể
                    // var globalIndex = (currentPage - 1) * rowPerPage + rowIndex;
                    //
                    // // In ra vị trí của bản ghi
                    // console.log("Vị trí của bản ghi là: " + globalIndex);
                    //
                    // // Bạn có thể trả về giá trị đã format cho cột này nếu cần
                    // return cellValue;
                }
            },
            {label: 'Tên', name: 'name', index: 'name', width: 240},
            {label: 'Mô tả', name: 'description', index: 'description', width: 120}
        ],
        onPaging: function (pgButton) {
            var totalPages = $("#jqGrid").jqGrid('getGridParam', 'lastpage'); // 'lastpage' chứa tổng số trang
            console.log("Tổng số trang hiện có: " + totalPages);
            var newPage = $(this).getGridParam('page');
            console.log(newPage)
            console.log(pgButton)
        },
        serializeGridData: function(postData) {
            console.log("===================> Tùy chỉnh tham số gửi đi");
            // Tùy chỉnh tham số gửi đi
            postData.customParam = 'yourCustomValue';
            return postData; // Trả lại tham số sau khi tùy chỉnh
            console.log("Tùy chỉnh tham số gửi đi ====================>");
        },
        // thêm số thứ tự cho bản ghi trả về
        loadComplete: (data) => addStt(data),
        // initCss from common.js
        gridComplete: () => initCss($('#jqGrid'))
        // Tùy chỉnh tham số trước khi gửi AJAX
        // beforeRequest: function() {
        //     console.log("===================> Trước khi gửi yêu cầu AJAX");
        //     // Tùy chỉnh tham số trước khi gửi AJAX
        //     var postData = $(this).jqGrid('getGridParam', 'postData');
        //     console.log(postData)
        //     // Ví dụ: thêm một tham số tìm kiếm trước khi gửi yêu cầu
        //     postData.customParam = 'yourCustomValue';
        //     $(this).jqGrid('setGridParam', { postData: postData });
        //
        //     console.log("Trước khi gửi yêu cầu AJAX ====================>");
        // },
        // Tùy chỉnh trước khi gửi
        // ajaxGridOptions: {
        //     contentType: 'application/json',
        //     beforeSend: function(jqXHR) {
        //         // Tùy chỉnh trước khi gửi
        //         jqXHR.setRequestHeader("Authorization", "Bearer yourToken");
        //     }
        //
        // },
    });
});

/**
 * jqGrid重新加载
 */
// function reload() {
//     var page = $("#jqGrid").jqGrid('getGridParam', 'page');
//     $("#jqGrid").jqGrid('setGridParam', {
//         page: page
//     }).trigger("reloadGrid");
// }
//
// function categoryAdd() {
//     reset();
//     $('.modal-title').html('分类添加');
//     $('#categoryModal').modal('show');
// }

//绑定modal上的保存按钮
// $('#saveButton').click(function () {
//     var categoryName = $("#categoryName").val();
//     if (!validCN_ENString2_18(categoryName)) {
//         $('#edit-error-msg').css("display", "block");
//         $('#edit-error-msg').html("请输入符合规范的分类名称！");
//     } else {
//         var params = $("#categoryForm").serialize();
//         var url = '/admin/categories/save';
//         var id = getSelectedRowWithoutAlert();
//         if (id != null) {
//             url = '/admin/categories/update';
//         }
//         $.ajax({
//             type: 'POST',//方法类型
//             url: url,
//             data: params,
//             success: function (result) {
//                 if (result.resultCode == 200) {
//                     $('#categoryModal').modal('hide');
//                     swal("保存成功", {
//                         icon: "success",
//                     });
//                     reload();
//                 }
//                 else {
//                     $('#categoryModal').modal('hide');
//                     swal(result.message, {
//                         icon: "error",
//                     });
//                 }
//                 ;
//             },
//             error: function () {
//                 swal("操作失败", {
//                     icon: "error",
//                 });
//             }
//         });
//     }
// });
//
// function categoryEdit() {
//     reset();
//     var id = getSelectedRow();
//     if (id == null) {
//         return;
//     }
//     $('.modal-title').html('分类编辑');
//     $('#categoryModal').modal('show');
//     $("#categoryId").val(id);
// }
//
// function deleteCagegory() {
//     var ids = getSelectedRows();
//     if (ids == null) {
//         return;
//     }
//     swal({
//         title: "确认弹框",
//         text: "确认要删除数据吗?",
//         icon: "warning",
//         buttons: true,
//         dangerMode: true,
//     }).then((flag) => {
//             if (flag) {
//                 $.ajax({
//                     type: "POST",
//                     url: "/admin/categories/delete",
//                     contentType: "application/json",
//                     data: JSON.stringify(ids),
//                     success: function (r) {
//                         if (r.resultCode == 200) {
//                             swal("删除成功", {
//                                 icon: "success",
//                             });
//                             $("#jqGrid").trigger("reloadGrid");
//                         } else {
//                             swal(r.message, {
//                                 icon: "error",
//                             });
//                         }
//                     }
//                 });
//             }
//         }
//     );
// }


// function reset() {
//     $("#categoryName").val('');
//     $("#categoryIcon option:first").prop("selected", 'selected');
// }

