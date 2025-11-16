package com.delivery.box.config;

import com.delivery.box.entity.Box;
import com.delivery.box.entity.BoxState;
import com.delivery.box.entity.Item;
import com.delivery.box.repository.BoxRepository;
import com.delivery.box.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) throws Exception {
        loadItems();
        loadBoxes();
        log.info("Sample data loaded successfully");
    }

    private void loadItems() {
        if (itemRepository.count() == 0) {
            Item item1 = new Item(null, "Paracetamol", 50.0, "MED_001");
            Item item2 = new Item(null, "Ibuprofen", 75.0, "MED_002");
            Item item3 = new Item(null, "Bandage", 30.0, "MED_003");
            Item item4 = new Item(null, "Thermometer", 100.0, "MED_004");
            Item item5 = new Item(null, "Antiseptic", 120.0, "MED_005");
            Item item6 = new Item(null, "Face-Mask", 25.0, "MED_006");
            Item item7 = new Item(null, "Hand-Sanitizer", 150.0, "MED_007");
            Item item8 = new Item(null, "Vitamin-C", 80.0, "MED_008");

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);
            itemRepository.save(item4);
            itemRepository.save(item5);
            itemRepository.save(item6);
            itemRepository.save(item7);
            itemRepository.save(item8);

            log.info("Loaded {} items", itemRepository.count());
        }
    }

    private void loadBoxes() {
        if (boxRepository.count() == 0) {
            Box box1 = new Box(null, "BOX001", 500.0, 100, BoxState.IDLE, null);
            Box box2 = new Box(null, "BOX002", 400.0, 85, BoxState.IDLE, null);
            Box box3 = new Box(null, "BOX003", 300.0, 50, BoxState.IDLE, null);
            Box box4 = new Box(null, "BOX004", 450.0, 20, BoxState.IDLE, null);
            Box box5 = new Box(null, "BOX005", 500.0, 15, BoxState.IDLE, null);

            boxRepository.save(box1);
            boxRepository.save(box2);
            boxRepository.save(box3);
            boxRepository.save(box4);
            boxRepository.save(box5);

            log.info("Loaded {} boxes", boxRepository.count());
        }
    }
}