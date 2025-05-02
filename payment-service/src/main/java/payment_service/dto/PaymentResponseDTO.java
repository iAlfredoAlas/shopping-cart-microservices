package payment_service.dto;

import lombok.Data;
import payment_service.enums.PaymentStatus;

@Data
public class PaymentResponseDTO {

    private Long orderId;
    private PaymentStatus status;
    private String message;
}