package com.stocksense.service;

import com.stocksense.dto.DashboardResponseDto;
import com.stocksense.dto.LowStockItemDto;
import com.stocksense.dto.ProductResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    public static final int LOW_STOCK_THRESHOLD = 10;

    private final ProductService productService;

    public DashboardService(ProductService productService) {
        this.productService = productService;
    }

    public DashboardResponseDto getDashboard() {
        List<ProductResponseDto> allProducts = productService.getAllProducts();

        List<LowStockItemDto> lowStockItems = allProducts.stream()
                .filter(p -> p.getCurrentStock() < LOW_STOCK_THRESHOLD)
                .sorted(Comparator.comparingInt(ProductResponseDto::getCurrentStock))
                .map(p -> new LowStockItemDto(
                        p.getId(),
                        p.getName(),
                        p.getSku(),
                        p.getCurrentStock(),
                        p.getUnitOfMeasure()
                ))
                .collect(Collectors.toList());

        return new DashboardResponseDto(
                allProducts.size(),
                LOW_STOCK_THRESHOLD,
                lowStockItems
        );
    }
}
