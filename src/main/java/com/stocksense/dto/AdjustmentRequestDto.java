package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdjustmentRequestDto {
    @NotNull @JsonProperty("product_id") private Long productId;
    @NotNull @JsonProperty("location_id") private Long locationId;
    @NotNull @Min(0) @JsonProperty("counted_quantity") private Integer countedQuantity;

    public Long getProductId() { return productId; }
    public Long getLocationId() { return locationId; }
    public Integer getCountedQuantity() { return countedQuantity; }
}
