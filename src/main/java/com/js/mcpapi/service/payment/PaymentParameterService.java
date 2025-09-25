package com.js.mcpapi.service.payment;

import com.js.mcpapi.code.payment.PAY003;
import com.js.mcpapi.dto.payment.PaymentRequestDto;
import com.js.mcpapi.dto.payment.PaymentParameterResponseDto;
import com.js.mcpapi.service.payment.strategy.PaymentParameterStrategy;
import com.js.mcpapi.service.payment.strategy.PaymentParameterStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * 결제 파라미터 서비스
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@Service
@RequiredArgsConstructor
public class PaymentParameterService {

    private final PaymentParameterStrategyFactory strategyFactory;
    private final SecureRandom secureRandom = new SecureRandom();

    public PaymentParameterResponseDto generatePaymentParameters(PaymentRequestDto request) {
        PAY003 selectedPg = selectPgByWeight();
        PaymentParameterStrategy strategy = strategyFactory.getStrategy(selectedPg);
        return strategy.generateParameters(request);
    }

    private PAY003 selectPgByWeight() {
        List<PAY003> pgList = Arrays.asList(PAY003.values());
        int totalWeight = pgList.stream().mapToInt(PAY003::getWeight).sum();

        if (totalWeight == 0) {
            // 모든 가중치가 0인 경우 첫 번째 PG 반환
            return pgList.get(0);
        }

        int randomValue = secureRandom.nextInt(totalWeight);
        int currentWeight = 0;

        for (PAY003 pg : pgList) {
            currentWeight += pg.getWeight();
            if (randomValue < currentWeight) {
                return pg;
            }
        }

        // 기본적으로 첫 번째 PG 반환 (예외 상황)
        return pgList.get(0);
    }
}