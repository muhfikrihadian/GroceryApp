package com.example.groceryapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelReview {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("idmenu")
    @Expose
    private String idmenu;
    @SerializedName("iduser")
    @Expose
    private String iduser;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("review")
    @Expose
    private String review;
    @SerializedName("rating")
    @Expose
    private Integer rating;


    public ModelReview(){

    }

    public ModelReview(String id, String idmenu, String iduser, String nama, String review, Integer rating){
        this.id = id;
        this.idmenu = idmenu;
        this.iduser = iduser;
        this.nama = nama;
        this.review = review;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdmenu() {
        return idmenu;
    }

    public void setIdmenu(String idmenu) {
        this.idmenu = idmenu;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

}
