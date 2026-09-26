package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TransferRequestDto {
    @NotNull @JsonProperty("product_id") private Long productId;
    @NotNull @JsonProperty("source_location_id") private Long sourceLocationId;
    @NotNull @JsonProperty("destination_location_id") private Long destinationLocationId;
    @NotNull @Min(1) private Integer quantity;

    public Long getProductId() { return productId; }
    public Long getSourceLocationId() { return sourceLocationId; }
    public Long getDestinationLocationId() { return destinationLocationId; }
    public Integer getQuantity() { return quantity; }
}
