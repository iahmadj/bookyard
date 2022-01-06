package com.classroom.bookyard.Model;

import java.util.ArrayList;

public class Product {
    private String id_product;
    private String id_seller;
    private String id_cat;
    private String description;
    private String fname;
    private ArrayList img;
    private String img_product;
    private Double price;

    public Product(){}

    public Product(String id_product, String id_seller, String id_cat, String fname, String description, ArrayList img, Double price) {
        this.id_product = id_product;
        this.id_seller = id_seller;
        this.id_cat = id_cat;
        this.fname = fname;
        this.description = description;
        this.img = img;
        this.price = price;
    }

    public Product(String id_product, String id_seller, String id_cat, String fname, String description, String img_product, Double price) {
        this.id_product = id_product;
        this.id_seller = id_seller;
        this.id_cat = id_cat;
        this.fname = fname;
        this.description = description;
        this.img_product = img_product;
        this.price = price;
    }

    public Product(String id_product, String id_seller, String id_cat, String img_product, String fname, Double price)  {
        this.id_product = id_product;
        this.id_seller = id_seller;
        this.id_cat = id_cat;
        this.img_product = img_product;
        this.fname = fname;
        this.price = price;
    }

    public String getId_product() {
        return id_product;
    }

    public String getfname() {
        return fname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList getImg() {
        return img;
    }

    public Double getPrice() {
        return price;
    }

    public String getImg_product() {
        return img_product;
    }

    public String getId_seller() {
        return id_seller;
    }

    public String getId_cat() {
        return id_cat;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public void setId_seller(String id_seller) {
        this.id_seller = id_seller;
    }

    public void setId_cat(String id_cat) {
        this.id_cat = id_cat;
    }

    public void setfname(String fname) {
        this.fname = fname;
    }

    public void setImg(ArrayList img) {
        this.img = img;
    }

    public void setImg_product(String img_product) {
        this.img_product = img_product;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


}
