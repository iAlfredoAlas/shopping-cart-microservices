package payment_service.service;

import payment_service.dto.PaymentRequestDTO;
import payment_service.dto.PaymentResponseDTO;

public interface IPaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO request);

}
