package com.medicare.hms.util;

import java.util.regex.Pattern;

/**
 * Sri Lankan phone numbers.
 * Valid: 0 + 9 digits (0771234567, 0112345678) or +94 + 9 digits (+94771234567).
 * Spaces and dashes are allowed while typing (077 123 4567, +94 77-123-4567) and removed before saving.
 */
public final class PhoneValidator {

    private static final Pattern SRI_LANKAN = Pattern.compile("^(0\\d{9}|\\+94\\d{9})$");

    public static final String FORMAT_HINT = "Use a Sri Lankan number like 0771234567 or +94771234567.";

    private PhoneValidator() {
    }

    /** Removes spaces and dashes, e.g. "077 123-4567" becomes "0771234567". */
    public static String normalize(String phone) {
        return phone == null ? null : phone.replaceAll("[\\s-]", "");
    }

    public static boolean isValid(String phone) {
        return phone != null && SRI_LANKAN.matcher(normalize(phone)).matches();
    }

    /** Required phone: throws when missing or wrong, otherwise returns the cleaned number. */
    public static String requireValid(String phone, String fieldName) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        if (!isValid(phone)) {
            throw new IllegalArgumentException(fieldName + " is not valid. " + FORMAT_HINT);
        }
        return normalize(phone);
    }

    /** Optional phone: empty is allowed, anything typed must be valid. */
    public static String optionalValid(String phone, String fieldName) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return requireValid(phone, fieldName);
    }
}
