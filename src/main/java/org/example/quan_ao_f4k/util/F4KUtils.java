package org.example.quan_ao_f4k.util;

import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.List;

@Component
public class F4KUtils {

    @Autowired
    private UserRepository userRepository;

    private F4KUtils() {
    }

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

    //TODO: Đợi thích hợp xong JWT bắt buộc xem lại
//    public User getUser() {
//    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof UserDetails) {
//                return ((UserDetails) principal);
//            } else {
//                return null; // Trường hợp này có thể xảy ra nếu không dùng UserDetails
//            }
//        }
//        return null;
        if (userDetails == null) {
            throw new BadRequestException("Lỗi đăng nhập, xem lại tài khoản đang đăng nhập");
        }
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new BadRequestException("Lỗi đăng nhập, xem lại tài khoản đang đăng nhập")
        );
    }
}
