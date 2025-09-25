package com.js.mcpapi.service.payment.strategy;

import com.js.mcpapi.dto.payment.PaymentRequestDto;
import com.js.mcpapi.dto.payment.PaymentParameterResponseDto;

/**
 * 결제 파라미터 생성 전략 인터페이스
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
public interface PaymentParameterStrategy {

    PaymentParameterResponseDto generateParameters(PaymentRequestDto request);
}