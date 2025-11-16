package com.delivery.box.service;

import com.delivery.box.dto.request.ItemRequest;
import com.delivery.box.dto.response.ItemResponse;
import com.delivery.box.exception.InvalidOperationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Test
    void createItem_Success() {
        ItemRequest request = new ItemRequest("Aspirin", 60.0, "MED_100");

        ItemResponse response = itemService.createItem(request);

        assertNotNull(response);
        assertEquals("Aspirin", response.getName());
        assertEquals(60.0, response.getWeight());
        assertEquals("MED_100", response.getCode());
    }

    @Test
    void createItem_DuplicateCode_ThrowsException() {
        ItemRequest request = new ItemRequest("DuplicateItem", 50.0, "MED_001");

        assertThrows(InvalidOperationException.class, () -> itemService.createItem(request));
    }

    @Test
    void getAllItems_ReturnsPreloadedData() {
        List<ItemResponse> items = itemService.getAllItems();

        assertNotNull(items);
        assertTrue(items.size() >= 9); // At least 9 preloaded items

        // Verify some specific items exist
        List<String> itemCodes = items.stream()
                .map(ItemResponse::getCode)
                .toList();

        assertTrue(itemCodes.contains("MED_001"));
        assertTrue(itemCodes.contains("MED_002"));
        assertTrue(itemCodes.contains("MED_003"));
    }

    @Test
    void createItem_WithSpecialCharacters_Success() {
        ItemRequest request = new ItemRequest("Test-Item_123", 45.0, "TEST_ITEM_001");

        ItemResponse response = itemService.createItem(request);

        assertNotNull(response);
        assertEquals("Test-Item_123", response.getName());
        assertEquals("TEST_ITEM_001", response.getCode());
    }
}