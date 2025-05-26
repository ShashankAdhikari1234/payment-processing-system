package com.org.payment_processing_system.dto;
import lombok.Data;

@Data
public class PaymentRequest {
//    @NotNull
    private Double amount;

//    @NotBlank
    private String currency;

//    @NotBlank
    private String returnUrl;

//    @NotBlank
    private String cancelUrl;
}
