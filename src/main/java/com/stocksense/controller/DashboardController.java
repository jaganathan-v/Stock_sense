package com.stocksense.controller;

import com.stocksense.dto.DashboardResponseDto;
import com.stocksense.service.DashboardService;
import com.stocksense.service.InventoryPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.time.LocalDate;

@RestController
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final InventoryPdfService inventoryPdfService;

    public DashboardController(DashboardService dashboardService, InventoryPdfService inventoryPdfService) {
        this.dashboardService = dashboardService;
        this.inventoryPdfService = inventoryPdfService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponseDto> getDashboard() {
        DashboardResponseDto dashboard = dashboardService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping(value = "/reports/inventory-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getInventoryPdf() {
        String filename = "stocksense-inventory-report-" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(inventoryPdfService.generate());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "service", "StockSense API (Spring Boot)"
        ));
    }
}
