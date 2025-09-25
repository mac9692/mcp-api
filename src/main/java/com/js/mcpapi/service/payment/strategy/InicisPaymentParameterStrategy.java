package com.js.mcpapi.service.payment.strategy;

import com.js.mcpapi.code.payment.PAY003;
import com.js.mcpapi.config.payment.InicisPaymentConfig;
import com.js.mcpapi.dto.payment.PaymentRequestDto;
import com.js.mcpapi.dto.payment.PaymentParameterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 이니시스 결제 파라미터 생성 전략
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Component
@RequiredArgsConstructor
public class InicisPaymentParameterStrategy implements PaymentParameterStrategy {

    private final InicisPaymentConfig inicisConfig;

    @Override
    public PaymentParameterResponseDto generateParameters(PaymentRequestDto request) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 이니시스 signData 생성: oid + price + timestamp
        String signData = request.getMerchantUid() + request.getAmount() + timestamp;
        String signature = generateSignature(signData);

        // 이니시스 verification 생성: mid + signData
        String verificationData = inicisConfig.getMid() + signData;
        String verification = generateSHA256Hash(verificationData);

        return new PaymentParameterResponseDto(
                PAY003.INICIS_PAY.getCode(),
                PAY003.INICIS_PAY.getDesc(),
                inicisConfig.getMid(),
                timestamp,
                signature,
                verification,
                inicisConfig.getHashKey(),
                "https://mobile.inicis.com/smart/payment/"
        );
    }

    private String generateSignature(String data) {
        try {
            String signKey = inicisConfig.getSignKey();
            String combinedData = signKey + data;
            return generateSHA256Hash(combinedData);
        } catch (Exception e) {
            throw new RuntimeException("이니시스 서명 생성 실패", e);
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