package com.stocksense;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StockSenseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long createdProductId;

    @Test
    @Order(1)
    void testCreateProduct() throws Exception {
        String json = """
                {
                    "name": "Ergonomic Mechanical Keyboard",
                    "sku": "KB-TEST-001",
                    "category": "Electronics",
                    "unit_of_measure": "units"
                }
                """;

        MvcResult result = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ergonomic Mechanical Keyboard"))
                .andExpect(jsonPath("$.sku").value("KB-TEST-001"))
                .andExpect(jsonPath("$.current_stock").value(0))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        createdProductId = root.get("id").asLong();
        assertThat(createdProductId).isNotNull();
    }

    @Test
    @Order(2)
    void testGetProductsInitialStock() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.sku == 'KB-TEST-001')].current_stock").value(0));
    }

    @Test
    @Order(3)
    void testRecordReceipt() throws Exception {
        String json = String.format("""
                {
                    "product_id": %d,
                    "quantity": 50,
                    "note": "Initial test batch received"
                }
                """, createdProductId);

        mockMvc.perform(post("/moves/receipt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product_id").value(createdProductId))
                .andExpect(jsonPath("$.quantity_change").value(50))
                .andExpect(jsonPath("$.move_type").value("receipt"));
    }

    @Test
    @Order(4)
    void testGetProductsAfterReceipt() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.sku == 'KB-TEST-001')].current_stock").value(50));
    }

    @Test
    @Order(5)
    void testDeliverySuccess() throws Exception {
        String json = String.format("""
                {
                    "product_id": %d,
                    "quantity": 20,
                    "note": "Shipment to Customer A"
                }
                """, createdProductId);

        mockMvc.perform(post("/moves/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product_id").value(createdProductId))
                .andExpect(jsonPath("$.quantity_change").value(-20))
                .andExpect(jsonPath("$.move_type").value("delivery"));

        // Stock should now be 50 - 20 = 30
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.sku == 'KB-TEST-001')].current_stock").value(30));
    }

    @Test
    @Order(6)
    void testDeliveryRejectionWhenInsufficientStock() throws Exception {
        // Attempting to deliver 100 when only 30 available
        String json = String.format("""
                {
                    "product_id": %d,
                    "quantity": 100,
                    "note": "Over-allocation attempt"
                }
                """, createdProductId);

        mockMvc.perform(post("/moves/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").exists());

        // Verify stock is STILL 30 (not modified because the rejected delivery was never committed)
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.sku == 'KB-TEST-001')].current_stock").value(30));
    }

    @Test
    @Order(7)
    void testDashboardLowStockDetection() throws Exception {
        // Deliver 25 more units: stock drops from 30 to 5 (which is below the threshold of 10)
        String json = String.format("""
                {
                    "product_id": %d,
                    "quantity": 25,
                    "note": "Shipment causing low stock"
                }
                """, createdProductId);

        mockMvc.perform(post("/moves/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        // Verify dashboard reports low stock item
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.low_stock_threshold").value(10))
                .andExpect(jsonPath("$.low_stock_items[?(@.sku == 'KB-TEST-001')].current_stock").value(5));
    }

    @Test
    @Order(8)
    void testDuplicateSkuRejection() throws Exception {
        String json = """
                {
                    "name": "Duplicate SKU Test",
                    "sku": "KB-TEST-001",
                    "category": "Electronics",
                    "unit_of_measure": "units"
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").exists());
    }
}
