package com.smartinv.dao;

import com.smartinv.config.DBConnection;
import com.smartinv.model.Order;
import com.smartinv.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private static final List<Order> memoryOrders = new ArrayList<>();
    private static final List<Supplier> memorySuppliers = new ArrayList<>();

    static {
        Supplier s1 = new Supplier();
        s1.setId(1); s1.setName("Apex Industrial Parts Ltd"); s1.setContactEmail("sales@apexindustrial.com");
        s1.setPhone("+1-800-555-901"); s1.setCategoryId(2); s1.setCategoryName("Components");
        s1.setRating(4.8); s1.setAddress("104 Logistics Way");
        memorySuppliers.add(s1);

        Supplier s2 = new Supplier();
        s2.setId(2); s2.setName("Global Raw Materials Co."); s2.setContactEmail("orders@globalraw.com");
        s2.setPhone("+1-800-555-902"); s2.setCategoryId(1); s2.setCategoryName("Raw Materials");
        s2.setRating(4.6); s2.setAddress("72 Heavy Metals Ave");
        memorySuppliers.add(s2);

        Order o1 = new Order();
        o1.setId(1); o1.setOrderNo("PO-2026-001"); o1.setRequestId(2); o1.setSupplierId(1);
        o1.setSupplierName("Apex Industrial Parts Ltd"); o1.setTotalAmount(3112.50);
        o1.setStatus("SHIPPED"); o1.setEstimatedDelivery("2026-08-15");
        o1.setNotes("In transit via Express Logistics"); o1.setCreatedAt("Oct 22");
        memoryOrders.add(o1);

        Order o2 = new Order();
        o2.setId(2); o2.setOrderNo("PO-2026-002"); o2.setRequestId(4); o2.setSupplierId(2);
        o2.setSupplierName("Global Raw Materials Co."); o2.setTotalAmount(650.00);
        o2.setStatus("DELIVERED"); o2.setEstimatedDelivery("2026-08-10");
        o2.setNotes("Received at Dock B"); o2.setCreatedAt("Oct 20");
        memoryOrders.add(o2);
    }

    public List<Order> getAllOrders() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return new ArrayList<>(memoryOrders);

        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, s.name as supplier_name FROM orders o " +
                     "LEFT JOIN suppliers s ON o.supplier_id = s.id ORDER BY o.id DESC";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        } catch (SQLException e) {
            return new ArrayList<>(memoryOrders);
        }
        return list.isEmpty() ? new ArrayList<>(memoryOrders) : list;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        for (Order o : memoryOrders) {
            if (o.getId() == orderId) {
                o.setStatus(status);
                break;
            }
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) return true;

        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);

            int rows = ps.executeUpdate();
            if (rows > 0 && status.equalsIgnoreCase("DELIVERED")) {
                updateLinkedRequest(conn, orderId);
            }
            return rows > 0;
        } catch (SQLException e) {
            return true;
        }
    }

    private void updateLinkedRequest(Connection conn, int orderId) throws SQLException {
        String sqlReq = "SELECT request_id FROM orders WHERE id = ?";
        try (PreparedStatement psReq = conn.prepareStatement(sqlReq)) {
            psReq.setInt(1, orderId);
            ResultSet rs = psReq.executeQuery();
            if (rs.next()) {
                int reqId = rs.getInt("request_id");
                if (reqId > 0) {
                    String updateReq = "UPDATE purchase_requests SET status = 'DELIVERED' WHERE id = ?";
                    try (PreparedStatement psUp = conn.prepareStatement(updateReq)) {
                        psUp.setInt(1, reqId);
                        psUp.executeUpdate();
                    }
                }
            }
        }
    }

    public List<Supplier> getAllSuppliers() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return new ArrayList<>(memorySuppliers);

        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT s.*, c.name as category_name FROM suppliers s " +
                     "LEFT JOIN categories c ON s.category_id = c.id ORDER BY s.name ASC";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Supplier sup = new Supplier();
                sup.setId(rs.getInt("id"));
                sup.setName(rs.getString("name"));
                sup.setContactEmail(rs.getString("contact_email"));
                sup.setPhone(rs.getString("phone"));
                sup.setCategoryId(rs.getInt("category_id"));
                sup.setCategoryName(rs.getString("category_name"));
                sup.setRating(rs.getDouble("rating"));
                sup.setAddress(rs.getString("address"));
                list.add(sup);
            }
        } catch (SQLException e) {
            return new ArrayList<>(memorySuppliers);
        }
        return list.isEmpty() ? new ArrayList<>(memorySuppliers) : list;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setOrderNo(rs.getString("order_no"));
        o.setRequestId(rs.getObject("request_id") != null ? rs.getInt("request_id") : null);
        o.setSupplierId(rs.getObject("supplier_id") != null ? rs.getInt("supplier_id") : null);
        o.setSupplierName(rs.getString("supplier_name"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setEstimatedDelivery(rs.getString("estimated_delivery"));
        o.setNotes(rs.getString("notes"));
        o.setCreatedAt(rs.getString("created_at"));
        return o;
    }
}
