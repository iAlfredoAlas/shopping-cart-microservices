package order_service.service;

import order_service.dto.OrderDTO;

public interface IOrderService {

    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);


}
