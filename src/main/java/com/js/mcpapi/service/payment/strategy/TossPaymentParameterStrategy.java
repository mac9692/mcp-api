package com.js.mcpapi.service.payment.strategy;

import com.js.mcpapi.code.payment.PAY003;
import com.js.mcpapi.config.payment.TossPaymentConfig;
import com.js.mcpapi.dto.payment.PaymentRequestDto;
import com.js.mcpapi.dto.payment.PaymentParameterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 토스 결제 파라미터 생성 전략
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Component
@RequiredArgsConstructor
public class TossPaymentParameterStrategy implements PaymentParameterStrategy {

    private final TossPaymentConfig tossConfig;

    @Override
    public PaymentParameterResponseDto generateParameters(PaymentRequestDto request) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 토스 signData 생성: clientKey + amount + orderId + timestamp
        String signData = tossConfig.getClientKey() + request.getAmount() + request.getMerchantUid() + timestamp;
        String signature = generateHMACSHA256(signData, tossConfig.getSecretKey());

        // 토스 verification (추가 검증용)
        String verificationData = request.getMerchantUid() + request.getAmount().toString() + timestamp;
        String verification = generateHMACSHA256(verificationData, tossConfig.getSecretKey());

        return new PaymentParameterResponseDto(
                PAY003.TOSS_PAY.getCode(),
                PAY003.TOSS_PAY.getDesc(),
                tossConfig.getClientKey(),
                timestamp,
                signature,
                verification,
                Base64.getEncoder().encodeToString(tossConfig.getSecretKey().getBytes()),
                "https://js.tosspayments.com/v1/payment"
        );
    }

    private String generateHMACSHA256(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("토스 HMAC-SHA256 서명 생성 실패", e);
        }
    }
}