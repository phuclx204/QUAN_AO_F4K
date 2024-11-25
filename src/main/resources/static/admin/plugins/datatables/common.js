Object.assign(DataTable.defaults, {
    processing: true,
    ordering: false,
    // nhấn enter rồi mới search
    search: {
        return: true
    },
    fixedColumns: {
        end: 1
    },
    language: {
        paginate: {
            previous: "<i class='mdi mdi-chevron-left'>",
            next: "<i class='mdi mdi-chevron-right'>"
        },
        info: "Tổng _TOTAL_ bản ghi",
        lengthMenu:
            'Số lượng hiển thị' +
            '<select class=\'form-select form-select-sm ms-1 me-1\'>' +
            '<option value="5">5</option>' +
            '<option value="10">10</option>' +
            '<option value="20">20</option>' +
            '<option value="100">100</option></select>'
    },
    drawCallback: function () {
        $('[data-bs-toggle="tooltip"]').tooltip();
        $(".dataTables_paginate > .pagination").addClass("pagination-rounded"),
            $("#products-datatable_length label").addClass("form-label"),
            document
                .querySelector(".dataTables_wrapper .row")
                .querySelectorAll(".col-md-6")
                .forEach(function (e) {
                    e.classList.add("col-sm-6"),
                        e.classList.remove("col-sm-12"),
                        e.classList.remove("col-md-6");
                });
    },
    ajax: {
        dataFilter: (data) => {
            const json = jQuery.parseJSON( data );
            json.recordsTotal = json.totalElements;
            json.recordsFiltered = json.totalElements;
            json.data = json.content;
            return JSON.stringify( json );
        },
        data: function (data) {
            return {
                page: Math.floor(data.start / data.length) + 1,
                size: data.length,
                sort: 'id,desc',
                filter: "",
                search: data.search.value,
            }
        },
        error: function (xhr, error, thrown) {
            console.log("Error: ", error);
        }
    }
});