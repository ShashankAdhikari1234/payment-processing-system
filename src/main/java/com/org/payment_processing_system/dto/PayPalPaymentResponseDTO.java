package com.org.payment_processing_system.dto;

import lombok.Data;

import java.util.List;

@Data
public class PayPalPaymentResponseDTO {
    private String id;
    private String intent;
    private String state;
    private Payer payer;
    private List<Transaction> transactions;
    private String creatTime;
    private List<Link> links;

    @Data
    public static class Payer {
        private String paymentMethod;
    }

    @Data
    public static class Transaction {
        private Amount amount;
        private List<Object> relatedResources;
    }

    @Data
    public static class Amount {
        private String total;
        private String currency;
    }

    @Data
    public static class Link {
        private String href;
        private String rel;
        private String method;
    }
}
