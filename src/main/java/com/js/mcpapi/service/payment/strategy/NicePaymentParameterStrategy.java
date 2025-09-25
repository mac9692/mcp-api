package com.js.mcpapi.service.payment.strategy;

import com.js.mcpapi.code.payment.PAY003;
import com.js.mcpapi.config.payment.NicePaymentConfig;
import com.js.mcpapi.dto.payment.PaymentRequestDto;
import com.js.mcpapi.dto.payment.PaymentParameterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 나이스페이 결제 파라미터 생성 전략
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Component
@RequiredArgsConstructor
public class NicePaymentParameterStrategy implements PaymentParameterStrategy {

    private final NicePaymentConfig niceConfig;

    @Override
    public PaymentParameterResponseDto generateParameters(PaymentRequestDto request) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 나이스페이 signData 생성: merchantId + orderId + amount + timestamp
        String signData = niceConfig.getMerchantId() + request.getMerchantUid() + request.getAmount() + timestamp;
        String signature = generateMD5Hash(signData + niceConfig.getMerchantKey());

        // 나이스페이 verification 생성 (추가 보안)
        String verificationData = request.getMerchantUid() + request.getAmount().toString() + timestamp;
        String verification = generateSHA256Hash(verificationData + niceConfig.getMerchantKey());

        return new PaymentParameterResponseDto(
                PAY003.NICE_PAY.getCode(),
                PAY003.NICE_PAY.getDesc(),
                niceConfig.getMerchantId(),
                timestamp,
                signature,
                verification,
                Base64.getEncoder().encodeToString(niceConfig.getMerchantKey().getBytes()),
                "https://web.nicepay.co.kr/v3/webpay/"
        );
    }

    private String generateMD5Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 해시 생성 실패", e);
        }
    }

    private String generateSHA256Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 해시 생성 실패", e);
        }
    }
}