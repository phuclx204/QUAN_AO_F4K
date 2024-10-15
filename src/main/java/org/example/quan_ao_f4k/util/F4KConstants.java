package org.example.quan_ao_f4k.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class F4KConstants {

    public final static int STATUS_ON = 1;
    public final static int STATUS_OFF = 0;

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
        public static final String PRODUCT_DETAIL = "ProductDetail";
    }
}
