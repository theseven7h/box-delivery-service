package com.delivery.box.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for creating a box
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoxRequest {

    @NotBlank(message = "Txref is required")
    @Size(max = 20, message = "Txref must not exceed 20 characters")
    private String txref;

    @NotNull(message = "Weight limit is required")
    @Max(value = 500, message = "Weight limit cannot exceed 500gr")
    @Min(value = 0, message = "Weight limit must be positive")
    private Double weightLimit;

    @NotNull(message = "Battery capacity is required")
    @Min(value = 0, message = "Battery capacity must be between 0 and 100")
    @Max(value = 100, message = "Battery capacity must be between 0 and 100")
    private Integer batteryCapacity;
}