package com.example.groceryapp;

public class ModelCartItem {
   private String id,pid,name,price,ratetype,number,cess,igst,sgst,cgst,hsn,productIcon;

    public ModelCartItem(){

    }


    public ModelCartItem(String id,String pid,String name,String price,String number,String cess,String hsn,String igst,
                         String cgst,String sgst)
    {
        this.id=id;
        this.pid=pid;
        this.name=name;
        this.number=number;
        this.price=price;
        this.cess=cess;
        this.hsn=hsn;
        this.igst=igst;
        this.cgst=cgst;
        this.sgst=sgst;


    }

    public String getId() {
        return id;
    }
    public String getCess() {
        return cess;
    }
    public String getName() {
        return name;
    }
    public String getPid() {
        return pid;
    }
    public String getCgst() {
        return cgst;
    }
    public String getNumber() {
        return number;
    }
    public String getPrice() {
        return price;
    }
    public String getHsn() {
        return hsn;
    }
    public String getIgst() {
        return igst;
    }
    public String getSgst() { return sgst; }






    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCess(String cess) {
        this.cess = cess;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public void setIgst(String igst) {
        this.igst = igst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }



}
