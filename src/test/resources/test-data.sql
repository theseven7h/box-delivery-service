-- Test Data for Integration Tests

-- Insert Items
INSERT INTO items (id, name, weight, code) VALUES (1, 'Paracetamol', 50.0, 'MED_001');
INSERT INTO items (id, name, weight, code) VALUES (2, 'Ibuprofen', 75.0, 'MED_002');
INSERT INTO items (id, name, weight, code) VALUES (3, 'Bandage', 30.0, 'MED_003');
INSERT INTO items (id, name, weight, code) VALUES (4, 'Thermometer', 100.0, 'MED_004');
INSERT INTO items (id, name, weight, code) VALUES (5, 'Antiseptic', 120.0, 'MED_005');
INSERT INTO items (id, name, weight, code) VALUES (6, 'Face-Mask', 25.0, 'MED_006');
INSERT INTO items (id, name, weight, code) VALUES (7, 'Hand-Sanitizer', 150.0, 'MED_007');
INSERT INTO items (id, name, weight, code) VALUES (8, 'Vitamin-C', 80.0, 'MED_008');
INSERT INTO items (id, name, weight, code) VALUES (9, 'Heavy-Item', 450.0, 'MED_009');

-- Insert Boxes
INSERT INTO boxes (id, txref, weight_limit, battery_capacity, state) VALUES (1, 'BOX001', 500.0, 100, 'IDLE');
INSERT INTO boxes (id, txref, weight_limit, battery_capacity, state) VALUES (2, 'BOX002', 400.0, 85, 'IDLE');
INSERT INTO boxes (id, txref, weight_limit, battery_capacity, state) VALUES (3, 'BOX003', 300.0, 50, 'IDLE');
INSERT INTO boxes (id, txref, weight_limit, battery_capacity, state) VALUES (4, 'BOX004', 450.0, 20, 'IDLE');
INSERT INTO boxes (id, txref, weight_limit, battery_capacity, state) VALUES (5, 'BOX005', 500.0, 15, 'IDLE');
INSERT INTO boxes (id, txref, weight_limit, battery_capacity, state) VALUES (6, 'BOX006', 200.0, 100, 'LOADED');
INSERT INTO boxes (id, txref, weight_limit, battery_capacity, state) VALUES (7, 'BOX007', 500.0, 100, 'DELIVERING');

-- Insert Box-Item relationships (BOX006 already has items loaded)
INSERT INTO box_items (box_id, item_id) VALUES (6, 1);
INSERT INTO box_items (box_id, item_id) VALUES (6, 2);
INSERT INTO box_items (box_id, item_id) VALUES (6, 3);

-- Reset sequences to avoid ID conflicts with auto-generated IDs
ALTER TABLE items ALTER COLUMN id RESTART WITH 100;
ALTER TABLE boxes ALTER COLUMN id RESTART WITH 100;