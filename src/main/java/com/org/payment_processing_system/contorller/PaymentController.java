package com.org.payment_processing_system.contorller;
import com.org.payment_processing_system.dto.PayPalPaymentResponseDTO;
import com.org.payment_processing_system.dto.PaymentRequest;
import com.org.payment_processing_system.service.PayPalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PayPalService payPalService;

    @PostMapping("/create")
    public ResponseEntity<PayPalPaymentResponseDTO> createPayment(@RequestBody PaymentRequest request) {
        PayPalPaymentResponseDTO payment = payPalService.createPayment(request);
        return ResponseEntity.ok(payment);
    }

}
