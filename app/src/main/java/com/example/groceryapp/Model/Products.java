package com.example.groceryapp.Model;

public class Products {
    public String id, sellerName, sid, pname, description, tipe, price, image, logo, pid, date, time, productState;
    private Integer rating;

    public Products() {
    }

    public Products(String id, String sellerName, String sid, String pname, String description, String tipe, String price, String image, String logo, String pid, String date, String time, String productState, Integer rating) {
        this.id = id;
        this.sellerName = sellerName;
        this.sid = sid;
        this.pname = pname;
        this.description = description;
        this.tipe = tipe;
        this.price = price;
        this.image = image;
        this.logo = logo;
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.productState = productState;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getsellerName() {
        return sellerName;
    }

    public void setsellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }
}