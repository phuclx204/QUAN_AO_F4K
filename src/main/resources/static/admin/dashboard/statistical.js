$(document).ready(function() {
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

    // Điền ngày bắt đầu và kết thúc vào các input
    const startDate = formatDate(last7Days);
    const endDate = formatDate(today);

    // Điền vào các trường ngày
    $("#startDate").val(startDate);
    $("#endDate").val(endDate);

    // Hàm vẽ biểu đồ
    function drawChart(orderStatistics) {
        const labels = Object.keys(orderStatistics); // Mảng các ngày
        const revenueData = labels.map(date => orderStatistics[date].totalRevenue); // Mảng doanh thu
        const ordersData = labels.map(date => orderStatistics[date].numberOfOrders); // Mảng số đơn hàng

        // Vẽ biểu đồ với Highcharts
        Highcharts.chart('chart', {
            chart: {
                type: 'bar'  // Chọn kiểu biểu đồ thanh
            },
            title: {
                text: 'Doanh thu và số đơn hàng theo ngày'
            },
            xAxis: {
                categories: labels, // Các ngày làm trục X
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
                valueSuffix: ' VND'
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

    // Gửi yêu cầu AJAX khi trang được tải để lấy dữ liệu cho 7 ngày gần nhất
    $.ajax({
        url: "/admin/statistical/data",
        method: "GET",
        data: {
            startDate: startDate,
            endDate: endDate
        },
        success: function(orderStatistics) {
            console.log(orderStatistics); // Kiểm tra dữ liệu nhận được
            drawChart(orderStatistics); // Vẽ biểu đồ với dữ liệu nhận được
        },
        error: function(err) {
            console.error("Error:", err);
        }
    });

    // Lọc dữ liệu khi nhấn nút "Lọc"
    $("#filterButton").click(function() {
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
            success: function(orderStatistics) {
                console.log(orderStatistics); // Kiểm tra dữ liệu nhận được
                drawChart(orderStatistics); // Cập nhật biểu đồ với dữ liệu mới
            },
            error: function(err) {
                console.error("Error:", err);
            }
        });
    });
});
$(document).ready(function() {
    // Gửi yêu cầu AJAX đến API để lấy doanh thu
    $.ajax({
        url: '/admin/statistical/totalRevenue',
        method: 'GET',
        success: function(response) {
            // Khi có dữ liệu, cập nhật giá trị vào thẻ HTML
            $('#revenueAmount').text(response.toFixed(0).replace(/\d(?=(\d{3})+\.)/g, '$&,')+'₫' );
        },
        error: function(err) {
            console.error('Error fetching revenue:', err);
            $('#revenueAmount').text('0₫'); // Hiển thị 0 nếu có lỗi
        }
    });
});
