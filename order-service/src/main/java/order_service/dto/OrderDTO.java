package order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    private Long id;

    @Valid
    @NotNull(message = "Customer is required")
    private CustomerDTO customer;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemDTO> items;

    private List<OrderDetailDTO> details;

    private double totalAmount;

}
