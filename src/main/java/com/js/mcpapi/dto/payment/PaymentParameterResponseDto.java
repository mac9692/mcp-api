package com.js.mcpapi.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 결제 파라미터 응답 DTO
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentParameterResponseDto {

    private String pgCode;
    private String pgName;
    private String mid;
    private String timestamp;
    private String signature;
    private String verification;
    private String mKey;
    private String paymentUrl;
}