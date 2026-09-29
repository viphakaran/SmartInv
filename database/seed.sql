-- Seed Data for Smart Inventory and Procurement Management System

INSERT INTO categories (id, name, description) VALUES
(1, 'Raw Materials', 'Basic raw items used in manufacturing'),
(2, 'Components', 'Engineered parts and electrical items'),
(3, 'Finished Goods', 'Assembled products ready for distribution'),
(4, 'Packaging', 'Containers, spools, and protective wrapping');

INSERT INTO users (id, username, password, name, role, email, phone, department) VALUES
(1, 'admin', 'admin123', 'Alex Vance', 'ADMIN', 'admin@smartinv.com', '+1-555-0100', 'IT Administration'),
(2, 'manager', 'mgr123', 'Sarah Jenkins', 'STORE_MANAGER', 'sarah.j@smartinv.com', '+1-555-0101', 'Operations'),
(3, 'procurement', 'proc123', 'Michael Ross', 'PROCUREMENT_OFFICER', 'michael.r@smartinv.com', '+1-555-0102', 'Procurement'),
(4, 'supplier_apex', 'sup123', 'Apex Industrial Supplies', 'SUPPLIER', 'contact@apexind.com', '+1-555-0103', 'Supply Chain');

INSERT INTO suppliers (id, name, contact_email, phone, category_id, rating, address) VALUES
(1, 'Apex Industrial Parts Ltd', 'sales@apexindustrial.com', '+1-800-555-901', 2, 4.8, '104 Logistics Way, Sector 4'),
(2, 'Global Raw Materials Co.', 'orders@globalraw.com', '+1-800-555-902', 1, 4.6, '72 Heavy Metals Ave, Zone 2'),
(3, 'PowerTech Energy Cells', 'supply@powertech.io', '+1-800-555-903', 2, 4.9, '300 Battery Blvd, Tech Park');

INSERT INTO inventory (id, name, sku, category_id, stock_qty, min_threshold, max_capacity, unit_price, unit_of_measure, status, image_url) VALUES
(1, 'Industrial Ball Bearing', 'BRG-782-X', 2, 1240, 200, 1500, 12.45, 'units', 'IN_STOCK', 'https://lh3.googleusercontent.com/aida-public/AB6AXuBKkBxcAMc5Xq2fIsjc1PXjPsTAejDw1NvyMpvjbKW4ZHR5MgT-j_14Rg4kzarAUGbmRdY9NSrerOtjW0Orap1G6xw5FiIdr1QKRHnaDtGtHoAL_dYpm22J2jFAiQaINDojv2MfvQyPiDnY8YLeizU4JFEJgkU79oXDs4_Tzt5cbN5-RjeM2WMuHPe1Y5U5FL6FVrp6GW1kSr0GK4aqVz6ojEoh_z5Y2X1uIhrZhPHPRzjuxNcjcpY'),
(2, 'Grade 8 Hex Bolt 1/2"', 'FCS-110-B', 1, 150, 500, 2000, 0.85, 'units', 'LOW_STOCK', 'https://lh3.googleusercontent.com/aida-public/AB6AXuDaNZeiw2OWhd3Q3HN2ZrKOOw1JFfgo-pYSECa8ghDCSAbebXb4oM8RIQ_gzpoXHuyH71XUPDa6pKN-ErmgrfP37P5poG98G9tCOh8Dpe-avGJFKqSEkKnh4i4G4w-129BWysa99pw-u2Ra3namXZgjLfw2_-4CKqq_TRJwTzmZnCFCFfAxWE7kri0g7JK-luLspKIxPAuykC32Lx1T9iNX6oZX8YqLfpOcHjJ8z9tNcU6NkpyoZG8'),
(3, 'Lithium-Ion Cell 18650', 'PWR-881-L', 2, 0, 100, 500, 4.50, 'units', 'OUT_OF_STOCK', ''),
(4, 'Copper Wire 2mm (Spool)', 'WRE-200-C', 1, 12, 50, 250, 89.99, 'spools', 'LOW_STOCK', 'https://lh3.googleusercontent.com/aida-public/AB6AXuBKkBxcAMc5Xq2fIsjc1PXjPsTAejDw1NvyMpvjbKW4ZHR5MgT-j_14Rg4kzarAUGbmRdY9NSrerOtjW0Orap1G6xw5FiIdr1QKRHnaDtGtHoAL_dYpm22J2jFAiQaINDojv2MfvQyPiDnY8YLeizU4JFEJgkU79oXDs4_Tzt5cbN5-RjeM2WMuHPe1Y5U5FL6FVrp6GW1kSr0GK4aqVz6ojEoh_z5Y2X1uIhrZhPHPRzjuxNcjcpY'),
(5, 'Industrial Lub. X-200', 'LUB-X20-D', 1, 45, 100, 400, 65.00, 'drums', 'LOW_STOCK', 'https://lh3.googleusercontent.com/aida-public/AB6AXuDaNZeiw2OWhd3Q3HN2ZrKOOw1JFfgo-pYSECa8ghDCSAbebXb4oM8RIQ_gzpoXHuyH71XUPDa6pKN-ErmgrfP37P5poG98G9tCOh8Dpe-avGJFKqSEkKnh4i4G4w-129BWysa99pw-u2Ra3namXZgjLfw2_-4CKqq_TRJwTzmZnCFCFfAxWE7kri0g7JK-luLspKIxPAuykC32Lx1T9iNX6oZX8YqLfpOcHjJ8z9tNcU6NkpyoZG8');

