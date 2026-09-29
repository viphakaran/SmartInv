package com.smartinv.model;

public class Notification {
    private int id;
    private String type; // LOW_STOCK, ORDER_STATUS, APPROVAL_NEEDED, SYSTEM
    private String recipientRole;
    private String recipientEmail;
    private String message;
    private String channel; // EMAIL, SMS, SYSTEM
    private String status;
    private String sentAt;

    public Notification() {}

    public Notification(int id, String type, String recipientRole, String recipientEmail, String message, String channel, String status, String sentAt) {
        this.id = id;
        this.type = type;
        this.recipientRole = recipientRole;
        this.recipientEmail = recipientEmail;
        this.message = message;
        this.channel = channel;
        this.status = status;
        this.sentAt = sentAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRecipientRole() { return recipientRole; }
    public void setRecipientRole(String recipientRole) { this.recipientRole = recipientRole; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
}
