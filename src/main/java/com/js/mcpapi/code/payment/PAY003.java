package com.js.mcpapi.code.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Getter
@AllArgsConstructor
public enum PAY003 {
    INICIS_PAY("10", "이니시스"),
    NICE_PAY("20", "나이스페이"),
    TOSS_PAY("30", "토스페이");

    private final String code;
    private final String desc;
}
