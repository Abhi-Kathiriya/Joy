package com.example.joy.model;

public class ModelProductReview {

    String pId,cId,uid,comments,rating,timestamp;

    public ModelProductReview() {
    }

    public ModelProductReview(String pId, String cId, String uid, String comments, String rating, String timestamp) {
        this.pId = pId;
        this.cId = cId;
        this.uid = uid;
        this.comments = comments;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
