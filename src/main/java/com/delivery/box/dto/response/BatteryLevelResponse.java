package com.delivery.box.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatteryLevelResponse {
    private String txref;
    private Integer batteryCapacity;
}