INSERT INTO purchase_requests (id, req_code, item_id, item_name, quantity, department, priority, notes, status, requested_by, created_at) VALUES
(1, 'REQ-4092', 1, 'Industrial Bearings XZ-9', 250, 'Manufacturing', 'Standard', 'Stock replenishment for Q4 production', 'PENDING_REVIEW', 2, NOW() - INTERVAL 1 HOUR),
(2, 'REQ-4091', 4, 'Safety Harness Pro', 50, 'Operations', 'High', 'Required for line maintenance team', 'PROCESSING', 2, NOW() - INTERVAL 1 DAY),
(3, 'REQ-4088', 3, 'Lithium Battery Packs', 500, 'Maintenance', 'High', 'Emergency replacement request', 'DENIED', 2, NOW() - INTERVAL 3 DAY),
(4, 'REQ-4085', 5, 'Hydraulic Fluid 50G', 10, 'Manufacturing', 'Standard', 'Monthly routine refill', 'DELIVERED', 2, NOW() - INTERVAL 5 DAY);

INSERT INTO orders (id, order_no, request_id, supplier_id, total_amount, status, estimated_delivery, notes, created_at) VALUES
(1, 'PO-2026-001', 2, 1, 3112.50, 'SHIPPED', CURRENT_DATE + INTERVAL 2 DAY, 'In transit via Express Logistics', NOW() - INTERVAL 1 DAY),
(2, 'PO-2026-002', 4, 2, 650.00, 'DELIVERED', CURRENT_DATE - INTERVAL 1 DAY, 'Received at Dock B', NOW() - INTERVAL 5 DAY);

INSERT INTO notifications (id, type, recipient_role, recipient_email, message, channel, status, sent_at) VALUES
(1, 'LOW_STOCK', 'STORE_MANAGER', 'sarah.j@smartinv.com', 'ALERT: Copper Wire 2mm (Spool) reached critical low level (12 units remaining).', 'EMAIL', 'SENT', NOW() - INTERVAL 2 HOUR),
(2, 'LOW_STOCK', 'PROCUREMENT_OFFICER', 'michael.r@smartinv.com', 'ALERT: Grade 8 Hex Bolt 1/2" reached low stock threshold (150/2000).', 'SMS', 'SENT', NOW() - INTERVAL 4 HOUR),
(3, 'ORDER_STATUS', 'STORE_MANAGER', 'sarah.j@smartinv.com', 'UPDATE: Purchase Order PO-2026-001 has been marked SHIPPED by Apex Industrial Parts Ltd.', 'EMAIL', 'SENT', NOW() - INTERVAL 1 DAY);
