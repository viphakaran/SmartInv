package com.smartinv.model;

public class PurchaseRequest {
    private int id;
    private String reqCode;
    private Integer itemId;
    private String itemName;
    private int quantity;
    private String department;
    private String priority; // Standard, High, Low
    private String notes;
    private String status; // PENDING_REVIEW, APPROVED, DENIED, PROCESSING, DELIVERED
    private Integer requestedBy;
    private String requestedByName;
    private String createdAt;

    public PurchaseRequest() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReqCode() { return reqCode; }
    public void setReqCode(String reqCode) { this.reqCode = reqCode; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Integer requestedBy) { this.requestedBy = requestedBy; }

    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
