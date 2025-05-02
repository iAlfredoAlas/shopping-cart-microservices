package order_service.service;


import order_service.dto.OrderDTO;
import order_service.dto.OrderDetailDTO;
import order_service.dto.OrderItemDTO;
import order_service.dto.ProductResponseDTO;
import order_service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderServiceImpl implements IOrderService {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    private final Map<Long, OrderDTO> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        double total = 0.0;
        List<OrderDetailDTO> details = new ArrayList<>();

        for (OrderItemDTO item : orderDTO.getItems()) {
            try {
                String url = productServiceUrl + "/" + item.getProductId();
                ResponseEntity<ProductResponseDTO> response =
                        restTemplate.getForEntity(url, ProductResponseDTO.class);

                ProductResponseDTO product = response.getBody();

                if (product == null || product.getTitle() == null || product.getPrice() <= 0) {
                    throw new ResourceNotFoundException("Invalid product data for ID " + item.getProductId());
                }

                double subtotal = product.getPrice() * item.getQuantity();

                item.setProduct(product);
                item.setSubtotal(subtotal);

                OrderDetailDTO detail = new OrderDetailDTO();
                detail.setProductId(product.getId());
                detail.setProductName(product.getTitle());
                detail.setPrice(product.getPrice());
                detail.setQuantity(item.getQuantity());
                detail.setSubtotal(subtotal);

                details.add(detail);
                total += subtotal;

            } catch (Exception e) {
                throw new ResourceNotFoundException("Product with ID " + item.getProductId() + " not found");
            }
        }

        Long orderId = idGenerator.getAndIncrement();
        orderDTO.setId(orderId);
        orderDTO.setDetails(details);

        BigDecimal roundedTotal = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
        orderDTO.setTotalAmount(roundedTotal.doubleValue());

        orders.put(orderId, orderDTO);

        return orderDTO;
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        OrderDTO order = orders.get(id);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID " + id + " not found");
        }
        return order;
    }
}