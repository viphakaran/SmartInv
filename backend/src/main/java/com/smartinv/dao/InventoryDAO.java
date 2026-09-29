package com.smartinv.dao;

import com.smartinv.config.DBConnection;
import com.smartinv.model.Category;
import com.smartinv.model.InventoryItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    private static final List<InventoryItem> memoryItems = new ArrayList<>();
    private static final List<Category> memoryCategories = new ArrayList<>();

    static {
        memoryCategories.add(new Category(1, "Raw Materials", "Basic raw items used in manufacturing"));
        memoryCategories.add(new Category(2, "Components", "Engineered parts and electrical items"));
        memoryCategories.add(new Category(3, "Finished Goods", "Assembled products ready for distribution"));
        memoryCategories.add(new Category(4, "Packaging", "Shipping and storage supplies"));

        InventoryItem i1 = new InventoryItem();
        i1.setId(1); i1.setName("Industrial Ball Bearing"); i1.setSku("BRG-782-X"); i1.setCategoryId(2); i1.setCategoryName("Components");
        i1.setStockQty(1240); i1.setMinThreshold(200); i1.setMaxCapacity(1500); i1.setUnitPrice(12.45); i1.setUnitOfMeasure("units");
        i1.setStatus("IN_STOCK"); i1.setImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuBKkBxcAMc5Xq2fIsjc1PXjPsTAejDw1NvyMpvjbKW4ZHR5MgT-j_14Rg4kzarAUGbmRdY9NSrerOtjW0Orap1G6xw5FiIdr1QKRHnaDtGtHoAL_dYpm22J2jFAiQaINDojv2MfvQyPiDnY8YLeizU4JFEJgkU79oXDs4_Tzt5cbN5-RjeM2WMuHPe1Y5U5FL6FVrp6GW1kSr0GK4aqVz6ojEoh_z5Y2X1uIhrZhPHPRzjuxNcjcpY");
        memoryItems.add(i1);

        InventoryItem i2 = new InventoryItem();
        i2.setId(2); i2.setName("Grade 8 Hex Bolt 1/2\""); i2.setSku("FCS-110-B"); i2.setCategoryId(1); i2.setCategoryName("Raw Materials");
        i2.setStockQty(150); i2.setMinThreshold(500); i2.setMaxCapacity(2000); i2.setUnitPrice(0.85); i2.setUnitOfMeasure("units");
        i2.setStatus("LOW_STOCK"); i2.setImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuDaNZeiw2OWhd3Q3HN2ZrKOOw1JFfgo-pYSECa8ghDCSAbebXb4oM8RIQ_gzpoXHuyH71XUPDa6pKN-ErmgrfP37P5poG98G9tCOh8Dpe-avGJFKqSEkKnh4i4G4w-129BWysa99pw-u2Ra3namXZgjLfw2_-4CKqq_TRJwTzmZnCFCFfAxWE7kri0g7JK-luLspKIxPAuykC32Lx1T9iNX6oZX8YqLfpOcHjJ8z9tNcU6NkpyoZG8");
        memoryItems.add(i2);

        InventoryItem i3 = new InventoryItem();
        i3.setId(3); i3.setName("Lithium-Ion Cell 18650"); i3.setSku("PWR-881-L"); i3.setCategoryId(2); i3.setCategoryName("Components");
        i3.setStockQty(0); i3.setMinThreshold(100); i3.setMaxCapacity(500); i3.setUnitPrice(4.50); i3.setUnitOfMeasure("units");
        i3.setStatus("OUT_OF_STOCK"); i3.setImageUrl("");
        memoryItems.add(i3);

        InventoryItem i4 = new InventoryItem();
        i4.setId(4); i4.setName("Copper Wire 2mm (Spool)"); i4.setSku("WRE-200-C"); i4.setCategoryId(1); i4.setCategoryName("Raw Materials");
        i4.setStockQty(12); i4.setMinThreshold(50); i4.setMaxCapacity(250); i4.setUnitPrice(89.99); i4.setUnitOfMeasure("spools");
        i4.setStatus("LOW_STOCK"); i4.setImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuBKkBxcAMc5Xq2fIsjc1PXjPsTAejDw1NvyMpvjbKW4ZHR5MgT-j_14Rg4kzarAUGbmRdY9NSrerOtjW0Orap1G6xw5FiIdr1QKRHnaDtGtHoAL_dYpm22J2jFAiQaINDojv2MfvQyPiDnY8YLeizU4JFEJgkU79oXDs4_Tzt5cbN5-RjeM2WMuHPe1Y5U5FL6FVrp6GW1kSr0GK4aqVz6ojEoh_z5Y2X1uIhrZhPHPRzjuxNcjcpY");
        memoryItems.add(i4);

        InventoryItem i5 = new InventoryItem();
        i5.setId(5); i5.setName("Industrial Lub. X-200"); i5.setSku("LUB-X20-D"); i5.setCategoryId(1); i5.setCategoryName("Raw Materials");
        i5.setStockQty(45); i5.setMinThreshold(100); i5.setMaxCapacity(400); i5.setUnitPrice(65.00); i5.setUnitOfMeasure("drums");
        i5.setStatus("LOW_STOCK"); i5.setImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuDaNZeiw2OWhd3Q3HN2ZrKOOw1JFfgo-pYSECa8ghDCSAbebXb4oM8RIQ_gzpoXHuyH71XUPDa6pKN-ErmgrfP37P5poG98G9tCOh8Dpe-avGJFKqSEkKnh4i4G4w-129BWysa99pw-u2Ra3namXZgjLfw2_-4CKqq_TRJwTzmZnCFCFfAxWE7kri0g7JK-luLspKIxPAuykC32Lx1T9iNX6oZX8YqLfpOcHjJ8z9tNcU6NkpyoZG8");
        memoryItems.add(i5);
    }

    public List<InventoryItem> getAllItems(String search, String category) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return filterMemoryItems(search, category);
        }

        List<InventoryItem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.*, c.name as category_name FROM inventory i " +
            "LEFT JOIN categories c ON i.category_id = c.id WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(i.name) LIKE ? OR LOWER(i.sku) LIKE ?) ");
            String queryParam = "%" + search.trim().toLowerCase() + "%";
            params.add(queryParam);
            params.add(queryParam);
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All Items")) {
            sql.append("AND c.name = ? ");
            params.add(category.trim());
        }

        sql.append("ORDER BY i.id ASC");

        try (conn; PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapItem(rs));
            }
        } catch (SQLException e) {
            return filterMemoryItems(search, category);
        }
        return list.isEmpty() ? filterMemoryItems(search, category) : list;
    }

    private List<InventoryItem> filterMemoryItems(String search, String category) {
        List<InventoryItem> result = new ArrayList<>();
        for (InventoryItem item : memoryItems) {
            boolean matchSearch = search == null || search.trim().isEmpty() ||
                    item.getName().toLowerCase().contains(search.toLowerCase()) ||
                    item.getSku().toLowerCase().contains(search.toLowerCase());
            boolean matchCat = category == null || category.trim().isEmpty() ||
                    category.equalsIgnoreCase("All Items") ||
                    (item.getCategoryName() != null && item.getCategoryName().equalsIgnoreCase(category));
            if (matchSearch && matchCat) {
                result.add(item);
            }
        }
        return result;
    }

    public boolean addItem(InventoryItem item) {
        item.setStatus(calculateStatus(item.getStockQty(), item.getMinThreshold()));
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            item.setId(memoryItems.size() + 1);
            memoryItems.add(0, item);
            return true;
        }

        String sql = "INSERT INTO inventory (name, sku, category_id, stock_qty, min_threshold, max_capacity, unit_price, unit_of_measure, status, image_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getSku());
            ps.setInt(3, item.getCategoryId() > 0 ? item.getCategoryId() : 1);
            ps.setInt(4, item.getStockQty());
            ps.setInt(5, item.getMinThreshold());
            ps.setInt(6, item.getMaxCapacity() > 0 ? item.getMaxCapacity() : 1000);
            ps.setDouble(7, item.getUnitPrice());
            ps.setString(8, item.getUnitOfMeasure() != null ? item.getUnitOfMeasure() : "units");
            ps.setString(9, item.getStatus());
            ps.setString(10, item.getImageUrl());
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                memoryItems.add(0, item);
            }
            return success;
        } catch (SQLException e) {
            item.setId(memoryItems.size() + 1);
            memoryItems.add(0, item);
            return true;
        }
    }

    public boolean updateItem(InventoryItem item) {
        item.setStatus(calculateStatus(item.getStockQty(), item.getMinThreshold()));
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            for (int i = 0; i < memoryItems.size(); i++) {
                if (memoryItems.get(i).getId() == item.getId()) {
                    memoryItems.set(i, item);
                    return true;
                }
            }
            return false;
        }

        String sql = "UPDATE inventory SET name = ?, sku = ?, category_id = ?, stock_qty = ?, " +
                     "min_threshold = ?, max_capacity = ?, unit_price = ?, unit_of_measure = ?, status = ?, image_url = ? " +
                     "WHERE id = ?";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getSku());
            ps.setInt(3, item.getCategoryId() > 0 ? item.getCategoryId() : 1);
            ps.setInt(4, item.getStockQty());
            ps.setInt(5, item.getMinThreshold());
            ps.setInt(6, item.getMaxCapacity());
            ps.setDouble(7, item.getUnitPrice());
            ps.setString(8, item.getUnitOfMeasure());
            ps.setString(9, item.getStatus());
            ps.setString(10, item.getImageUrl());
            ps.setInt(11, item.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            for (int i = 0; i < memoryItems.size(); i++) {
                if (memoryItems.get(i).getId() == item.getId()) {
                    memoryItems.set(i, item);
                    return true;
                }
            }
            return false;
        }
    }

    public boolean deleteItem(int id) {
        Connection conn = DBConnection.getConnection();
        memoryItems.removeIf(item -> item.getId() == id);
        if (conn == null) return true;

        String sql = "DELETE FROM inventory WHERE id = ?";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return true;
        }
    }

    public List<Category> getAllCategories() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return memoryCategories;

        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name ASC";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (SQLException e) {
            return memoryCategories;
        }
        return list.isEmpty() ? memoryCategories : list;
    }

    private String calculateStatus(int qty, int minThreshold) {
        if (qty <= 0) return "OUT_OF_STOCK";
        if (qty <= minThreshold) return "LOW_STOCK";
        return "IN_STOCK";
    }

    private InventoryItem mapItem(ResultSet rs) throws SQLException {
        InventoryItem item = new InventoryItem();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setSku(rs.getString("sku"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setCategoryName(rs.getString("category_name"));
        item.setStockQty(rs.getInt("stock_qty"));
        item.setMinThreshold(rs.getInt("min_threshold"));
        item.setMaxCapacity(rs.getInt("max_capacity"));
        item.setUnitPrice(rs.getDouble("unit_price"));
        item.setUnitOfMeasure(rs.getString("unit_of_measure"));
        item.setStatus(rs.getString("status"));
        item.setImageUrl(rs.getString("image_url"));
        return item;
    }
}
