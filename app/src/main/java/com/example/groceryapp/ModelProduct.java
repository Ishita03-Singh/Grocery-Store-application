package com.example.groceryapp;

import android.content.Context;
import android.widget.Toast;

public class ModelProduct {
    private String itemCode,itemName, productCategory,rateType,productIcon,
            rate,discountNote,discountmAvailable,uid,discountPrice,cessrate,igstrate,sgstrate,cgstrate,hsncode;
    private Context context;

    public ModelProduct(Context context){
        this.context = context;
    }

    public ModelProduct(){
    }
    public ModelProduct(String itemCode,String itemName,String rateType,
                        String productCategory,String productIcon,String cess_rate,String igst_rate,String sgst_rate,String cgst_rate,String hsn_code
    ,String rate,String discountPrice,String discountNote,String discountAvailable,String uid){

        this.itemCode=itemCode;
        this.itemName=itemName;
        this.rateType=rateType;
        this.productCategory=productCategory;
        this.productIcon=productIcon;
        this.cessrate=cess_rate;
        this.igstrate=igst_rate;
        this.cgstrate=cgst_rate;
        this.sgstrate=sgst_rate;
        this.hsncode=hsn_code;
        this.rate=rate;
        this.discountPrice=discountPrice;
        this.discountmAvailable=discountAvailable;
        this.discountNote=discountNote;
        this.uid=uid;
    }

    public String getDiscountAvailable() {
        return discountmAvailable;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getItemCode(){return itemCode;}

    public String getItemName() { return itemName; }

    public String getRate() { return rate; }

    public String getCess_rate() { return cessrate; }

    public String getRateType() { return rateType; }

    public String getCgst_rate() { return cgstrate; }

    public String getHsn_code() { return hsncode; }

    public String getProductIcon() {
        return productIcon;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public String getIgst_rate() { return igstrate; }

    public String getUid() {
        return uid;
    }

    public String getSgst_rate() { return sgstrate; }




    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    public void setDiscountAvailable(String discountAvailable) { this.discountmAvailable = discountAvailable;
    }


    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }


    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }


    public void setCess_rate(String cess_rate) {
        this.cessrate = cess_rate;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public void setIgst_rate(String igst_rate) {
        this.igstrate = igst_rate;
    }

    public void setCgst_rate(String cgst_rate) {
        this.cgstrate = cgst_rate;
    }

    public void setHsn_code(String hsn_code) {
        this.hsncode = hsn_code;
    }

    public void setSgst_rate(String sgst_rate) {
        this.sgstrate = sgst_rate;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
