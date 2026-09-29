package com.smartinv.dao;

import com.smartinv.config.DBConnection;
import com.smartinv.model.PurchaseRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProcurementDAO {

    private static final List<PurchaseRequest> memoryRequests = new ArrayList<>();

    static {
        PurchaseRequest r1 = new PurchaseRequest();
        r1.setId(1); r1.setReqCode("REQ-4092"); r1.setItemId(1); r1.setItemName("Industrial Bearings XZ-9");
        r1.setQuantity(250); r1.setDepartment("Manufacturing"); r1.setPriority("Standard");
        r1.setNotes("Stock replenishment for Q4 production"); r1.setStatus("PENDING_REVIEW");
        r1.setRequestedBy(2); r1.setRequestedByName("Sarah Jenkins"); r1.setCreatedAt("Oct 24");
        memoryRequests.add(r1);

        PurchaseRequest r2 = new PurchaseRequest();
        r2.setId(2); r2.setReqCode("REQ-4091"); r2.setItemId(4); r2.setItemName("Safety Harness Pro");
        r2.setQuantity(50); r2.setDepartment("Operations"); r2.setPriority("High");
        r2.setNotes("Required for line maintenance team"); r2.setStatus("PROCESSING");
        r2.setRequestedBy(2); r2.setRequestedByName("Sarah Jenkins"); r2.setCreatedAt("Oct 23");
        memoryRequests.add(r2);

        PurchaseRequest r3 = new PurchaseRequest();
        r3.setId(3); r3.setReqCode("REQ-4088"); r3.setItemId(3); r3.setItemName("Lithium Battery Packs");
        r3.setQuantity(500); r3.setDepartment("Maintenance"); r3.setPriority("High");
        r3.setNotes("Emergency replacement request"); r3.setStatus("DENIED");
        r3.setRequestedBy(2); r3.setRequestedByName("Sarah Jenkins"); r3.setCreatedAt("Oct 21");
        memoryRequests.add(r3);

        PurchaseRequest r4 = new PurchaseRequest();
        r4.setId(4); r4.setReqCode("REQ-4085"); r4.setItemId(5); r4.setItemName("Hydraulic Fluid 50G");
        r4.setQuantity(10); r4.setDepartment("Manufacturing"); r4.setPriority("Standard");
        r4.setNotes("Monthly routine refill"); r4.setStatus("DELIVERED");
        r4.setRequestedBy(2); r4.setRequestedByName("Sarah Jenkins"); r4.setCreatedAt("Oct 20");
        memoryRequests.add(r4);
    }

    public List<PurchaseRequest> getAllRequests(String filter) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return filterMemory(filter);
        }

        List<PurchaseRequest> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT pr.*, u.name as requested_by_name FROM purchase_requests pr " +
            "LEFT JOIN users u ON pr.requested_by = u.id WHERE 1=1 "
        );

        if (filter != null && !filter.isEmpty() && !filter.equalsIgnoreCase("All Requests")) {
            if (filter.equalsIgnoreCase("Pending Review")) {
                sql.append("AND pr.status = 'PENDING_REVIEW' ");
            } else if (filter.equalsIgnoreCase("Processing")) {
                sql.append("AND pr.status = 'PROCESSING' ");
            } else if (filter.equalsIgnoreCase("Delivered")) {
                sql.append("AND pr.status = 'DELIVERED' ");
            }
        }

        sql.append("ORDER BY pr.id DESC");

        try (conn; PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRequest(rs));
            }
        } catch (SQLException e) {
            return filterMemory(filter);
        }
        return list.isEmpty() ? filterMemory(filter) : list;
    }

    private List<PurchaseRequest> filterMemory(String filter) {
        if (filter == null || filter.isEmpty() || filter.equalsIgnoreCase("All Requests")) {
            return new ArrayList<>(memoryRequests);
        }
        List<PurchaseRequest> res = new ArrayList<>();
        for (PurchaseRequest r : memoryRequests) {
            if (filter.equalsIgnoreCase("Pending Review") && "PENDING_REVIEW".equalsIgnoreCase(r.getStatus())) res.add(r);
            else if (filter.equalsIgnoreCase("Processing") && "PROCESSING".equalsIgnoreCase(r.getStatus())) res.add(r);
            else if (filter.equalsIgnoreCase("Delivered") && "DELIVERED".equalsIgnoreCase(r.getStatus())) res.add(r);
        }
        return res;
    }

    public boolean createRequest(PurchaseRequest req) {
        if (req.getReqCode() == null) {
            req.setReqCode("REQ-" + (int)(1000 + Math.random() * 9000));
        }
        req.setStatus("PENDING_REVIEW");
        req.setCreatedAt("Just now");
        req.setId(memoryRequests.size() + 1);

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            memoryRequests.add(0, req);
            return true;
        }

        String sql = "INSERT INTO purchase_requests (req_code, item_id, item_name, quantity, department, priority, notes, status, requested_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, req.getReqCode());
            if (req.getItemId() != null && req.getItemId() > 0) {
                ps.setInt(2, req.getItemId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, req.getItemName());
            ps.setInt(4, req.getQuantity());
            ps.setString(5, req.getDepartment() != null ? req.getDepartment() : "General");
            ps.setString(6, req.getPriority() != null ? req.getPriority() : "Standard");
            ps.setString(7, req.getNotes());
            ps.setString(8, "PENDING_REVIEW");
            ps.setInt(9, req.getRequestedBy() != null ? req.getRequestedBy() : 2);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) memoryRequests.add(0, req);
            return ok;
        } catch (SQLException e) {
            memoryRequests.add(0, req);
            return true;
        }
    }

    public boolean updateRequestStatus(int requestId, String status) {
        for (PurchaseRequest r : memoryRequests) {
            if (r.getId() == requestId) {
                r.setStatus(status);
                break;
            }
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) return true;

        String sql = "UPDATE purchase_requests SET status = ? WHERE id = ?";
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return true;
        }
    }

    private PurchaseRequest mapRequest(ResultSet rs) throws SQLException {
        PurchaseRequest r = new PurchaseRequest();
        r.setId(rs.getInt("id"));
        r.setReqCode(rs.getString("req_code"));
        r.setItemId(rs.getObject("item_id") != null ? rs.getInt("item_id") : null);
        r.setItemName(rs.getString("item_name"));
        r.setQuantity(rs.getInt("quantity"));
        r.setDepartment(rs.getString("department"));
        r.setPriority(rs.getString("priority"));
        r.setNotes(rs.getString("notes"));
        r.setStatus(rs.getString("status"));
        r.setRequestedBy(rs.getObject("requested_by") != null ? rs.getInt("requested_by") : null);
        r.setRequestedByName(rs.getString("requested_by_name"));
        r.setCreatedAt(rs.getString("created_at"));
        return r;
    }
}
