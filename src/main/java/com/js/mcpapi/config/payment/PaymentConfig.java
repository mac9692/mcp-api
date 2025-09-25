package com.js.mcpapi.config.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 결제 관련 설정 통합 관리 클래스
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({InicisPaymentConfig.class, TossPaymentConfig.class, NicePaymentConfig.class})
public class PaymentConfig {

    private final InicisPaymentConfig inicisPaymentConfig;
    private final TossPaymentConfig tossPaymentConfig;
    private final NicePaymentConfig nicePaymentConfig;

    public InicisPaymentConfig getInicisConfig() {
        return inicisPaymentConfig;
    }

    public TossPaymentConfig getTossConfig() {
        return tossPaymentConfig;
    }

    public NicePaymentConfig getNiceConfig() {
        return nicePaymentConfig;
    }
}