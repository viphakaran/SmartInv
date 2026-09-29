package com.smartinv.servlet;

import com.smartinv.dao.NotificationDAO;
import com.smartinv.dao.ProcurementDAO;
import com.smartinv.model.Notification;
import com.smartinv.model.PurchaseRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProcurementServlet implements HttpHandler {
    private final ProcurementDAO procurementDAO = new ProcurementDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        AuthServlet.addCORSHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            List<PurchaseRequest> list = procurementDAO.getAllRequests(null);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                PurchaseRequest r = list.get(i);
                sb.append(String.format("{\"id\":%d,\"reqCode\":\"%s\",\"itemName\":\"%s\",\"quantity\":%d,\"department\":\"%s\",\"priority\":\"%s\",\"status\":\"%s\",\"createdAt\":\"%s\"}",
                        r.getId(), AuthServlet.escapeJSON(r.getReqCode()), AuthServlet.escapeJSON(r.getItemName()), r.getQuantity(),
                        AuthServlet.escapeJSON(r.getDepartment()), AuthServlet.escapeJSON(r.getPriority()),
                        AuthServlet.escapeJSON(r.getStatus()), AuthServlet.escapeJSON(r.getCreatedAt())));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            AuthServlet.sendJSON(exchange, 200, sb.toString());
        } else if ("POST".equalsIgnoreCase(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            PurchaseRequest req = new PurchaseRequest();
            req.setItemName(AuthServlet.extractJSONValue(body, "itemName"));
            String qtyStr = AuthServlet.extractJSONValue(body, "quantity");
            req.setQuantity(!qtyStr.isEmpty() ? Integer.parseInt(qtyStr) : 1);
            req.setDepartment(AuthServlet.extractJSONValue(body, "department"));
            req.setPriority(AuthServlet.extractJSONValue(body, "priority"));
            req.setNotes(AuthServlet.extractJSONValue(body, "notes"));

            boolean success = procurementDAO.createRequest(req);
            if (success) {
                Notification notif = new Notification();
                notif.setType("APPROVAL_NEEDED");
                notif.setRecipientRole("PROCUREMENT_OFFICER");
                notif.setRecipientEmail("michael.r@smartinv.com");
                notif.setMessage("NEW REQUEST: Purchase request for " + req.getQuantity() + "x " + req.getItemName() + " created.");
                notif.setChannel("EMAIL");
                notificationDAO.createNotification(notif);
            }
            AuthServlet.sendJSON(exchange, 200, "{\"success\":" + success + "}");
        } else if ("PUT".equalsIgnoreCase(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String idStr = AuthServlet.extractJSONValue(body, "id");
            String status = AuthServlet.extractJSONValue(body, "status");

            boolean success = false;
            if (!idStr.isEmpty()) {
                success = procurementDAO.updateRequestStatus(Integer.parseInt(idStr), status);
            }
            AuthServlet.sendJSON(exchange, 200, "{\"success\":" + success + "}");
        }
    }
}
