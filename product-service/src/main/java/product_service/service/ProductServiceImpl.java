package product_service.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import product_service.dto.ProductDTO;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://fakestoreapi.com/products";

    @Override
    public List<ProductDTO> getAllProducts(Pageable pageable) {
        ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(API_URL, ProductDTO[].class);
        List<ProductDTO> allProducts = Arrays.asList(response.getBody());

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, allProducts.size());

        if (start > allProducts.size()) {
            return List.of();
        }

        return allProducts.subList(start, end);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        try {
            return restTemplate.getForObject(API_URL + "/" + id, ProductDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Product with ID " + id + " not found");
        }
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> request = new HttpEntity<>(productDto, headers);

        ResponseEntity<ProductDTO> response = restTemplate.exchange(
                API_URL + "/" + id,
                HttpMethod.PUT,
                request,
                ProductDTO.class
        );

        return response.getBody();
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> request = new HttpEntity<>(productDto, headers);
        return restTemplate.postForObject(API_URL, request, ProductDTO.class);
    }

    @Override
    public void deleteProduct(Long id) {
        try {
            restTemplate.delete(API_URL + "/" + id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Product with ID " + id + " not found");
        }
    }
}
