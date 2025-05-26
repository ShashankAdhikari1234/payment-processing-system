package com.org.payment_processing_system.service;

import com.org.payment_processing_system.dto.PayPalPaymentResponseDTO;
import com.org.payment_processing_system.dto.PaymentRequest;
import com.org.payment_processing_system.entity.Transaction;
import com.org.payment_processing_system.exception.PayPalServiceException;
import com.org.payment_processing_system.repo.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url}")
    private String paypalBaseUrl;

    private String accessToken;

    private LocalDateTime tokenExpiry;

    @PostConstruct
    public void init() {
        fetchAccessToken();
    }

    private void fetchAccessToken() {
        log.info("Fetching PayPal OAuth2 token...");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    paypalBaseUrl + "/v1/oauth2/token",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                accessToken = (String) body.get("access_token");
                Integer expiresIn = (Integer) body.get("expires_in");
                tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn - 60); // Refresh a bit earlier
                log.info("Received access token, expires in {} seconds", expiresIn);
            } else {
                throw new PayPalServiceException("Failed to fetch PayPal access token. Status: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            throw new PayPalServiceException("Error occurred while fetching access token", ex);
        }
    }

    private void checkAndRefreshToken() {
        if (accessToken == null || LocalDateTime.now().isAfter(tokenExpiry)) {
            fetchAccessToken();
        }
    }

    @CircuitBreaker(name = "paypalApiCircuitBreaker", fallbackMethod = "paymentCreationFallback")
    public PayPalPaymentResponseDTO createPayment(PaymentRequest paymentRequest) {
        checkAndRefreshToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> paymentPayload = new HashMap<>();
        paymentPayload.put("intent", "sale");

        Map<String, Object> payer = new HashMap<>();
        payer.put("payment_method", "paypal");
        paymentPayload.put("payer", payer);

        Map<String, Object> amountDetails = new HashMap<>();
        amountDetails.put("total", String.format("%.2f", paymentRequest.getAmount()));
        amountDetails.put("currency", paymentRequest.getCurrency());

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("amount", amountDetails);
        paymentPayload.put("transactions", Collections.singletonList(transaction));

        Map<String, String> redirectUrls = new HashMap<>();
        redirectUrls.put("return_url", paymentRequest.getReturnUrl());
        redirectUrls.put("cancel_url", paymentRequest.getCancelUrl());
        paymentPayload.put("redirect_urls", redirectUrls);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentPayload, headers);

        try {
            ResponseEntity<PayPalPaymentResponseDTO> response = restTemplate.exchange(
                    paypalBaseUrl + "/v1/payments/payment",
                    HttpMethod.POST,
                    entity,
                    PayPalPaymentResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                PayPalPaymentResponseDTO body = response.getBody();

                Transaction txn = Transaction.builder()
                        .paypalTransactionId(body.getId())
                        .status("CREATED")
                        .amount(paymentRequest.getAmount())
                        .currency(paymentRequest.getCurrency())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                transactionRepository.save(txn);
                return body;
            } else {
                throw new PayPalServiceException("Failed to create PayPal payment. Status: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            throw new PayPalServiceException("Error occurred while creating PayPal payment", ex);
        }
    }

    public PayPalPaymentResponseDTO paymentCreationFallback(PaymentRequest request, Throwable throwable) {
        log.error("Payment creation failed due to: {}", throwable.getMessage(), throwable);
        throw new PayPalServiceException("PayPal API is currently unavailable. Please try again later.", throwable);
    }
}
