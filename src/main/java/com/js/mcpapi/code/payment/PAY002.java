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
public enum PAY002 {
    POINT("10", "포인트"),
    COIN("20", "COIN");

    private final String code;
    private final String desc;
}
