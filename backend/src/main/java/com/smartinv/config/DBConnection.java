package com.smartinv.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String MYSQL_URL = System.getenv("DB_URL") != null 
        ? System.getenv("DB_URL") 
        : "jdbc:mysql://localhost:3306/smartinv_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String MYSQL_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String MYSQL_PASS = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : "root123";

    private static final String SQLITE_URL = "jdbc:sqlite:smartinv.db";
    private static boolean isSQLite = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // MySQL driver fallback
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // SQLite driver fallback
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
            isSQLite = false;
            return conn;
        } catch (Exception e) {
            isSQLite = true;
            try {
                Connection conn = DriverManager.getConnection(SQLITE_URL);
                initializeSQLiteIfNeeded(conn);
                return conn;
            } catch (Exception sqle) {
                // SQLite driver or file not accessible in standalone mode
                return null;
            }
        }
    }

    public static boolean isSQLite() {
        return isSQLite;
    }

    private static synchronized void initializeSQLiteIfNeeded(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "name TEXT, " +
                    "role TEXT, " +
                    "email TEXT, " +
                    "phone TEXT, " +
                    "department TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE, " +
                    "description TEXT);");

            stmt.execute("CREATE TABLE IF NOT EXISTS inventory (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "sku TEXT UNIQUE, " +
                    "category_id INTEGER, " +
                    "stock_qty INTEGER DEFAULT 0, " +
                    "min_threshold INTEGER DEFAULT 10, " +
                    "max_capacity INTEGER DEFAULT 1000, " +
                    "unit_price REAL DEFAULT 0.0, " +
                    "unit_of_measure TEXT DEFAULT 'units', " +
                    "status TEXT DEFAULT 'IN_STOCK', " +
                    "image_url TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            stmt.execute("CREATE TABLE IF NOT EXISTS suppliers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "contact_email TEXT, " +
                    "phone TEXT, " +
                    "category_id INTEGER, " +
                    "rating REAL DEFAULT 4.5, " +
                    "address TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            stmt.execute("CREATE TABLE IF NOT EXISTS purchase_requests (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "req_code TEXT UNIQUE, " +
                    "item_id INTEGER, " +
                    "item_name TEXT, " +
                    "quantity INTEGER, " +
                    "department TEXT, " +
                    "priority TEXT DEFAULT 'Standard', " +
                    "notes TEXT, " +
                    "status TEXT DEFAULT 'PENDING_REVIEW', " +
                    "requested_by INTEGER, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_no TEXT UNIQUE, " +
                    "request_id INTEGER, " +
                    "supplier_id INTEGER, " +
                    "total_amount REAL, " +
                    "status TEXT DEFAULT 'PENDING', " +
                    "estimated_delivery TEXT, " +
                    "notes TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT, " +
                    "recipient_role TEXT, " +
                    "recipient_email TEXT, " +
                    "message TEXT, " +
                    "channel TEXT DEFAULT 'EMAIL', " +
                    "status TEXT DEFAULT 'SENT', " +
                    "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO categories VALUES (1, 'Raw Materials', 'Basic raw items used in manufacturing');");
                stmt.execute("INSERT INTO categories VALUES (2, 'Components', 'Engineered parts and electrical items');");
                stmt.execute("INSERT INTO categories VALUES (3, 'Finished Goods', 'Assembled products ready for distribution');");

                stmt.execute("INSERT INTO users VALUES (1, 'admin', 'admin123', 'Alex Vance', 'ADMIN', 'admin@smartinv.com', '+1-555-0100', 'IT Administration', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO users VALUES (2, 'manager', 'mgr123', 'Sarah Jenkins', 'STORE_MANAGER', 'sarah.j@smartinv.com', '+1-555-0101', 'Operations', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO users VALUES (3, 'procurement', 'proc123', 'Michael Ross', 'PROCUREMENT_OFFICER', 'michael.r@smartinv.com', '+1-555-0102', 'Procurement', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO users VALUES (4, 'supplier_apex', 'sup123', 'Apex Industrial', 'SUPPLIER', 'contact@apexind.com', '+1-555-0103', 'Supply Chain', CURRENT_TIMESTAMP);");

                stmt.execute("INSERT INTO suppliers VALUES (1, 'Apex Industrial Parts Ltd', 'sales@apexindustrial.com', '+1-800-555-901', 2, 4.8, '104 Logistics Way', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO suppliers VALUES (2, 'Global Raw Materials Co.', 'orders@globalraw.com', '+1-800-555-902', 1, 4.6, '72 Heavy Metals Ave', CURRENT_TIMESTAMP);");

                stmt.execute("INSERT INTO inventory VALUES (1, 'Industrial Ball Bearing', 'BRG-782-X', 2, 1240, 200, 1500, 12.45, 'units', 'IN_STOCK', 'https://lh3.googleusercontent.com/aida-public/AB6AXuBKkBxcAMc5Xq2fIsjc1PXjPsTAejDw1NvyMpvjbKW4ZHR5MgT-j_14Rg4kzarAUGbmRdY9NSrerOtjW0Orap1G6xw5FiIdr1QKRHnaDtGtHoAL_dYpm22J2jFAiQaINDojv2MfvQyPiDnY8YLeizU4JFEJgkU79oXDs4_Tzt5cbN5-RjeM2WMuHPe1Y5U5FL6FVrp6GW1kSr0GK4aqVz6ojEoh_z5Y2X1uIhrZhPHPRzjuxNcjcpY', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO inventory VALUES (2, 'Grade 8 Hex Bolt 1/2\"', 'FCS-110-B', 1, 150, 500, 2000, 0.85, 'units', 'LOW_STOCK', 'https://lh3.googleusercontent.com/aida-public/AB6AXuDaNZeiw2OWhd3Q3HN2ZrKOOw1JFfgo-pYSECa8ghDCSAbebXb4oM8RIQ_gzpoXHuyH71XUPDa6pKN-ErmgrfP37P5poG98G9tCOh8Dpe-avGJFKqSEkKnh4i4G4w-129BWysa99pw-u2Ra3namXZgjLfw2_-4CKqq_TRJwTzmZnCFCFfAxWE7kri0g7JK-luLspKIxPAuykC32Lx1T9iNX6oZX8YqLfpOcHjJ8z9tNcU6NkpyoZG8', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO inventory VALUES (3, 'Lithium-Ion Cell 18650', 'PWR-881-L', 2, 0, 100, 500, 4.50, 'units', 'OUT_OF_STOCK', '', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO inventory VALUES (4, 'Copper Wire 2mm (Spool)', 'WRE-200-C', 1, 12, 50, 250, 89.99, 'spools', 'LOW_STOCK', 'https://lh3.googleusercontent.com/aida-public/AB6AXuBKkBxcAMc5Xq2fIsjc1PXjPsTAejDw1NvyMpvjbKW4ZHR5MgT-j_14Rg4kzarAUGbmRdY9NSrerOtjW0Orap1G6xw5FiIdr1QKRHnaDtGtHoAL_dYpm22J2jFAiQaINDojv2MfvQyPiDnY8YLeizU4JFEJgkU79oXDs4_Tzt5cbN5-RjeM2WMuHPe1Y5U5FL6FVrp6GW1kSr0GK4aqVz6ojEoh_z5Y2X1uIhrZhPHPRzjuxNcjcpY', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO inventory VALUES (5, 'Industrial Lub. X-200', 'LUB-X20-D', 1, 45, 100, 400, 65.00, 'drums', 'LOW_STOCK', 'https://lh3.googleusercontent.com/aida-public/AB6AXuDaNZeiw2OWhd3Q3HN2ZrKOOw1JFfgo-pYSECa8ghDCSAbebXb4oM8RIQ_gzpoXHuyH71XUPDa6pKN-ErmgrfP37P5poG98G9tCOh8Dpe-avGJFKqSEkKnh4i4G4w-129BWysa99pw-u2Ra3namXZgjLfw2_-4CKqq_TRJwTzmZnCFCFfAxWE7kri0g7JK-luLspKIxPAuykC32Lx1T9iNX6oZX8YqLfpOcHjJ8z9tNcU6NkpyoZG8', CURRENT_TIMESTAMP);");

                stmt.execute("INSERT INTO purchase_requests VALUES (1, 'REQ-4092', 1, 'Industrial Bearings XZ-9', 250, 'Manufacturing', 'Standard', 'Stock replenishment for Q4 production', 'PENDING_REVIEW', 2, CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO purchase_requests VALUES (2, 'REQ-4091', 4, 'Safety Harness Pro', 50, 'Operations', 'High', 'Required for line maintenance team', 'PROCESSING', 2, CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO purchase_requests VALUES (3, 'REQ-4088', 3, 'Lithium Battery Packs', 500, 'Maintenance', 'High', 'Emergency replacement request', 'DENIED', 2, CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO purchase_requests VALUES (4, 'REQ-4085', 5, 'Hydraulic Fluid 50G', 10, 'Manufacturing', 'Standard', 'Monthly routine refill', 'DELIVERED', 2, CURRENT_TIMESTAMP);");

                stmt.execute("INSERT INTO orders VALUES (1, 'PO-2026-001', 2, 1, 3112.50, 'SHIPPED', '2026-08-15', 'In transit via Express Logistics', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO orders VALUES (2, 'PO-2026-002', 4, 2, 650.00, 'DELIVERED', '2026-08-10', 'Received at Dock B', CURRENT_TIMESTAMP);");

                stmt.execute("INSERT INTO notifications VALUES (1, 'LOW_STOCK', 'STORE_MANAGER', 'sarah.j@smartinv.com', 'ALERT: Copper Wire 2mm (Spool) reached critical low level (12 units remaining).', 'EMAIL', 'SENT', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO notifications VALUES (2, 'LOW_STOCK', 'PROCUREMENT_OFFICER', 'michael.r@smartinv.com', 'ALERT: Grade 8 Hex Bolt 1/2\" reached low stock threshold (150/2000).', 'SMS', 'SENT', CURRENT_TIMESTAMP);");
                stmt.execute("INSERT INTO notifications VALUES (3, 'ORDER_STATUS', 'STORE_MANAGER', 'sarah.j@smartinv.com', 'UPDATE: Purchase Order PO-2026-001 has been marked SHIPPED by Apex Industrial Parts Ltd.', 'EMAIL', 'SENT', CURRENT_TIMESTAMP);");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
