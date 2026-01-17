package com.example.assignment_3_fawzia;

public class Software {
    private String name;
    private String version;
    private int quantity;
    private double price;
    private int filePosition;

    public Software(String name, String version, int quantity, double price, int filePosition) {
        this.name = name;
        this.version = version;
        this.quantity = quantity;
        this.price = price;
        this.filePosition = filePosition;
    }

    public String getKey() {
        return name + version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFilePosition() {
        return filePosition;
    }

    public void setFilePosition(int filePosition) {
        this.filePosition = filePosition;
    }

    @Override
    public String toString() {
        return String.format("%s %s (Qty: %d, Price: $%.2f, Pos: %d)",
                name, version, quantity, price, filePosition);
    }
}