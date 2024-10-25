package org.example.quan_ao_f4k.util;

import java.util.List;

public interface SearchFields {
    List<String> BRAND = List.of("name");
    List<String> CATEGORY = List.of("name");
    List<String> SIZE = List.of("name");
    List<String> COLOR = List.of("name");
    List<String> PROVINCE = List.of("name");
    List<String> DISTRICT = List.of("name");
    List<String> WARD = List.of("name");
    List<String> ADDRESS = List.of("name");

    List<String> GUARANTEE = List.of("name");
    List<String> PRODUCT = List.of(
            "name",
            "category.name",
            "brand.name",
            "description"
    );

    List<String> PRODUCT_DETAIL = List.of(
            "color.name",
            "size.name"
    );
    List<String> ORDER = List.of(
            "code",
            "toName",
            "toAddress",
            "toPhone",
            "status",
            "paymentStatus",
            "paymentMethodType",
            "user.username",
            "note"
    );
}

