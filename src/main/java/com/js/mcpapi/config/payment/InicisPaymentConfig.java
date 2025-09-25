package com.js.mcpapi.config.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 이니시스 결제 설정 클래스
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payment.inicis")
public class InicisPaymentConfig {

    private String mid;
    private String signKey;
    private String iniApiKey;
    private String hashKey;
}