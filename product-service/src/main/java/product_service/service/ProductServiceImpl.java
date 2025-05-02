package product_service.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import product_service.dto.ProductDTO;
import product_service.exception.ResourceNotFoundException;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://fakestoreapi.com/products";

    @Override
    public List<ProductDTO> getAllProducts(Pageable pageable) {
        try {
            ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(API_URL, ProductDTO[].class);
            List<ProductDTO> allProducts = Arrays.asList(response.getBody());

            int page = pageable.getPageNumber();
            int size = pageable.getPageSize();
            int start = page * size;
            int end = Math.min(start + size, allProducts.size());

            if (start > allProducts.size()) {
                return List.of(); // página vacía
            }

            return allProducts.subList(start, end);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch product list: " + e.getMessage());
        }
    }

    @Override
    public ProductDTO getProductById(Long id) {
        try {
            ProductDTO product = restTemplate.getForObject(API_URL + "/" + id, ProductDTO.class);

            if (product == null || product.getId() == null) {
                throw new ResourceNotFoundException("Product with ID " + id + " not found");
            }

            return product;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Product with ID " + id + " not found");
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving product: " + e.getMessage());
        }
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        try {
            if (productDto == null || id == null) {
                throw new IllegalArgumentException("Product or ID must not be null");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ProductDTO> request = new HttpEntity<>(productDto, headers);

            ResponseEntity<ProductDTO> response = restTemplate.exchange(
                    API_URL + "/" + id,
                    HttpMethod.PUT,
                    request,
                    ProductDTO.class
            );

            if (response.getBody() == null) {
                throw new ResourceNotFoundException("Unable to update product with ID " + id);
            }

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Product with ID " + id + " not found");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Invalid product data: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error updating product: " + e.getMessage());
        }
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDto) {
        try {
            if (productDto == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ProductDTO> request = new HttpEntity<>(productDto, headers);

            ProductDTO created = restTemplate.postForObject(API_URL, request, ProductDTO.class);

            if (created == null) {
                throw new RuntimeException("Failed to create product");
            }

            return created;

        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Invalid product data: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error creating product: " + e.getMessage());
        }
    }

    @Override
    public void deleteProduct(Long id) {
        try {
            restTemplate.delete(API_URL + "/" + id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Product with ID " + id + " not found");
        } catch (Exception e) {
            throw new RuntimeException("Error deleting product: " + e.getMessage());
        }
    }
}
