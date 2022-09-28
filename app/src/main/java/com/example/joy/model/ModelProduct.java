package com.example.joy.model;

public class ModelProduct {

    private String productId;
    private String categoryId;
    private String productTitle;
    private String productDescription;
    private String productInstruction;
    private String deliveryTime;
    private String productIcon;
    private String originalPrice;
    private String discountPrice;
    private String discountNote;
    private String discountAvailable;
    private String timestamp;
    private String uid,favourite;

    public ModelProduct() {
    }

    public ModelProduct(String productId, String categoryId, String productTitle, String productDescription, String productInstruction,
                        String deliveryTime, String productIcon, String originalPrice, String discountPrice, String discountNote,
                        String discountAvailable, String timestamp, String uid, String favourite) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productInstruction = productInstruction;
        this.deliveryTime = deliveryTime;
        this.productIcon = productIcon;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.discountNote = discountNote;
        this.discountAvailable = discountAvailable;
        this.timestamp = timestamp;
        this.uid = uid;
        this.favourite = favourite;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductInstruction() {
        return productInstruction;
    }

    public void setProductInstruction(String productInstruction) {
        this.productInstruction = productInstruction;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
