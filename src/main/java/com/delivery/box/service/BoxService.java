package com.delivery.box.service;

import com.delivery.box.dto.request.BoxRequest;
import com.delivery.box.dto.request.LoadItemsRequest;
import com.delivery.box.dto.response.BatteryLevelResponse;
import com.delivery.box.dto.response.BoxResponse;
import com.delivery.box.dto.response.ItemResponse;
import com.delivery.box.entity.Box;
import com.delivery.box.entity.BoxState;
import com.delivery.box.entity.Item;
import com.delivery.box.exception.BoxNotFoundException;
import com.delivery.box.exception.InvalidOperationException;
import com.delivery.box.exception.ItemNotFoundException;
import com.delivery.box.repository.BoxRepository;
import com.delivery.box.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoxService {

    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BoxResponse createBox(BoxRequest request) {
        if (boxRepository.existsByTxref(request.getTxref())) {
            throw new InvalidOperationException("Box with txref '" + request.getTxref() + "' already exists");
        }

        Box box = new Box();
        box.setTxref(request.getTxref());
        box.setWeightLimit(request.getWeightLimit());
        box.setBatteryCapacity(request.getBatteryCapacity());
        box.setState(BoxState.IDLE);

        Box savedBox = boxRepository.save(box);
        return mapToBoxResponse(savedBox);
    }

    @Transactional
    public BoxResponse loadBox(String txref, LoadItemsRequest request) {
        Box box = boxRepository.findByTxref(txref)
                .orElseThrow(() -> new BoxNotFoundException("Box with txref '" + txref + "' not found"));

        if (box.getBatteryCapacity() < 25) {
            throw new InvalidOperationException("Cannot load box with battery level below 25%. Current level: " + box.getBatteryCapacity() + "%");
        }

        if (box.getState() != BoxState.IDLE && box.getState() != BoxState.LOADING) {
            throw new InvalidOperationException("Box is not available for loading. Current state: " + box.getState());
        }

        box.setState(BoxState.LOADING);

        List<Item> itemsToLoad = request.getItemCodes().stream()
                .map(code -> itemRepository.findByCode(code)
                        .orElseThrow(() -> new ItemNotFoundException("Item with code '" + code + "' not found")))
                .collect(Collectors.toList());

        double totalWeight = itemsToLoad.stream()
                .mapToDouble(Item::getWeight)
                .sum();

        double currentWeight = calculateCurrentWeight(box);
        double newTotalWeight = currentWeight + totalWeight;

        if (newTotalWeight > box.getWeightLimit()) {
            throw new InvalidOperationException(
                    String.format("Cannot load items. Weight limit exceeded. Current: %.2fgr, Adding: %.2fgr, Limit: %.2fgr",
                            currentWeight, totalWeight, box.getWeightLimit())
            );
        }

        box.getItems().addAll(itemsToLoad);

        box.setState(BoxState.LOADED);

        Box savedBox = boxRepository.save(box);
        return mapToBoxResponse(savedBox);
    }

    public List<ItemResponse> getLoadedItems(String txref) {
        Box box = boxRepository.findByTxref(txref)
                .orElseThrow(() -> new BoxNotFoundException("Box with txref '" + txref + "' not found"));

        return box.getItems().stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BoxResponse> getAvailableBoxes() {
        List<Box> boxes = boxRepository.findAvailableBoxesForLoading(BoxState.IDLE);

        return boxes.stream()
                .filter(box -> calculateRemainingCapacity(box) > 0)
                .map(this::mapToBoxResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BatteryLevelResponse getBatteryLevel(String txref) {
        Box box = boxRepository.findByTxref(txref)
                .orElseThrow(() -> new BoxNotFoundException("Box with txref '" + txref + "' not found"));

        return new BatteryLevelResponse(box.getTxref(), box.getBatteryCapacity());
    }

    @Transactional(readOnly = true)
    public BoxResponse getBox(String txref) {
        Box box = boxRepository.findByTxref(txref)
                .orElseThrow(() -> new BoxNotFoundException("Box with txref '" + txref + "' not found"));

        return mapToBoxResponse(box);
    }

    private BoxResponse mapToBoxResponse(Box box) {
        List<ItemResponse> items = box.getItems().stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());

        double currentWeight = calculateCurrentWeight(box);
        double remainingCapacity = calculateRemainingCapacity(box);

        return new BoxResponse(
                box.getId(),
                box.getTxref(),
                box.getWeightLimit(),
                box.getBatteryCapacity(),
                box.getState(),
                currentWeight,
                remainingCapacity,
                items
        );
    }

    private Double calculateCurrentWeight(Box box) {
        return box.getItems().stream()
                .mapToDouble(Item::getWeight)
                .sum();
    }

    private Double calculateRemainingCapacity(Box box) {
        return box.getWeightLimit() - calculateCurrentWeight(box);
    }

    private ItemResponse mapToItemResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getWeight(),
                item.getCode()
        );
    }
}