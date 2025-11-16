package com.delivery.box.controller;

import com.delivery.box.dto.request.BoxRequest;
import com.delivery.box.dto.request.LoadItemsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BoxControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBox_Success() throws Exception {
        BoxRequest request = new BoxRequest("BOX888", 500.0, 80);

        mockMvc.perform(post("/api/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.txref").value("BOX888"))
                .andExpect(jsonPath("$.weightLimit").value(500.0))
                .andExpect(jsonPath("$.batteryCapacity").value(80))
                .andExpect(jsonPath("$.state").value("IDLE"));
    }

    @Test
    void createBox_ValidationError_TxrefTooLong() throws Exception {
        BoxRequest request = new BoxRequest("BOX123456789012345678901", 500.0, 80);

        mockMvc.perform(post("/api/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void createBox_ValidationError_WeightTooHigh() throws Exception {
        BoxRequest request = new BoxRequest("BOX777", 600.0, 80);

        mockMvc.perform(post("/api/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBox_ValidationError_InvalidBatteryCapacity() throws Exception {
        BoxRequest request = new BoxRequest("BOX777", 500.0, 150);

        mockMvc.perform(post("/api/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loadBox_Success() throws Exception {
        LoadItemsRequest request = new LoadItemsRequest(List.of("MED_001", "MED_002"));

        mockMvc.perform(post("/api/boxes/BOX001/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("LOADED"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.currentWeight").value(125.0))
                .andExpect(jsonPath("$.remainingCapacity").value(375.0));
    }

    @Test
    void loadBox_LowBattery_Returns400() throws Exception {
        LoadItemsRequest request = new LoadItemsRequest(List.of("MED_001"));

        mockMvc.perform(post("/api/boxes/BOX004/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("battery level below 25%")));
    }

    @Test
    void loadBox_WeightExceeded_Returns400() throws Exception {
        LoadItemsRequest request = new LoadItemsRequest(List.of("MED_009"));

        mockMvc.perform(post("/api/boxes/BOX003/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Weight limit exceeded")));
    }

    @Test
    void loadBox_BoxNotFound_Returns404() throws Exception {
        LoadItemsRequest request = new LoadItemsRequest(List.of("MED_001"));

        mockMvc.perform(post("/api/boxes/BOXNOTEXIST/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void loadBox_ItemNotFound_Returns404() throws Exception {
        LoadItemsRequest request = new LoadItemsRequest(List.of("MED_999"));

        mockMvc.perform(post("/api/boxes/BOX001/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void getLoadedItems_Success() throws Exception {
        mockMvc.perform(get("/api/boxes/BOX006/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].code", hasItems("MED_001", "MED_002", "MED_003")));
    }

    @Test
    void getLoadedItems_EmptyBox_ReturnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/boxes/BOX001/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAvailableBoxes_Success() throws Exception {
        mockMvc.perform(get("/api/boxes/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[*].state", everyItem(is("IDLE"))))
                .andExpect(jsonPath("$[*].batteryCapacity", everyItem(greaterThanOrEqualTo(25))));
    }

    @Test
    void getBatteryLevel_Success() throws Exception {
        mockMvc.perform(get("/api/boxes/BOX001/battery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.txref").value("BOX001"))
                .andExpect(jsonPath("$.batteryCapacity").value(100));
    }

    @Test
    void getBatteryLevel_BoxNotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/boxes/BOXNOTEXIST/battery"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBox_Success() throws Exception {
        mockMvc.perform(get("/api/boxes/BOX001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.txref").value("BOX001"))
                .andExpect(jsonPath("$.weightLimit").value(500.0))
                .andExpect(jsonPath("$.batteryCapacity").value(100))
                .andExpect(jsonPath("$.state").value("IDLE"));
    }

    @Test
    void getBox_WithLoadedItems_Success() throws Exception {
        mockMvc.perform(get("/api/boxes/BOX006"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.txref").value("BOX006"))
                .andExpect(jsonPath("$.state").value("LOADED"))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.currentWeight").value(155.0))
                .andExpect(jsonPath("$.remainingCapacity").value(45.0));
    }
}