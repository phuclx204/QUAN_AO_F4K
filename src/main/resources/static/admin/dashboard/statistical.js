
// Biểu đồ cột
$(document).ready(function () {
    // Tính toán ngày hiện tại và ngày 7 ngày trước
    const today = moment();
    const last7Days = moment().subtract(7, 'days');

    $.ajax({
        url: "/admin/statistical/data",
        method: "GET",
        data: {
            startDate: last7Days.format('YYYY-MM-DD'),
            endDate: today.format('YYYY-MM-DD')
        },
        success: function (orderStatistics) {
            drawChart(orderStatistics); // Vẽ biểu đồ
        },
        error: function (err) {
            console.error("Error:", err);
        }
    });

    const $createDateRanger = $('#createDateRanger');

    // Khởi tạo date range picker
    $createDateRanger.daterangepicker({
        startDate: moment().subtract(7, 'days'),
        endDate: moment(),
        locale: {
            format: 'DD/MM/YYYY'
        }
    });

    // Khi áp dụng date range
    $createDateRanger.on('apply.daterangepicker', function (ev, picker) {
        // Cập nhật giá trị cho input
        $createDateRanger.val(picker.startDate.format('DD/MM/YYYY') + ' - ' + picker.endDate.format('DD/MM/YYYY'));
    });

    // Hàm vẽ biểu đồ cột
    function drawChart(orderStatistics) {
        const labels = Object.keys(orderStatistics); // Mảng các ngày
        const revenueData = labels.map(date => orderStatistics[date].totalRevenue); // Mảng doanh thu
        const ordersData = labels.map(date => orderStatistics[date].numberOfOrders); // Mảng số đơn hàng

        Highcharts.chart('chart', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Doanh thu và số đơn hàng theo ngày'
            },
            xAxis: {
                categories: labels,
                title: {
                    text: 'Ngày'
                }
            },
            yAxis: [{
                min: 0,
                title: {
                    text: 'Doanh thu (VND)',
                    align: 'high'
                },
                labels: {
                    overflow: 'justify'
                }
            }, {
                title: {
                    text: 'Số đơn hàng'
                },
                opposite: true
            }],
            tooltip: {
                shared: true,
                valueSuffix: ''
            },
            series: [{
                name: 'Doanh thu',
                data: revenueData,
                color: 'rgba(75, 192, 192, 0.7)',
                yAxis: 0
            }, {
                name: 'Số đơn hàng',
                data: ordersData,
                color: 'rgba(153, 102, 255, 0.7)',
                yAxis: 1
            }]
        });
    }

    // Lọc dữ liệu khi nhấn nút "Lọc"
    $("#filterButton").click(function () {
        const dateRange = $createDateRanger.val().split(' - ');
        const startDate = moment(dateRange[0], 'DD/MM/YYYY').format('YYYY-MM-DD');
        const endDate = moment(dateRange[1], 'DD/MM/YYYY').format('YYYY-MM-DD');

        if (!startDate || !endDate) {
            alert("Vui lòng chọn khoảng thời gian.");
            return;
        }

        // Gửi yêu cầu AJAX để lọc dữ liệu với phạm vi ngày mới
        $.ajax({
            url: "/admin/statistical/data",
            method: "GET",
            data: {
                startDate: startDate,
                endDate: endDate
            },
            success: function (orderStatistics) {
                drawChart(orderStatistics);
            },
            error: function (err) {
                console.error("Error:", err);
            }
        });
    });
    // Lấy dữ liệu mặc định cho biểu đồ khi trang được tải
    $.ajax({
        url: "/admin/statistical/data",
        method: "GET",
        data: { startDate: last7Days.format('YYYY-MM-DD'), endDate: today.format('YYYY-MM-DD') },
        success: function (orderStatistics) {
            drawChart(orderStatistics);
        },
        error: function (err) {
            console.error("Error:", err);
        }
    });
});

// thống kê tổng doanh thu
$(document).ready(function () {
    $.ajax({
        url: '/admin/statistical/total-revenue',
        method: 'GET',
        success: function (response) {
            $('#revenueAmount').text(formatPrice(response.toFixed(0)));
        },
        error: function (err) {
            console.error('Error fetching revenue:', err);
            $('#revenueAmount').text('0₫');
        }
    });
});

//  thống kê tổng số đơn hàng
$(document).ready(function () {
    $.ajax({
        url: '/admin/statistical/total-quantity-order',
        method: 'GET',
        success: function (response) {
            $('#totalQuantityOrder').text(response);
        },
        error: function (err) {
            console.error('Error fetching revenue:', err);
            $('#totalQuantityOrder').text('0');
        }
    });
});

// biểu đồ tròn thống kê doanh thu bán tại quầy và online
$(document).ready(function () {
    // Gửi yêu cầu lấy dữ liệu tổng doanh thu theo loại đơn hàng
    $.ajax({
        url: '/admin/statistical/total-by-order-type',
        method: 'GET',
        success: function (response) {
            const revenueData = {
                OFFLINE: response.OFFLINE || 0,
                ONLINE: response.ONLINE || 0
            };
            drawPieChart(revenueData);
        },
        error: function (err) {
            console.error('Error fetching total revenue by order type:', err);
        }
    });

    // Hàm hiển thị biểu đồ tròn
    function drawPieChart(data) {
        const chartData = Object.keys(data).map(key => ({
            name: key === 'OFFLINE' ? 'Tại cửa hàng' : 'Trực tuyến',
            y: parseFloat(data[key]),
            color: key === 'OFFLINE' ? '#36A2EB' : '#FF6384'
        }));

        Highcharts.chart('average-chart', {
            chart: {
                type: 'pie'
            },
            title: {
                text: 'Thống kê doanh thu theo loại đơn hàng'
            },
            series: [{
                name: 'Doanh thu',
                data: chartData,
                showInLegend: true
            }],
            tooltip: {
                pointFormat: '<b>{point.y} ₫</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                    }
                }
            }
        });
    }

});

