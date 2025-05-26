package com.org.payment_processing_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootApplication
public class PaymentProcessingSystemApplication {

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
		SpringApplication.run(PaymentProcessingSystemApplication.class, args);
		String payload = "{\"id\":\"WH-123456789\",\"event_type\":\"PAYMENT.SALE.COMPLETED\"}";
		String secret = "YourWebhookSecretHere";

		Mac hmac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		hmac.init(secretKey);
		byte[] hash = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
		String signature = Base64.getEncoder().encodeToString(hash);

		System.out.println("Generated Signature: " + signature);
	}

}
