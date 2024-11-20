package org.example.quan_ao_f4k.util;

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

    public static class TrangThaiHoaDon {
        public static final int HUY_DON = 0;
        public static final int TAO_MOI = 1;
        public static final int TRA_HANG = 2;
        public static final int HOAN_TAT = 3;
        public static final int CHO_GIAO_HANG = 4;
        public static final int CHO_XAC_NHAN = 5;
        public static final int DANG_GIAO_HANG = 6;
        public static final int DA_GIAO_HANG = 7;
        public static final int CHO_LAY_HANG_HANG = 8;
        private TrangThaiHoaDon() {

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