$(document).ready(function() {
    const $timeFilter = $('#time-filter');
    const $datepickerContainer = $('#datepicker-container');
    const $weekSelector = $('#week-selector');
    const $yearSelector = $('#year-selector');
    const $datepicker = $('#datepicker');
    const $week = $('#week');
    const $year = $('#year');
    const $orderTypeSelector = $('#order-type-selector');
    const $container = $('#container');

    // Hiển thị lựa chọn bộ lọc tương ứng với loại thời gian
    $timeFilter.on('change', function() {
        const filterType = $(this).val();
        showTimeFilterOptions(filterType);
    });

    // Khởi tạo datepicker và đặt ngày mặc định là ngày hôm nay
    $datepicker.datepicker({
        format: 'yyyy/mm/dd',
        autoclose: true
    }).datepicker('setDate', new Date()); // Thiết lập ngày mặc định là ngày hôm nay

    // Sự kiện thay đổi cho các bộ lọc
    $datepicker.on('change', function() {
        loadBestSellingProducts('day'); // Khi thay đổi ngày, tải lại dữ liệu
    });

    $week.on('change', function() {
        loadBestSellingProducts('week'); // Khi thay đổi tuần, tải lại dữ liệu
    });

    $year.on('change', function() {
        loadBestSellingProducts('year'); // Khi thay đổi năm, tải lại dữ liệu
    });

    $orderTypeSelector.on('change', function() {
        loadBestSellingProducts('orderType'); // Khi thay đổi loại đơn hàng, tải lại dữ liệu
    });

    // Kiểm tra và in ra ngày đã được chọn từ datepicker
    console.log("Ngày đã chọn: ", $datepicker.val());

    // Hiển thị các bộ lọc tùy theo loại thời gian được chọn
    function showTimeFilterOptions(filterType) {
        $datepickerContainer.hide();
        $weekSelector.hide();
        $yearSelector.hide();
        $orderTypeSelector.hide();

        switch (filterType) {
            case 'day':
                $datepickerContainer.show();
                break;
            case 'week':
                $weekSelector.show();
                break;
            case 'year':
                $yearSelector.show();
                break;
            case 'orderType':
                $orderTypeSelector.show();
                break;
        }
        loadBestSellingProducts(filterType);
    }

    // Gọi API và vẽ biểu đồ
    function loadBestSellingProducts(filterType) {
        let filterValue = '';
        let orderType = $('#orderType').val(); // Lấy loại đơn hàng từ lựa chọn

        switch (filterType) {
            case 'day':
                filterValue = $datepicker.val(); // Ngày cụ thể
                break;
            case 'week':
                filterValue = $week.val(); // Tuần hiện tại hoặc tuần trước
                break;
            case 'year':
                filterValue = $year.val(); // Năm cụ thể
                break;
            case 'orderType':
                orderType = $('#orderType').val() || ''; // Loại đơn hàng đã chọn
                break;
        }

        console.log("Filter Value:", filterValue);
        console.log("Filter type:", filterType);
        console.log("order type:", orderType);

        // Gửi yêu cầu AJAX đến API
        $.ajax({
            url: '/admin/statistical/quantity-best-sale',
            method: 'GET',
            data: {
                filterType: filterType,
                filterValue: filterValue,
                orderType: orderType
            },
            success: function(response) {
                drawChart(response);
            },
            error: function(err) {
                console.error('Error fetching best selling products:', err);
            }
        });
    }

    // Vẽ biểu đồ cột
    function drawChart(data) {
        if (data && data.length > 0) {
            const labels = Object.keys(data); // Mảng các ngày
            const productNames = labels.map(item => data[item].productName); // Tên sản phẩm kết hợp
            const quantities = labels.map(item => data[item].quantity); // Số lượng bán

            Highcharts.chart($container[0], {
                chart: {
                    type: 'bar'
                },
                title: {
                    text: 'Sản phẩm bán chạy nhất'
                },
                xAxis: {
                    categories: productNames,
                    title: {
                        text: 'Sản phẩm'
                    }
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Số lượng bán'
                    }
                },
                tooltip: {
                    shared: true,
                    valueSuffix: ''
                },
                series: [{
                    name: 'Số lượng bán',
                    data: quantities,
                    color: 'rgba(75, 192, 192, 0.7)'
                }]
            });
        } else {
            $container.html('<p class="text-danger text-center">Không có dữ liệu.</p>');
        }
    }

    // Mặc định chọn 'Ngày' khi load trang
    $timeFilter.val('day').trigger('change');
});

function formatPrice(amount) {
    // Chuyển đổi số thành chuỗi và định dạng với dấu phẩy
    return parseFloat(amount).toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",") + ' ₫';
}