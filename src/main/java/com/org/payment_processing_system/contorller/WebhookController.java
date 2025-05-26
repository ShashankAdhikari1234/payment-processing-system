package com.org.payment_processing_system.contorller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/webhooks")
@Slf4j
public class WebhookController {

    private static final String WEBHOOK_SECRET = "YourWebhookSecretHere"; // Store securely in real app

    @PostMapping("/paypal")
    public ResponseEntity<String> handlePaypalWebhook(HttpServletRequest request) throws Exception {
        String signature = request.getHeader("PAYPAL-TRANSMISSION-SIG");
        String payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);

        log.info("Received PayPal webhook payload: {}", payload);

        if (!validateSignature(payload, signature)) {
            log.warn("Invalid webhook signature");
            return ResponseEntity.status(400).body("Invalid signature");
        }

        // Log webhook payload or process according to your business logic
        log.info("Webhook verified successfully.");

        return ResponseEntity.ok("Webhook received");
    }

    private boolean validateSignature(String payload, String signature) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(WEBHOOK_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKey);

        byte[] hash = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String computedSignature = Base64.getEncoder().encodeToString(hash);

        return computedSignature.equals(signature);
    }
}
