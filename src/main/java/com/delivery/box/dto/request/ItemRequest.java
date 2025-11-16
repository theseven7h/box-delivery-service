package com.delivery.box.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Name can only contain letters, numbers, hyphen and underscore")
    private String name;

    @NotNull(message = "Weight is required")
    @Min(value = 0, message = "Weight must be positive")
    private Double weight;

    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code can only contain uppercase letters, numbers and underscore")
    private String code;
}