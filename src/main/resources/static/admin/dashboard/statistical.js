
// Biểu đồ cột
$(document).ready(function () {
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
            drawChart(orderStatistics);
        },
        error: function (err) {
            console.error("Error:", err);
        }
    });

    const $createDateRanger = $('#createDateRanger');

    $createDateRanger.daterangepicker({
        startDate: moment().subtract(7, 'days'),
        endDate: moment(),
        locale: {
            format: 'DD/MM/YYYY'
        }
    });

    $createDateRanger.on('apply.daterangepicker', function (ev, picker) {
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
//  thống kê số đơn đang chờ xác nhận
$(document).ready(function () {
    $.ajax({
        url: '/admin/statistical/order-wait-confirm',
        method: 'GET',
        success: function (response) {
            $('#orderWaitConfirm').text(response);
        },
        error: function (err) {
            console.error('Error fetching revenue:', err);
            $('#orderWaitConfirm').text('0');
        }
    });
});

// biểu đồ tròn thống kê doanh thu bán tại quầy và online
$(document).ready(function () {
    $.ajax({
        url: '/admin/statistical/total-by-order-type',
        method: 'GET',
        success: function (response) {
            const revenueData = {
                offline: response.offline || 0,
                online: response.online || 0
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
            name: key === 'offline' ? 'Tại cửa hàng' : 'Trực tuyến',
            y: parseFloat(data[key]),
            color: key === 'offline' ? '#36A2EB' : '#FF6384'
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

// biếu đồ thống kê sản phẩm bán chạy nhất
$(document).ready(function() {
    const $datepicker = $('#datepicker');
    const $container = $('#container');
    const $orderType = $('#orderType');

    $datepicker.daterangepicker({
        startDate: moment().subtract(7, 'days'),
        endDate: moment(),
        locale: {
            format: 'DD/MM/YYYY'
        }
    });

    $orderType.val('');

    $datepicker.on('change', function() {
        loadBestSellingProducts();
    });

    $orderType.on('change', function() {
        loadBestSellingProducts();
    });

    function loadBestSellingProducts() {
        const startDate = $datepicker.data('daterangepicker').startDate.format('YYYY-MM-DD');
        const endDate = $datepicker.data('daterangepicker').endDate.format('YYYY-MM-DD');
        const orderType = $orderType.val() || '';
        const orderTypeParam = orderType && orderType !== '' ? `&orderType=${orderType}` : '';

        $.ajax({
            url: `/admin/statistical/quantity-best-sale?startDate=${startDate}&endDate=${endDate}${orderTypeParam}`,
            method: 'GET',
            success: function(response) {
                drawChart(response);
            },
            error: function(err) {
                console.error('Error fetching best selling products:', err);
                $container.html('<p class="text-danger text-center">Không có dữ liệu.</p>');
            }
        });
    }

    // Vẽ biểu đồ cột
    function drawChart(data) {
        if (data && data.length > 0) {
            const labels = Object.keys(data); // Mảng các ngày
            const productNames = labels.map(item => data[item].productName);
            const quantities = labels.map(item => data[item].quantity);

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

    loadBestSellingProducts();
});

function formatPrice(amount) {
    return parseFloat(amount).toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",") + ' ₫';
}