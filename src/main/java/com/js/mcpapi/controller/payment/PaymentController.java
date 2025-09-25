package com.js.mcpapi.controller.payment;

import com.js.mcpapi.dto.payment.PaymentRequestDto;
import com.js.mcpapi.dto.payment.PaymentParameterResponseDto;
import com.js.mcpapi.service.payment.PaymentParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 결제 컨트롤러
 *
 * @author parkjinseong
 * @version 1.0
 * @since 2025. 9. 25.
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentParameterService paymentParameterService;

    @PostMapping("/parameters")
    public ResponseEntity<PaymentParameterResponseDto> getPaymentParameters(@RequestBody PaymentRequestDto request) {
        PaymentParameterResponseDto response = paymentParameterService.generatePaymentParameters(request);
        return ResponseEntity.ok(response);
    }
}