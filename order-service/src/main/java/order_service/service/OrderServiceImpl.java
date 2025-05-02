package order_service.service;


import order_service.dto.OrderDTO;
import order_service.dto.OrderItemDTO;
import order_service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderServiceImpl implements IOrderService{

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public OrderServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        for (OrderItemDTO item : orderDTO.getItems()) {
            try {
                String url = productServiceUrl + "/" + item.getProductId();
                ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new ResourceNotFoundException("Product with ID " + item.getProductId() + " not found");
                }

            } catch (Exception e) {
                throw new ResourceNotFoundException("Product with ID " + item.getProductId() + " not found");
            }
        }

        System.out.println("Order created successfully for customer ID: " + orderDTO.getCustomerId());
        return orderDTO;
    }
}
