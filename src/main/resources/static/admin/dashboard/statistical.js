$(document).ready(function () {
    // Tính toán ngày hiện tại và ngày 7 ngày trước
    const today = new Date();
    const last7Days = new Date();
    last7Days.setDate(today.getDate() - 7);

    // Định dạng ngày theo định dạng yyyy-mm-dd
    const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    const startDate = formatDate(last7Days);
    const endDate = formatDate(today);

    $("#startDate").val(startDate);
    $("#endDate").val(endDate);

    // Hàm vẽ biểu đồ cột
    function drawChart(orderStatistics) {
        const labels = Object.keys(orderStatistics); // Mảng các ngày
        const revenueData = labels.map(date => orderStatistics[date].totalRevenue); // Mảng doanh thu
        const ordersData = labels.map(date => orderStatistics[date].numberOfOrders); // Mảng số đơn hàng

        Highcharts.chart('chart', {
            chart: {
                type: 'bar'
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

    $.ajax({
        url: "/admin/statistical/data",
        method: "GET",
        data: {
            startDate: startDate,
            endDate: endDate
        },
        success: function (orderStatistics) {
            // console.log(orderStatistics);
            drawChart(orderStatistics);
        },
        error: function (err) {
            console.error("Error:", err);
        }
    });

    // Lọc dữ liệu khi nhấn nút "Lọc"
    $("#filterButton").click(function () {
        const startDate = $("#startDate").val();
        const endDate = $("#endDate").val();

        if (!startDate || !endDate) {
            alert("Vui lòng chọn cả ngày bắt đầu và kết thúc.");
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
                // console.log(orderStatistics);
                drawChart(orderStatistics);
            },
            error: function (err) {
                console.error("Error:", err);
            }
        });
    });
});
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
    $(document).ready(function () {
        $.ajax({
            url: '/admin/statistical/total-quantity-product',
            method: 'GET',
            success: function (response) {
                $('#totalQuantityProduct').text(response);
            },
            error: function (err) {
                console.error('Error fetching revenue:', err);
                $('#totalQuantityProduct').text('0');
            }
        });
    });
});

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
            name: key === 'OFFLINE' ? 'Trực tiếp' : 'Trực tuyến',
            y: parseFloat(data[key]),
            color: key === 'OFFLINE' ? '#36A2EB' : '#FF6384'
        }));

        Highcharts.chart('average-chart', {
            chart: {
                type: 'pie'
            },
            title: {
                text: 'Biểu đồ tổng doanh thu theo loại đơn hàng'
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

        // Hiển thị giá trị chi tiết
        $('#total-pay-online').text(formatPrice(data.ONLINE));
        $('#total-pay-offline').text(formatPrice(data.OFFLINE));
    }

});
function formatPrice(amount) {
    // Chuyển đổi số thành chuỗi và định dạng với dấu phẩy
    return parseFloat(amount).toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",") + ' ₫';
}