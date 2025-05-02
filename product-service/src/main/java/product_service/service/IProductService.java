package product_service.service;

import product_service.dto.ProductDTO;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IProductService {

    List<ProductDTO> getAllProducts(Pageable pageable);
    ProductDTO getProductById(Long id);
    ProductDTO updateProduct(Long id, ProductDTO productDto);
    ProductDTO createProduct(ProductDTO productDto);
    void deleteProduct(Long id);

}
