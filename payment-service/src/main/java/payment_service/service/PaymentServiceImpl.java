package payment_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import payment_service.config.RestTemplateConfig;
import payment_service.dto.PaymentRequestDTO;
import payment_service.dto.PaymentResponseDTO;
import payment_service.enums.PaymentStatus;
import payment_service.exception.BadRequestException;
import payment_service.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        String url = orderServiceUrl + "/" + request.getOrderId();

        try {
            ResponseEntity<Map> orderResponse = restTemplate.getForEntity(url, Map.class);

            if (!orderResponse.getStatusCode().is2xxSuccessful() || orderResponse.getBody() == null) {
                throw new ResourceNotFoundException("Order not found.");
            }

            Map<String, Object> orderData = orderResponse.getBody();

            Map<String, Object> customer = (Map<String, Object>) orderData.get("customer");
            Double totalAmount = Double.parseDouble(orderData.get("totalAmount").toString());

            boolean invalidCustomer = customer == null || !customer.get("id").toString().equals(request.getCustomerId().toString());
            boolean invalidAmount = !amountEquals(totalAmount, request.getAmount());

            if (invalidCustomer || invalidAmount) {
                StringBuilder errorMessage = new StringBuilder("Payment validation failed: ");

                if (invalidCustomer) {
                    errorMessage.append("customer ID mismatch");
                }

                if (invalidAmount) {
                    if (invalidCustomer) errorMessage.append(", ");
                    errorMessage.append("amount mismatch");
                }

                throw new BadRequestException(errorMessage.toString());
            }

            PaymentResponseDTO response = new PaymentResponseDTO();
            response.setOrderId(request.getOrderId());
            response.setStatus(PaymentStatus.SUCCESS);
            response.setMessage("Payment processed successfully.");
            return response;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Order with ID " + request.getOrderId() + " not found");
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Unexpected error: " + e.getMessage());
        }
    }

    private boolean amountEquals(Double expected, Double actual) {
        BigDecimal expectedBD = BigDecimal.valueOf(expected).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualBD = BigDecimal.valueOf(actual).setScale(2, RoundingMode.HALF_UP);
        return expectedBD.compareTo(actualBD) == 0;
    }
}