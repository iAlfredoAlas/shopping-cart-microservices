package order_service.service;


import order_service.dto.OrderDTO;
import order_service.dto.OrderItemDTO;
import order_service.dto.ProductResponseDTO;
import order_service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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
        double total = 0.0;

        for (OrderItemDTO item : orderDTO.getItems()) {
            try {
                String url = productServiceUrl + "/" + item.getProductId();
                ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    throw new ResourceNotFoundException("Product with ID " + item.getProductId() + " not found");
                }

                Map<String, Object> productData = response.getBody();

                double price = Double.parseDouble(productData.get("price").toString());
                String title = productData.get("title").toString();
                double subtotal = price * item.getQuantity();

                // Set product details in new DTO
                ProductResponseDTO product = new ProductResponseDTO();
                product.setId(item.getProductId());
                product.setTitle(title);
                product.setPrice(price);

                item.setProduct(product);
                item.setSubtotal(subtotal);

                total += subtotal;

            } catch (Exception e) {
                throw new ResourceNotFoundException("Product with ID " + item.getProductId() + " not found");
            }
        }

        orderDTO.setTotalAmount(total);
        return orderDTO;
    }

}
