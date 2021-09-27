package com.example.groceryapp.Model;

public class Order
{
    private String sID, sellerName, sellerPhone, name, phone, address, city, state, date, time, totalAmount;

    public Order() {
    }

    public Order(String sID, String sellerName, String sellerPhone, String name, String phone, String address, String city, String state, String date, String time, String totalAmount) {
        this.sID = sID;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
    }

    public String getSID() {
        return sID;
    }

    public void setSID(String sID) {
        this.sID = sID;
    }

    public String getSellername() {
        return sellerName;
    }

    public void setSellername(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerphone() {
        return sellerPhone;
    }

    public void setSellerphone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
