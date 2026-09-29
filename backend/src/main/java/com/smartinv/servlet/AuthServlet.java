package com.smartinv.servlet;

import com.smartinv.dao.UserDAO;
import com.smartinv.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AuthServlet implements HttpHandler {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCORSHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();

        if ("POST".equalsIgnoreCase(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String username = extractJSONValue(body, "username");
            String password = extractJSONValue(body, "password");

            User user = userDAO.authenticate(username, password);
            if (user != null) {
                String json = String.format("{\"success\":true,\"user\":{\"id\":%d,\"username\":\"%s\",\"name\":\"%s\",\"role\":\"%s\",\"email\":\"%s\",\"department\":\"%s\"},\"token\":\"jwt-session-%d\"}",
                        user.getId(), escapeJSON(user.getUsername()), escapeJSON(user.getName()), user.getRole(), escapeJSON(user.getEmail()), escapeJSON(user.getDepartment()), user.getId());
                sendJSON(exchange, 200, json);
            } else {
                sendJSON(exchange, 401, "{\"success\":false,\"message\":\"Invalid credentials\"}");
            }
        } else {
            List<User> users = userDAO.getAllUsers();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                sb.append(String.format("{\"id\":%d,\"username\":\"%s\",\"name\":\"%s\",\"role\":\"%s\",\"email\":\"%s\",\"department\":\"%s\"}",
                        u.getId(), escapeJSON(u.getUsername()), escapeJSON(u.getName()), u.getRole(), escapeJSON(u.getEmail()), escapeJSON(u.getDepartment())));
                if (i < users.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJSON(exchange, 200, sb.toString());
        }
    }

    public static void addCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    public static void sendJSON(HttpExchange exchange, int code, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static String extractJSONValue(String json, String key) {
        if (json == null) return "";
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int start = idx + search.length();
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) start++;
        int end = start;
        while (end < json.length() && json.charAt(end) != '"' && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }

    public static String escapeJSON(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
