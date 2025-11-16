package com.delivery.box.controller;

import com.delivery.box.dto.request.BoxRequest;
import com.delivery.box.dto.request.LoadItemsRequest;
import com.delivery.box.dto.response.BatteryLevelResponse;
import com.delivery.box.dto.response.BoxResponse;
import com.delivery.box.dto.response.ItemResponse;
import com.delivery.box.service.BoxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boxes")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    @PostMapping
    public ResponseEntity<BoxResponse> createBox(@Valid @RequestBody BoxRequest request) {
        BoxResponse response = boxService.createBox(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{txref}/load")
    public ResponseEntity<BoxResponse> loadBox(
            @PathVariable String txref,
            @Valid @RequestBody LoadItemsRequest request) {
        BoxResponse response = boxService.loadBox(txref, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{txref}/items")
    public ResponseEntity<List<ItemResponse>> getLoadedItems(@PathVariable String txref) {
        List<ItemResponse> items = boxService.getLoadedItems(txref);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/available")
    public ResponseEntity<List<BoxResponse>> getAvailableBoxes() {
        List<BoxResponse> boxes = boxService.getAvailableBoxes();
        return ResponseEntity.ok(boxes);
    }

    @GetMapping("/{txref}/battery")
    public ResponseEntity<BatteryLevelResponse> getBatteryLevel(@PathVariable String txref) {
        BatteryLevelResponse response = boxService.getBatteryLevel(txref);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{txref}")
    public ResponseEntity<BoxResponse> getBox(@PathVariable String txref) {
        BoxResponse response = boxService.getBox(txref);
        return ResponseEntity.ok(response);
    }
}