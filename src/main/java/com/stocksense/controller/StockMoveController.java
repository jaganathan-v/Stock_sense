package com.stocksense.controller;

import com.stocksense.dto.DeliveryRequestDto;
import com.stocksense.dto.TransferRequestDto;
import com.stocksense.dto.AdjustmentRequestDto;
import com.stocksense.dto.ReceiptRequestDto;
import com.stocksense.dto.StockMoveResponseDto;
import com.stocksense.service.StockMoveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moves")
@CrossOrigin(origins = "*")
public class StockMoveController {

    private final StockMoveService stockMoveService;

    public StockMoveController(StockMoveService stockMoveService) {
        this.stockMoveService = stockMoveService;
    }

    @PostMapping("/receipt")
    public ResponseEntity<StockMoveResponseDto> recordReceipt(@Valid @RequestBody ReceiptRequestDto dto) {
        StockMoveResponseDto move = stockMoveService.recordReceipt(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(move);
    }

    @PostMapping("/delivery")
    public ResponseEntity<StockMoveResponseDto> recordDelivery(@Valid @RequestBody DeliveryRequestDto dto) {
        StockMoveResponseDto move = stockMoveService.recordDelivery(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(move);
    }

    @PostMapping("/receipt/draft")
    public ResponseEntity<StockMoveResponseDto> saveReceiptDraft(@Valid @RequestBody ReceiptRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockMoveService.saveReceiptDraft(dto));
    }

    @PostMapping("/delivery/draft")
    public ResponseEntity<StockMoveResponseDto> saveDeliveryDraft(@Valid @RequestBody DeliveryRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockMoveService.saveDeliveryDraft(dto));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<StockMoveResponseDto>> getPendingMoves() {
        return ResponseEntity.ok(stockMoveService.getPendingMoves());
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<StockMoveResponseDto> confirmDraft(@PathVariable Long id) {
        return ResponseEntity.ok(stockMoveService.confirmDraft(id));
    }

    @DeleteMapping("/{id}/draft")
    public ResponseEntity<Void> discardDraft(@PathVariable Long id) {
        stockMoveService.discardDraft(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<StockMoveResponseDto>> recordTransfer(@Valid @RequestBody TransferRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockMoveService.recordTransfer(dto));
    }

    @PostMapping("/adjustment")
    public ResponseEntity<StockMoveResponseDto> recordAdjustment(@Valid @RequestBody AdjustmentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockMoveService.recordAdjustment(dto));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<StockMoveResponseDto>> getRecentMoves(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<StockMoveResponseDto> recent = stockMoveService.getRecentMoves(type, limit);
        return ResponseEntity.ok(recent);
    }

    @GetMapping("/all")
    public ResponseEntity<List<StockMoveResponseDto>> getAllMoves() {
        List<StockMoveResponseDto> all = stockMoveService.getAllMoves();
        return ResponseEntity.ok(all);
    }
}
