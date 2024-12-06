package org.example.quan_ao_f4k.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class HoaDonUtils {
    private HoaDonUtils() {
    }

    public static final String ORDER_NOTE_ONLINE = "Chuyển tiền";
    public static final String ORDER_NOTE = "Đặt hàng";

    @Getter
    public enum PhuongThucMuaHang {
        CHUYEN_TIEN,
        THANH_TOAN_SAU_NHAN_HANG;
    }

    public static class TrangThaiThanhToan {
        public static final int CHUA_THANH_TOAN = 1;
        public static final int DA_THANH_TOAN = 2;
        public static final int CHO_THANH_TOAN = 3;
    }

    public static class LoaiHoaDon {
        public static final String ONLINE = "online";
        public static final String OFFLINE = "offline";

        private LoaiHoaDon() {

        }
    }

    @Getter
    @AllArgsConstructor
    public enum TrangThaiHoaDon {
        HUY_DON(0, "Hủy đơn"),
        TAO_MOI(1, "Tạo mới"),
        TRA_HANG(2, "Trả hàng"), // bỏ
        HOAN_TAT(3, "Hoàn tất"),
        CHO_GIAO_HANG(4, "Chờ giao hàng"),
        CHO_XAC_NHAN(5, "Chờ xác nhận"),
        DANG_GIAO_HANG(6, "Đang giao hàng"),
        DA_GIAO_HANG(7, "Đã giao hàng"), // bỏ
        CHO_LAY_HANG(8, "Chờ lấy hàng");

        private final int status; // Giá trị trạng thái
        private final String mess; // Mô tả trạng thái

        public static String getMessByStatus(int status) {
            for (TrangThaiHoaDon trangThai : TrangThaiHoaDon.values()) {
                if (trangThai.getStatus() == status) {
                    return trangThai.getMess();
                }
            }
            return "";
        }

        public static TrangThaiHoaDon getEnumByStatus(int status) {
            for (TrangThaiHoaDon trangThai : TrangThaiHoaDon.values()) {
                if (trangThai.getStatus() == status) {
                    return trangThai;
                }
            }
            return null;
        }
    }


    public static String taoMaHoaDon() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String ngayHienTai = sdf.format(new Date());

        Random random = new Random();
        int maRandom = random.nextInt(10000);

        String maRandomString = String.format("%04d", maRandom);
        return "DH-" + ngayHienTai + "-" + maRandomString;
    }

    public static void main(String[] args) {
        System.out.println(taoMaHoaDon());
    }
}
