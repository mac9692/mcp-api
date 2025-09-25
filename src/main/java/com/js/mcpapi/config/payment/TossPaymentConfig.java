package com.js.mcpapi.config.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 토스 결제 설정 클래스
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentConfig {

    private String clientKey;
    private String secretKey;
}