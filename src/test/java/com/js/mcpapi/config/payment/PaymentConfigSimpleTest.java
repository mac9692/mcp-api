package com.js.mcpapi.config.payment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 결제 설정 순수 JUnit 테스트 (Spring Context 없음)
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
class PaymentConfigSimpleTest {

    @Test
    void 이니시스_설정_생성_및_값_설정_테스트() {
        // Given
        InicisPaymentConfig config = new InicisPaymentConfig();

        // When
        config.setMid("INIpayTest");
        config.setSignKey("SU5JTElURV9UUklQTEVERVNfS0VZU1RS");
        config.setIniApiKey("ItEQKi3rY7uvDS8l");
        config.setHashKey("3CB8183A4BE283555ACC8363C0360223");

        // Then
        assertThat(config.getMid()).isEqualTo("INIpayTest");
        assertThat(config.getSignKey()).isEqualTo("SU5JTElURV9UUklQTEVERVNfS0VZU1RS");
        assertThat(config.getIniApiKey()).isEqualTo("ItEQKi3rY7uvDS8l");
        assertThat(config.getHashKey()).isEqualTo("3CB8183A4BE283555ACC8363C0360223");
    }

    @Test
    void 토스_설정_생성_및_값_설정_테스트() {
        // Given
        TossPaymentConfig config = new TossPaymentConfig();

        // When
        config.setClientKey("test_ck_DpexMgkW36PL5OnYYn7drGbR5ozO");
        config.setSecretKey("test_sk_P9BRQmyarY56W4lPgbnNrJ07KzLN");

        // Then
        assertThat(config.getClientKey()).isEqualTo("test_ck_DpexMgkW36PL5OnYYn7drGbR5ozO");
        assertThat(config.getSecretKey()).isEqualTo("test_sk_P9BRQmyarY56W4lPgbnNrJ07KzLN");
        assertThat(config.getClientKey()).startsWith("test_ck_");
        assertThat(config.getSecretKey()).startsWith("test_sk_");
    }

    @Test
    void 나이스페이_설정_생성_및_값_설정_테스트() {
        // Given
        NicePaymentConfig config = new NicePaymentConfig();

        // When
        config.setMerchantId("nicepay00m");
        config.setMerchantKey("EYzu8jGGMfqaDEp76gSckuvnaHHu+bC4opsSN6lHv3b2lurNYkVXrZ7Z1AoqQnXI3eLuaUFyoRNC6FkrzVjceg==");

        // Then
        assertThat(config.getMerchantId()).isEqualTo("nicepay00m");
        assertThat(config.getMerchantKey()).isEqualTo("EYzu8jGGMfqaDEp76gSckuvnaHHu+bC4opsSN6lHv3b2lurNYkVXrZ7Z1AoqQnXI3eLuaUFyoRNC6FkrzVjceg==");
        assertThat(config.getMerchantKey()).hasSizeGreaterThan(50);
    }

    @Test
    void PaymentConfig_통합_설정_테스트() {
        // Given
        InicisPaymentConfig inicisConfig = new InicisPaymentConfig();
        inicisConfig.setMid("INIpayTest");
        inicisConfig.setSignKey("SU5JTElURV9UUklQTEVERVNfS0VZU1RS");

        TossPaymentConfig tossConfig = new TossPaymentConfig();
        tossConfig.setClientKey("test_ck_DpexMgkW36PL5OnYYn7drGbR5ozO");
        tossConfig.setSecretKey("test_sk_P9BRQmyarY56W4lPgbnNrJ07KzLN");

        NicePaymentConfig niceConfig = new NicePaymentConfig();
        niceConfig.setMerchantId("nicepay00m");
        niceConfig.setMerchantKey("EYzu8jGGMfqaDEp76gSckuvnaHHu+bC4opsSN6lHv3b2lurNYkVXrZ7Z1AoqQnXI3eLuaUFyoRNC6FkrzVjceg==");

        PaymentConfig paymentConfig = new PaymentConfig(inicisConfig, tossConfig, niceConfig);

        // When & Then
        assertThat(paymentConfig.getInicisConfig()).isNotNull();
        assertThat(paymentConfig.getTossConfig()).isNotNull();
        assertThat(paymentConfig.getNiceConfig()).isNotNull();

        assertThat(paymentConfig.getInicisConfig().getMid()).isEqualTo("INIpayTest");
        assertThat(paymentConfig.getTossConfig().getClientKey()).startsWith("test_ck_");
        assertThat(paymentConfig.getNiceConfig().getMerchantId()).isEqualTo("nicepay00m");
    }

    @Test
    void 모든_PG사_설정_값_유효성_검증_테스트() {
        // Given
        InicisPaymentConfig inicis = new InicisPaymentConfig();
        inicis.setMid("INIpayTest");
        inicis.setSignKey("SU5JTElURV9UUklQTEVERVNfS0VZU1RS");
        inicis.setIniApiKey("ItEQKi3rY7uvDS8l");
        inicis.setHashKey("3CB8183A4BE283555ACC8363C0360223");

        TossPaymentConfig toss = new TossPaymentConfig();
        toss.setClientKey("test_ck_DpexMgkW36PL5OnYYn7drGbR5ozO");
        toss.setSecretKey("test_sk_P9BRQmyarY56W4lPgbnNrJ07KzLN");

        NicePaymentConfig nice = new NicePaymentConfig();
        nice.setMerchantId("nicepay00m");
        nice.setMerchantKey("EYzu8jGGMfqaDEp76gSckuvnaHHu+bC4opsSN6lHv3b2lurNYkVXrZ7Z1AoqQnXI3eLuaUFyoRNC6FkrzVjceg==");

        // When & Then - 모든 설정값이 null이 아니고 비어있지 않아야 함
        assertThat(inicis.getMid()).isNotNull().isNotBlank();
        assertThat(inicis.getSignKey()).isNotNull().isNotBlank();
        assertThat(inicis.getIniApiKey()).isNotNull().isNotBlank();
        assertThat(inicis.getHashKey()).isNotNull().isNotBlank();

        assertThat(toss.getClientKey()).isNotNull().isNotBlank();
        assertThat(toss.getSecretKey()).isNotNull().isNotBlank();

        assertThat(nice.getMerchantId()).isNotNull().isNotBlank();
        assertThat(nice.getMerchantKey()).isNotNull().isNotBlank();
    }

    @Test
    void PAY003_enum_코드_값_검증_테스트() {
        // Given & When & Then
        assertThat(com.js.mcpapi.code.payment.PAY003.INICIS_PAY.getCode()).isEqualTo("10");
        assertThat(com.js.mcpapi.code.payment.PAY003.INICIS_PAY.getDesc()).isEqualTo("이니시스");

        assertThat(com.js.mcpapi.code.payment.PAY003.NICE_PAY.getCode()).isEqualTo("20");
        assertThat(com.js.mcpapi.code.payment.PAY003.NICE_PAY.getDesc()).isEqualTo("나이스페이");

        assertThat(com.js.mcpapi.code.payment.PAY003.TOSS_PAY.getCode()).isEqualTo("30");
        assertThat(com.js.mcpapi.code.payment.PAY003.TOSS_PAY.getDesc()).isEqualTo("토스페이");
    }
}