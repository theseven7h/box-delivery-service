package com.delivery.box.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadItemsRequest {
    @NotEmpty(message = "Item codes list cannot be empty")
    private List<String> itemCodes;
}