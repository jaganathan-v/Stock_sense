package com.stocksense.service;

import com.stocksense.dto.ProductCreateDto;
import com.stocksense.dto.ProductResponseDto;
import com.stocksense.exception.ConflictException;
import com.stocksense.exception.ResourceNotFoundException;
import com.stocksense.model.Product;
import com.stocksense.repository.ProductLocationStockProjection;
import com.stocksense.repository.ProductRepository;
import com.stocksense.repository.ProductStockProjection;
import com.stocksense.repository.StockMoveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final StockMoveRepository stockMoveRepository;

    public ProductService(ProductRepository productRepository, StockMoveRepository stockMoveRepository) {
        this.productRepository = productRepository;
        this.stockMoveRepository = stockMoveRepository;
    }

    @Transactional
    public ProductResponseDto createProduct(ProductCreateDto dto) {
        if (productRepository.existsBySkuIgnoreCase(dto.getSku())) {
            throw new ConflictException("A product with SKU '" + dto.getSku() + "' already exists.");
        }

        Product product = new Product(
                dto.getName(),
                dto.getSku(),
                dto.getCategory(),
                dto.getUnitOfMeasure()
        );

        Product saved = productRepository.save(product);

        // Newly created product has 0 stock moves, so current stock is 0
        return new ProductResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getSku(),
                saved.getCategory(),
                saved.getUnitOfMeasure(),
                0,
                new LinkedHashMap<>()
        );
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAllByOrderByNameAsc();

        // 1. Compute total current stock per product
        List<ProductStockProjection> stockProjections = stockMoveRepository.computeAllProductStocks();
        Map<Long, Integer> stockMap = new HashMap<>();
        for (ProductStockProjection projection : stockProjections) {
            stockMap.put(projection.getProductId(), projection.getCurrentStock() != null ? projection.getCurrentStock() : 0);
        }

        // 2. Compute stock broken down by location
        List<ProductLocationStockProjection> locProjections = stockMoveRepository.computeAllProductLocationStocks();
        Map<Long, Map<String, Integer>> locStockMap = new HashMap<>();
        for (ProductLocationStockProjection lp : locProjections) {
            locStockMap.computeIfAbsent(lp.getProductId(), k -> new LinkedHashMap<>())
                    .put(lp.getLocationName(), lp.getCurrentStock() != null ? lp.getCurrentStock() : 0);
        }

        return products.stream().map(p -> new ProductResponseDto(
                p.getId(),
                p.getName(),
                p.getSku(),
                p.getCategory(),
                p.getUnitOfMeasure(),
                stockMap.getOrDefault(p.getId(), 0),
                locStockMap.getOrDefault(p.getId(), new LinkedHashMap<>())
        )).collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProductEntity(productId);
        long movesCount = stockMoveRepository.countByProductId(productId);

        if (movesCount > 0) {
            throw new ConflictException(
                    "Cannot delete product '" + product.getName() + "' because it has " + movesCount +
                    " ledger transaction(s). To preserve audit integrity, products with transaction history cannot be deleted."
            );
        }

        productRepository.delete(product);
    }

    public Product getProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id=" + productId + " not found."));
    }

    public Integer computeCurrentStock(Long productId) {
        Integer stock = stockMoveRepository.computeCurrentStockByProductId(productId);
        return stock != null ? stock : 0;
    }
}
