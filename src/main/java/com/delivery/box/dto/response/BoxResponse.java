package com.delivery.box.dto.response;

import com.delivery.box.entity.BoxState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoxResponse {
    private Long id;
    private String txref;
    private Double weightLimit;
    private Integer batteryCapacity;
    private BoxState state;
    private Double currentWeight;
    private Double remainingCapacity;
    private List<ItemResponse> items;
}