package com.delivery.box.service;

import com.delivery.box.dto.request.ItemRequest;
import com.delivery.box.dto.response.ItemResponse;
import com.delivery.box.entity.Item;
import com.delivery.box.exception.InvalidOperationException;
import com.delivery.box.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public ItemResponse createItem(ItemRequest request) {
        if (itemRepository.existsByCode(request.getCode())) {
            throw new InvalidOperationException("Item with code '" + request.getCode() + "' already exists");
        }

        Item item = new Item();
        item.setName(request.getName());
        item.setWeight(request.getWeight());
        item.setCode(request.getCode());

        Item savedItem = itemRepository.save(item);
        return mapToItemResponse(savedItem);
    }

    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());
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