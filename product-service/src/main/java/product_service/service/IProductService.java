package product_service.service;

import product_service.dto.ProductDTO;

import java.util.List;

public interface IProductService {

    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO productDto);
    void deleteProduct(Long id);

}
