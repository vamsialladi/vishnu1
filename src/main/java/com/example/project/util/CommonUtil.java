package com.example.project.util;

import java.util.UUID;

public class CommonUtil {

    private CommonUtil() {
        // Private constructor to prevent instantiation
    }

    public static String generateUniqueSku(String prefix) {
        String cleanPrefix = (prefix != null && !prefix.isBlank()) ? prefix.toUpperCase().trim() : "PLANT";
        String uuidPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return cleanPrefix + "-" + uuidPart;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
