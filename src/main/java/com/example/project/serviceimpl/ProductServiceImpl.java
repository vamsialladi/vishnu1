package com.example.project.serviceimpl;

import com.example.project.dto.ProductRequest;
import com.example.project.dto.ProductResponse;
import com.example.project.entity.Product;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.ProductRepository;
import com.example.project.service.ProductService;
import com.example.project.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        String sku = productRequest.getSku();
        if (CommonUtil.isNullOrEmpty(sku)) {
            sku = CommonUtil.generateUniqueSku(productRequest.getCategory());
        } else if (productRepository.existsBySku(sku)) {
            throw new IllegalArgumentException("Product with SKU already exists: " + sku);
        }

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setScientificName(productRequest.getScientificName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setRentalPrice(productRequest.getRentalPrice());
        product.setInstallationCharge(productRequest.getInstallationCharge());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        product.setSku(sku);
        product.setSunlightRequirement(productRequest.getSunlightRequirement());
        product.setWaterRequirement(productRequest.getWaterRequirement());
        product.setMaintenanceLevel(productRequest.getMaintenanceLevel());
        product.setIndoor(productRequest.isIndoor());
        product.setActive(productRequest.isActive());

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String query) {
        return productRepository.searchProducts(query).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (productRequest.getSku() != null && !productRequest.getSku().equals(product.getSku())) {
            if (productRepository.existsBySku(productRequest.getSku())) {
                throw new IllegalArgumentException("Product with SKU already exists: " + productRequest.getSku());
            }
            product.setSku(productRequest.getSku());
        }

        product.setName(productRequest.getName());
        product.setScientificName(productRequest.getScientificName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setRentalPrice(productRequest.getRentalPrice());
        product.setInstallationCharge(productRequest.getInstallationCharge());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        product.setSunlightRequirement(productRequest.getSunlightRequirement());
        product.setWaterRequirement(productRequest.getWaterRequirement());
        product.setMaintenanceLevel(productRequest.getMaintenanceLevel());
        product.setIndoor(productRequest.isIndoor());
        product.setActive(productRequest.isActive());

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getScientificName(),
                product.getDescription(),
                product.getPrice(),
                product.getRentalPrice(),
                product.getInstallationCharge(),
                product.getQuantity(),
                product.getCategory(),
                product.getSku(),
                product.getSunlightRequirement(),
                product.getWaterRequirement(),
                product.getMaintenanceLevel(),
                product.isIndoor(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
