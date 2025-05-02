package order_service.dto;

import lombok.Data;

@Data
public class ProductResponseDTO {

    private Long id;
    private String title;
    private double price;

}
