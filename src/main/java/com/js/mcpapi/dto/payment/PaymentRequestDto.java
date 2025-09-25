package com.js.mcpapi.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 결제 요청 DTO
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    private BigDecimal amount;
    private String productName;
    private String buyerName;
    private String buyerEmail;
    private String buyerTel;
    private String merchantUid;
    private String successUrl;
    private String failUrl;
}