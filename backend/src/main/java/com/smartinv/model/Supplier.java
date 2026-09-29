package com.smartinv.model;

public class Supplier {
    private int id;
    private String name;
    private String contactEmail;
    private String phone;
    private Integer categoryId;
    private String categoryName;
    private double rating;
    private String address;

    public Supplier() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
