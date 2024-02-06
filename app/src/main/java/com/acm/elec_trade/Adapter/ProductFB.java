package com.acm.elec_trade.Adapter;

public class ProductFB {
    String name, desc, price;

    public ProductFB(String name, String desc, String price) {
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    public ProductFB() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ProductFB{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
