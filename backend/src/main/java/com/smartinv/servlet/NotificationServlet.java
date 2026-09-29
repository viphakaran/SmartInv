package com.smartinv.servlet;

import com.smartinv.dao.NotificationDAO;
import com.smartinv.model.Notification;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NotificationServlet implements HttpHandler {
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
            List<Notification> list = notificationDAO.getAllNotifications();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Notification n = list.get(i);
                sb.append(String.format("{\"id\":%d,\"type\":\"%s\",\"recipientRole\":\"%s\",\"recipientEmail\":\"%s\",\"message\":\"%s\",\"channel\":\"%s\",\"status\":\"%s\",\"sentAt\":\"%s\"}",
                        n.getId(), AuthServlet.escapeJSON(n.getType()), AuthServlet.escapeJSON(n.getRecipientRole()),
                        AuthServlet.escapeJSON(n.getRecipientEmail()), AuthServlet.escapeJSON(n.getMessage()),
                        AuthServlet.escapeJSON(n.getChannel()), AuthServlet.escapeJSON(n.getStatus()),
                        AuthServlet.escapeJSON(n.getSentAt())));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            AuthServlet.sendJSON(exchange, 200, sb.toString());
        } else if ("POST".equalsIgnoreCase(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Notification notif = new Notification();
            notif.setType(AuthServlet.extractJSONValue(body, "type"));
            notif.setRecipientRole(AuthServlet.extractJSONValue(body, "recipientRole"));
            notif.setRecipientEmail(AuthServlet.extractJSONValue(body, "recipientEmail"));
            notif.setMessage(AuthServlet.extractJSONValue(body, "message"));
            notif.setChannel(AuthServlet.extractJSONValue(body, "channel"));

            boolean success = notificationDAO.createNotification(notif);
            AuthServlet.sendJSON(exchange, 200, "{\"success\":" + success + "}");
        }
    }
}
