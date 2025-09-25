package com.js.mcpapi.config.payment;

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
@EnableConfigurationProperties({InicisPaymentConfig.class, TossPaymentConfig.class, NicePaymentConfig.class})
public class PaymentConfig {
}