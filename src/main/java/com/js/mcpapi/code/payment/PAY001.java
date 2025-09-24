package com.js.mcpapi.code.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 24.
 */
@Getter
@AllArgsConstructor
public enum PAY001 {
    USE("10", "사용"),
    SAVE("20", "적립");

    private final String code;
    private final String desc;
}
