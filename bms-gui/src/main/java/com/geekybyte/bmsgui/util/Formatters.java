package com.geekybyte.bmsgui.util;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public final class Formatters {

    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Formatters() {
    }

    public static String money(BigDecimal amount) {
        if (amount == null) return "-";
        return amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
