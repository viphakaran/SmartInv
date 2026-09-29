package com.smartinv.servlet;

import com.smartinv.dao.InventoryDAO;
import com.smartinv.dao.NotificationDAO;
import com.smartinv.model.Category;
import com.smartinv.model.InventoryItem;
import com.smartinv.model.Notification;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class InventoryServlet implements HttpHandler {
    private final InventoryDAO inventoryDAO = new InventoryDAO();
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
            if (query != null && query.contains("categories")) {
                List<Category> categories = inventoryDAO.getAllCategories();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < categories.size(); i++) {
                    Category c = categories.get(i);
                    sb.append(String.format("{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\"}",
                            c.getId(), AuthServlet.escapeJSON(c.getName()), AuthServlet.escapeJSON(c.getDescription())));
                    if (i < categories.size() - 1) sb.append(",");
                }
                sb.append("]");
                AuthServlet.sendJSON(exchange, 200, sb.toString());
                return;
            }

            String search = getQueryParam(query, "search");
            String category = getQueryParam(query, "category");

            List<InventoryItem> items = inventoryDAO.getAllItems(search, category);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < items.size(); i++) {
                InventoryItem item = items.get(i);
                sb.append(String.format("{\"id\":%d,\"name\":\"%s\",\"sku\":\"%s\",\"categoryId\":%d,\"categoryName\":\"%s\",\"stockQty\":%d,\"minThreshold\":%d,\"maxCapacity\":%d,\"unitPrice\":%.2f,\"unitOfMeasure\":\"%s\",\"status\":\"%s\",\"imageUrl\":\"%s\"}",
                        item.getId(), AuthServlet.escapeJSON(item.getName()), AuthServlet.escapeJSON(item.getSku()),
                        item.getCategoryId(), AuthServlet.escapeJSON(item.getCategoryName()), item.getStockQty(),
                        item.getMinThreshold(), item.getMaxCapacity(), item.getUnitPrice(),
                        AuthServlet.escapeJSON(item.getUnitOfMeasure()), item.getStatus(), AuthServlet.escapeJSON(item.getImageUrl())));
                if (i < items.size() - 1) sb.append(",");
            }
            sb.append("]");
            AuthServlet.sendJSON(exchange, 200, sb.toString());
        } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            InventoryItem item = parseItem(body);

            boolean success;
            if ("POST".equalsIgnoreCase(method)) {
                success = inventoryDAO.addItem(item);
            } else {
                success = inventoryDAO.updateItem(item);
            }

            if (success && item.getStockQty() <= item.getMinThreshold()) {
                Notification notif = new Notification();
                notif.setType("LOW_STOCK");
                notif.setRecipientRole("STORE_MANAGER");
                notif.setRecipientEmail("sarah.j@smartinv.com");
                notif.setMessage("AUTOMATED ALERT: Item '" + item.getName() + "' stock is low (" + item.getStockQty() + " units remaining).");
                notif.setChannel("EMAIL");
                notificationDAO.createNotification(notif);
            }

            AuthServlet.sendJSON(exchange, 200, "{\"success\":" + success + "}");
        } else if ("DELETE".equalsIgnoreCase(method)) {
            String idStr = getQueryParam(query, "id");
            boolean success = false;
            if (idStr != null && !idStr.isEmpty()) {
                success = inventoryDAO.deleteItem(Integer.parseInt(idStr));
            }
            AuthServlet.sendJSON(exchange, 200, "{\"success\":" + success + "}");
        }
    }

    private InventoryItem parseItem(String body) {
        InventoryItem item = new InventoryItem();
        String idStr = AuthServlet.extractJSONValue(body, "id");
        if (!idStr.isEmpty()) item.setId(Integer.parseInt(idStr));
        item.setName(AuthServlet.extractJSONValue(body, "name"));
        item.setSku(AuthServlet.extractJSONValue(body, "sku"));
        String catName = AuthServlet.extractJSONValue(body, "categoryName");
        item.setCategoryName(!catName.isEmpty() ? catName : "Raw Materials");
        String catIdStr = AuthServlet.extractJSONValue(body, "categoryId");
        if (!catIdStr.isEmpty()) {
            item.setCategoryId(Integer.parseInt(catIdStr));
        } else {
            if ("Components".equalsIgnoreCase(catName)) item.setCategoryId(2);
            else if ("Finished Goods".equalsIgnoreCase(catName)) item.setCategoryId(3);
            else if ("Packaging".equalsIgnoreCase(catName)) item.setCategoryId(4);
            else item.setCategoryId(1);
        }
        String qtyStr = AuthServlet.extractJSONValue(body, "stockQty");
        item.setStockQty(!qtyStr.isEmpty() ? Integer.parseInt(qtyStr) : 0);
        String minStr = AuthServlet.extractJSONValue(body, "minThreshold");
        item.setMinThreshold(!minStr.isEmpty() ? Integer.parseInt(minStr) : 10);
        String maxStr = AuthServlet.extractJSONValue(body, "maxCapacity");
        item.setMaxCapacity(!maxStr.isEmpty() ? Integer.parseInt(maxStr) : 1000);
        String priceStr = AuthServlet.extractJSONValue(body, "unitPrice");
        item.setUnitPrice(!priceStr.isEmpty() ? Double.parseDouble(priceStr) : 0.0);
        item.setUnitOfMeasure(AuthServlet.extractJSONValue(body, "unitOfMeasure"));
        item.setImageUrl(AuthServlet.extractJSONValue(body, "imageUrl"));
        return item;
    }

    private String getQueryParam(String query, String param) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2 && parts[0].equalsIgnoreCase(param)) {
                return java.net.URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
