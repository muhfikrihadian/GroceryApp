package com.example.groceryapp.Model;

public class Cart
{
    private String sID, pid, totalprice, tipe, pname, price, sellerName, sellerAddress, sellerEmail, sellerPhone, quantity, date, time, image;

    public Cart() {
    }

    public Cart(String pid, String sID, String totalprice, String tipe, String sellerName, String sellerAddress, String sellerEmail, String sellerPhone, String pname, String price, String image, String quantity, String date, String time) {
        this.pid = pid;
        this.sID = sID;
        this.pname = pname;
        this.price = price;
        this.totalprice = totalprice;
        this.tipe = tipe;
        this.quantity = quantity;
        this.sellerName = sellerName;
        this.sellerAddress = sellerAddress;
        this.sellerEmail = sellerEmail;
        this.sellerPhone = sellerPhone;
        this.date = date;
        this.time = time;
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSID() {
        return sID;
    }

    public void setSID(String sID) {
        this.sID = sID;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerphone() {
        return sellerPhone;
    }

    public void setSellerphone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSelleraddress() {
        return sellerAddress;
    }

    public void setSelleraddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public String getSelleremail() {
        return sellerEmail;
    }

    public void setSelleremail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
