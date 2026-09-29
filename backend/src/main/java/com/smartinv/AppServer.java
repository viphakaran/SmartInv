package com.smartinv;

import com.smartinv.config.DBConnection;
import com.smartinv.servlet.*;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.sql.Connection;

public class AppServer {
    private static final int PORT = System.getenv("PORT") != null 
        ? Integer.parseInt(System.getenv("PORT")) 
        : 8080;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SmartInv Java Servlets Backend Server (3-Tier) ");
        System.out.println("==================================================");

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Database Connection Established. Mode: " + (DBConnection.isSQLite() ? "Embedded SQLite" : "MySQL"));
        } catch (Exception e) {
            System.err.println("Database Warning: " + e.getMessage());
        }

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/api/auth", new AuthServlet());
            server.createContext("/api/inventory", new InventoryServlet());
            server.createContext("/api/procurement", new ProcurementServlet());
            server.createContext("/api/orders", new OrderServlet());
            server.createContext("/api/notifications", new NotificationServlet());
            server.createContext("/api/reports", new ReportServlet());
            server.createContext("/health", exchange -> {
                AuthServlet.sendJSON(exchange, 200, "{\"status\":\"UP\",\"system\":\"SmartInv Java Servlets Backend\"}");
            });

            server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
            server.start();
            System.out.println("Backend Servlets running cleanly on http://localhost:" + PORT + "/api");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
