package com.stocksense.service;

import com.stocksense.dto.DeliveryRequestDto;
import com.stocksense.dto.ReceiptRequestDto;
import com.stocksense.dto.StockMoveResponseDto;
import com.stocksense.exception.InsufficientStockException;
import com.stocksense.exception.ResourceNotFoundException;
import com.stocksense.model.Location;
import com.stocksense.model.MoveType;
import com.stocksense.model.Product;
import com.stocksense.model.StockMove;
import com.stocksense.repository.LocationRepository;
import com.stocksense.repository.StockMoveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockMoveService {

    private final StockMoveRepository stockMoveRepository;
    private final ProductService productService;
    private final LocationRepository locationRepository;

    public StockMoveService(StockMoveRepository stockMoveRepository,
                            ProductService productService,
                            LocationRepository locationRepository) {
        this.stockMoveRepository = stockMoveRepository;
        this.productService = productService;
        this.locationRepository = locationRepository;
    }

    /**
     * Records a stock receipt: creates an immutable positive StockMove entry.
     */
    public StockMoveResponseDto recordReceipt(ReceiptRequestDto dto) {
        Product product = productService.getProductEntity(dto.getProductId());
        Location location = getDefaultLocation();

        StockMove move = new StockMove(
                product,
                location,
                dto.getQuantity(), // Positive quantity change
                MoveType.RECEIPT,
                (dto.getNote() != null && !dto.getNote().isBlank()) ? dto.getNote().trim() : "Stock receipt"
        );

        StockMove saved = stockMoveRepository.save(move);

        return new StockMoveResponseDto(
                saved.getId(),
                product.getId(),
                location.getId(),
                saved.getQuantityChange(),
                saved.getMoveType().getValue(),
                saved.getTimestamp(),
                saved.getNote()
        );
    }

    /**
     * Records a stock delivery: validates sufficient stock exists first,
     * then creates an immutable negative StockMove entry.
     */
    public StockMoveResponseDto recordDelivery(DeliveryRequestDto dto) {
        Product product = productService.getProductEntity(dto.getProductId());
        int currentStock = productService.computeCurrentStock(product.getId());

        if (dto.getQuantity() > currentStock) {
            throw new InsufficientStockException(
                    "Insufficient stock for '" + product.getName() + "'. " +
                    "Available: " + currentStock + " " + product.getUnitOfMeasure() + ", " +
                    "Requested: " + dto.getQuantity() + " " + product.getUnitOfMeasure() + "."
            );
        }

        Location location = getDefaultLocation();

        StockMove move = new StockMove(
                product,
                location,
                -dto.getQuantity(), // Negative quantity change
                MoveType.DELIVERY,
                (dto.getNote() != null && !dto.getNote().isBlank()) ? dto.getNote().trim() : "Stock delivery"
        );

        StockMove saved = stockMoveRepository.save(move);

        return new StockMoveResponseDto(
                saved.getId(),
                product.getId(),
                location.getId(),
                saved.getQuantityChange(),
                saved.getMoveType().getValue(),
                saved.getTimestamp(),
                saved.getNote()
        );
    }

    private Location getDefaultLocation() {
        return locationRepository.findByName("Main Warehouse")
                .orElseGet(() -> locationRepository.save(new Location("Main Warehouse")));
    }
}
