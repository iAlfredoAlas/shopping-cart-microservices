package payment_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import payment_service.config.RestTemplateConfig;
import payment_service.dto.PaymentRequestDTO;
import payment_service.dto.PaymentResponseDTO;
import payment_service.enums.PaymentStatus;

import java.util.Map;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final RestTemplate restTemplate;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public PaymentServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setOrderId(request.getOrderId());

        try {

            String url = orderServiceUrl + "/" + request.getOrderId();
            ResponseEntity<Map> orderResponse = restTemplate.getForEntity(url, Map.class);

            if (!orderResponse.getStatusCode().is2xxSuccessful() || orderResponse.getBody() == null) {
                response.setStatus(PaymentStatus.FAILED);
                response.setMessage("Order not found.");
                return response;
            }

            Map<String, Object> orderData = orderResponse.getBody();

            Map customer = (Map) orderData.get("customer");
            if (customer == null || !customer.get("id").toString().equals(request.getCustomerId().toString())) {
                response.setStatus(PaymentStatus.FAILED);
                response.setMessage("Customer ID does not match with order.");
                return response;
            }

            response.setStatus(PaymentStatus.SUCCESS);
            response.setMessage("Payment processed successfully.");
            return response;

        } catch (Exception e) {
            response.setStatus(PaymentStatus.FAILED);
            response.setMessage("Error validating order: " + e.getMessage());
            return response;
        }
    }
}

