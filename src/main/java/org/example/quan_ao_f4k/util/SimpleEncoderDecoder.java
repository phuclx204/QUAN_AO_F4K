package org.example.quan_ao_f4k.util;

import java.util.Base64;

public class SimpleEncoderDecoder {

    public static String encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String decode(String encodedInput) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedInput);
        return new String(decodedBytes);
    }
}
