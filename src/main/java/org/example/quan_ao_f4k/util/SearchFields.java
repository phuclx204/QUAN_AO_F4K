package org.example.quan_ao_f4k.util;

import java.util.List;

public interface SearchFields {
    List<String> BRAND = List.of(
            "name"
    );

    List<String> CATEGORY = List.of("name");

    List<String> GUARANTEE = List.of("" +
            "name",
            "status"
    );
    List<String> PRODUCT = List.of(
            "name",
            "category.name",
            "brand.name",
            "description",
            "status"
    );
}

