package com.smartinv.servlet;

import com.smartinv.dao.NotificationDAO;
import com.smartinv.dao.OrderDAO;
import com.smartinv.model.Notification;
import com.smartinv.model.Order;
import com.smartinv.model.Supplier;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OrderServlet implements HttpHandler {
    private final OrderDAO orderDAO = new OrderDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        AuthServlet.addCORSHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        if ("GET".equalsIgnoreCase(method)) {
            if (query != null && query.contains("suppliers")) {
                List<Supplier> suppliers = orderDAO.getAllSuppliers();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < suppliers.size(); i++) {
                    Supplier s = suppliers.get(i);
                    sb.append(String.format("{\"id\":%d,\"name\":\"%s\",\"contactEmail\":\"%s\",\"phone\":\"%s\",\"rating\":%.1f,\"address\":\"%s\"}",
                            s.getId(), AuthServlet.escapeJSON(s.getName()), AuthServlet.escapeJSON(s.getContactEmail()),
                            AuthServlet.escapeJSON(s.getPhone()), s.getRating(), AuthServlet.escapeJSON(s.getAddress())));
                    if (i < suppliers.size() - 1) sb.append(",");
                }
                sb.append("]");
                AuthServlet.sendJSON(exchange, 200, sb.toString());
                return;
            }

            List<Order> orders = orderDAO.getAllOrders();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                sb.append(String.format("{\"id\":%d,\"orderNo\":\"%s\",\"supplierName\":\"%s\",\"totalAmount\":%.2f,\"status\":\"%s\",\"estimatedDelivery\":\"%s\"}",
                        o.getId(), AuthServlet.escapeJSON(o.getOrderNo()), AuthServlet.escapeJSON(o.getSupplierName()),
                        o.getTotalAmount(), AuthServlet.escapeJSON(o.getStatus()), AuthServlet.escapeJSON(o.getEstimatedDelivery())));
                if (i < orders.size() - 1) sb.append(",");
            }
            sb.append("]");
            AuthServlet.sendJSON(exchange, 200, sb.toString());
        } else if ("PUT".equalsIgnoreCase(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String idStr = AuthServlet.extractJSONValue(body, "id");
            String status = AuthServlet.extractJSONValue(body, "status");

            boolean success = false;
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                success = orderDAO.updateOrderStatus(id, status);
                if (success) {
                    Notification notif = new Notification();
                    notif.setType("ORDER_STATUS");
                    notif.setRecipientRole("STORE_MANAGER");
                    notif.setRecipientEmail("sarah.j@smartinv.com");
                    notif.setMessage("ORDER UPDATE: Order PO-2026-00" + id + " status changed to " + status + ".");
                    notif.setChannel("EMAIL");
                    notificationDAO.createNotification(notif);
                }
            }
            AuthServlet.sendJSON(exchange, 200, "{\"success\":" + success + "}");
        }
    }
}
