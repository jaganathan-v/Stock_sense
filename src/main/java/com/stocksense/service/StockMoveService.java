package com.stocksense.service;

import com.stocksense.dto.DeliveryRequestDto;
import com.stocksense.dto.TransferRequestDto;
import com.stocksense.dto.AdjustmentRequestDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
     * Records a stock receipt: creates an immutable positive StockMove entry,
     * associating location and optional supplier name.
     */
    public StockMoveResponseDto recordReceipt(ReceiptRequestDto dto) {
        Product product = productService.getProductEntity(dto.getProductId());
        Location location = resolveLocation(dto.getLocationId());

        String supplierName = (dto.getSupplierName() != null && !dto.getSupplierName().isBlank())
                ? dto.getSupplierName().trim()
                : null;

        StockMove move = new StockMove(
                product,
                location,
                dto.getQuantity(), // Positive quantity change
                MoveType.RECEIPT,
                (dto.getNote() != null && !dto.getNote().isBlank()) ? dto.getNote().trim() : "Stock receipt",
                supplierName
        );

        StockMove saved = stockMoveRepository.save(move);
        return mapToDto(saved);
    }

    /**
     * Records a stock delivery: validates sufficient stock exists at the specified location first,
     * then creates an immutable negative StockMove entry.
     */
    public StockMoveResponseDto recordDelivery(DeliveryRequestDto dto) {
        Product product = productService.getProductEntity(dto.getProductId());
        Location location = resolveLocation(dto.getLocationId());

        int availableAtLocation = stockMoveRepository.computeStockByProductAndLocation(product.getId(), location.getId());

        if (dto.getQuantity() > availableAtLocation) {
            throw new InsufficientStockException(
                    "Insufficient stock for '" + product.getName() + "' at '" + location.getName() + "'. " +
                    "Available: " + availableAtLocation + " " + product.getUnitOfMeasure() + ", " +
                    "Requested: " + dto.getQuantity() + " " + product.getUnitOfMeasure() + "."
            );
        }

        StockMove move = new StockMove(
                product,
                location,
                -dto.getQuantity(), // Negative quantity change
                MoveType.DELIVERY,
                (dto.getNote() != null && !dto.getNote().isBlank()) ? dto.getNote().trim() : "Stock delivery",
                null
        );

        StockMove saved = stockMoveRepository.save(move);
        return mapToDto(saved);
    }

    public List<StockMoveResponseDto> recordTransfer(TransferRequestDto dto) {
        Product product = productService.getProductEntity(dto.getProductId());
        Location source = resolveLocation(dto.getSourceLocationId());
        Location destination = resolveLocation(dto.getDestinationLocationId());
        if (source.getId().equals(destination.getId())) {
            throw new IllegalArgumentException("Source and destination locations must be different.");
        }
        int available = stockMoveRepository.computeStockByProductAndLocation(product.getId(), source.getId());
        if (dto.getQuantity() > available) {
            throw new InsufficientStockException("Insufficient stock for '" + product.getName() + "' at '" + source.getName() + "'. Available: " + available + ", requested: " + dto.getQuantity() + ".");
        }
        String transferRef = UUID.randomUUID().toString();
        StockMove out = new StockMove(product, source, -dto.getQuantity(), MoveType.TRANSFER,
                "Transfer " + transferRef + " to " + destination.getName());
        StockMove in = new StockMove(product, destination, dto.getQuantity(), MoveType.TRANSFER,
                "Transfer " + transferRef + " from " + source.getName());
        return List.of(mapToDto(stockMoveRepository.save(out)), mapToDto(stockMoveRepository.save(in)));
    }

    public StockMoveResponseDto recordAdjustment(AdjustmentRequestDto dto) {
        Product product = productService.getProductEntity(dto.getProductId());
        Location location = resolveLocation(dto.getLocationId());
        int current = stockMoveRepository.computeStockByProductAndLocation(product.getId(), location.getId());
        int difference = dto.getCountedQuantity() - current;
        StockMove move = new StockMove(product, location, difference, MoveType.ADJUSTMENT,
                "Stock count adjustment (counted " + dto.getCountedQuantity() + ", was " + current + ")");
        return mapToDto(stockMoveRepository.save(move));
    }

    @Transactional(readOnly = true)
    public List<StockMoveResponseDto> getRecentMoves(String type, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        PageRequest pageRequest = PageRequest.of(0, safeLimit);

        List<StockMove> moves;
        if (type != null && !type.isBlank()) {
            MoveType moveType = MoveType.fromValue(type.trim());
            moves = stockMoveRepository.findRecentWithDetailsByMoveType(moveType, pageRequest);
        } else {
            moves = stockMoveRepository.findRecentWithDetails(pageRequest);
        }

        return moves.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMoveResponseDto> getAllMoves() {
        return stockMoveRepository.findAllWithDetails().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private StockMoveResponseDto mapToDto(StockMove move) {
        return new StockMoveResponseDto(
                move.getId(),
                move.getProduct().getId(),
                move.getProduct().getName(),
                move.getProduct().getSku(),
                move.getLocation().getId(),
                move.getLocation().getName(),
                move.getQuantityChange(),
                move.getMoveType().getValue(),
                move.getTimestamp(),
                move.getNote(),
                move.getSupplierName(),
                move.getProduct().getUnitOfMeasure()
        );
    }

    private Location resolveLocation(Long locationId) {
        if (locationId != null) {
            return locationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
        }
        return getDefaultLocation();
    }

    private Location getDefaultLocation() {
        return locationRepository.findByName("Main Warehouse")
                .orElseGet(() -> locationRepository.save(new Location("Main Warehouse")));
    }
}
