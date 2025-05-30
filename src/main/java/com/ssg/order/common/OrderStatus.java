package com.ssg.order.common;

import java.util.Arrays;

public enum OrderStatus {
    CREATED("00", "주문 생성"),
    CANCELED("01", "주문 취소");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static OrderStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid order status code: " + code));
    }
}
