package org.example.quan_ao_f4k.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class F4KUtils {

    public static <T> Page<T> toPage(List<T> list, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<T> pageList;


        if (list.size() < startItem) {
            pageList = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, list.size());
            pageList = list.subList(startItem, toIndex);
        }

        return new PageImpl<>(pageList, pageable, list.size());
    }

    public static String toSlug(String input) {
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", "");

        slug = slug.toLowerCase();

        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        slug = slug.replaceAll("\\s+", "-");
        slug = slug.replaceAll("-+", "-");
        return slug + "-" + generateRandomCode();
    }

    public static String generateRandomCode() {
        String character = "abcdefghijklmnopqrstuvwxyz0123456789-";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(character.length());
            code.append(character.charAt(index));
        }
        return code.toString();
    }
}
