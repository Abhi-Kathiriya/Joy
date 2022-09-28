package com.example.joy.model;

public class ModelCategory {

    private String categoryTitle,categoryId;
    private String productIcon,uid;

    public ModelCategory() {
    }

    public ModelCategory(String categoryTitle, String categoryId, String productIcon, String uid) {
        this.categoryTitle = categoryTitle;
        this.categoryId = categoryId;
        this.productIcon = productIcon;
        this.uid = uid;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
