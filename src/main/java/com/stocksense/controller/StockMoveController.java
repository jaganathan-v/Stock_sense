package com.stocksense.controller;

import com.stocksense.dto.DeliveryRequestDto;
import com.stocksense.dto.ReceiptRequestDto;
import com.stocksense.dto.StockMoveResponseDto;
import com.stocksense.service.StockMoveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
