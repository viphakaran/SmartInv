package com.smartinv.dao;

import com.smartinv.config.DBConnection;
import com.smartinv.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private static final List<Notification> memoryNotifs = new ArrayList<>();

    static {
        memoryNotifs.add(new Notification(1, "LOW_STOCK", "STORE_MANAGER", "sarah.j@smartinv.com",
                "ALERT: Copper Wire 2mm (Spool) reached critical low level (12 units remaining).", "EMAIL", "SENT", "10 mins ago"));
        memoryNotifs.add(new Notification(2, "LOW_STOCK", "PROCUREMENT_OFFICER", "michael.r@smartinv.com",
                "ALERT: Grade 8 Hex Bolt 1/2\" reached low stock threshold (150/2000).", "SMS", "SENT", "2 hours ago"));
        memoryNotifs.add(new Notification(3, "ORDER_STATUS", "STORE_MANAGER", "sarah.j@smartinv.com",
                "UPDATE: Purchase Order PO-2026-001 has been marked SHIPPED by Apex Industrial Parts Ltd.", "EMAIL", "SENT", "5 hours ago"));
    }

    public List<Notification> getAllNotifications() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return new ArrayList<>(memoryNotifs);

        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY id DESC LIMIT 50";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Notification(
                    rs.getInt("id"),
                    rs.getString("type"),
                    rs.getString("recipient_role"),
                    rs.getString("recipient_email"),
                    rs.getString("message"),
                    rs.getString("channel"),
                    rs.getString("status"),
                    rs.getString("sent_at")
                ));
            }
        } catch (SQLException e) {
            return new ArrayList<>(memoryNotifs);
        }
        return list.isEmpty() ? new ArrayList<>(memoryNotifs) : list;
    }

    public boolean createNotification(Notification notif) {
        notif.setId(memoryNotifs.size() + 1);
        notif.setStatus("SENT");
        notif.setSentAt("Just now");

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            memoryNotifs.add(0, notif);
            return true;
        }

        String sql = "INSERT INTO notifications (type, recipient_role, recipient_email, message, channel, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notif.getType());
            ps.setString(2, notif.getRecipientRole());
            ps.setString(3, notif.getRecipientEmail());
            ps.setString(4, notif.getMessage());
            ps.setString(5, notif.getChannel() != null ? notif.getChannel() : "EMAIL");
            ps.setString(6, "SENT");
            boolean ok = ps.executeUpdate() > 0;
            if (ok) memoryNotifs.add(0, notif);
            return ok;
        } catch (SQLException e) {
            memoryNotifs.add(0, notif);
            return true;
        }
    }
}
