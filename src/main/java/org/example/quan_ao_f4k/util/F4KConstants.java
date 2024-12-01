package org.example.quan_ao_f4k.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class F4KConstants {

    public final static int STATUS_ON = 1;
    public final static int STATUS_OFF = 0;

    public final static int HET_HANG = 0;
    public final static int CON_HANG = 1;
    public final static int KHONG_DU_HANG = 2;

    public final static int TYPE_CASH = 1;
    public final static int TYPE_PERCENT = 2;

    public final static String ROLE_ADMIN = "ADMIN";
    public final static String ROLE_USER = "USER";
    public final static String ROLE_STAFF = "STAFF";

    @Getter
    @AllArgsConstructor
    public enum ErrCode {
        NOT_FOUND("Không tồn tại bản ghi có id [%s] tại bảng [%s]"),
        IS_EXITS("[%s] đã tồn tại trong bảng [%s]")
        ;
        private String description;
    }

    public static class TableCode {
        private TableCode() {};

        public static final String CATEGORY = "category";
        public static final String SIZE = "size";
        public static final String COLOR = "color";
        public static final String PRODUCT = "PRODUCT";
        public static final String PRODUCT_DETAIL = "PRODUCT_DETAIL";
        public static final String ORDER_HISTORY = "ORDER_HISTORY";
    }
}
