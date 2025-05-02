package order_service.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private double subtotal;
}
