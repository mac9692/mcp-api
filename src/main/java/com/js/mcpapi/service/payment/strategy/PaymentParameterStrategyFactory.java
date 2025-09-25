package com.js.mcpapi.service.payment.strategy;

import com.js.mcpapi.code.payment.PAY003;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 결제 파라미터 전략 팩토리
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Component
@RequiredArgsConstructor
public class PaymentParameterStrategyFactory {

    private final InicisPaymentParameterStrategy inicisStrategy;
    private final TossPaymentParameterStrategy tossStrategy;
    private final NicePaymentParameterStrategy niceStrategy;

    public PaymentParameterStrategy getStrategy(PAY003 pgType) {
        return switch (pgType) {
            case INICIS_PAY -> inicisStrategy;
            case TOSS_PAY -> tossStrategy;
            case NICE_PAY -> niceStrategy;
            default -> throw new IllegalArgumentException("지원하지 않는 PG사입니다: " + pgType);
        };
    }
}