package com.firom.bms.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IdentifierGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private IdentifierGenerator() {
    }

    /** Generates a 10-digit numeric account number, e.g. 4821093756 */
    public static String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(1 + RANDOM.nextInt(9)); // first digit non-zero
        for (int i = 0; i < 9; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /** Generates a transaction reference, e.g. TRX-20260701-A1B2C3D4 */
    public static String generateTransactionReference() {
        String datePart = LocalDate.now().format(DATE_FMT);
        String randomPart = Long.toHexString(RANDOM.nextLong()).toUpperCase();
        if (randomPart.length() > 8) {
            randomPart = randomPart.substring(0, 8);
        }
        return "TRX-" + datePart + "-" + randomPart;
    }
}
