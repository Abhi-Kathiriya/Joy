package com.example.joy.model;

public class ModelOffer {

    private String offerId,offerIcon,uid,offerTitle;

    public ModelOffer() {
    }

    public ModelOffer(String offerId, String offerIcon, String uid, String offerTitle) {
        this.offerId = offerId;
        this.offerIcon = offerIcon;
        this.uid = uid;
        this.offerTitle = offerTitle;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferIcon() {
        return offerIcon;
    }

    public void setOfferIcon(String offerIcon) {
        this.offerIcon = offerIcon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
