package com.tud.database.models;

public class Item {
    public Item(int id, String name, double price) {
        this.name = name;
        this.price = price;
        this.id = id;
    }

    private final int id;
    private String name;
    private double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }
}
