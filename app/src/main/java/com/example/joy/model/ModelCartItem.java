package com.example.joy.model;

public class ModelCartItem {

    String id, pId, name, cost, price, quantity, image, shopUid, uid;

    public ModelCartItem() {

    }

    public ModelCartItem(String id, String pId, String name, String cost, String price, String quantity, String image, String shopUid, String uid) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.shopUid = shopUid;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public String getShopUid() {
        return shopUid;
    }

    public void setShopUid(String shopUid) {
        this.shopUid = shopUid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
