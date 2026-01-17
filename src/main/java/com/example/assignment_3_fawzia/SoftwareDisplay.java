package com.example.assignment_3_fawzia;

import javafx.beans.property.*;

public class SoftwareDisplay {
    private final StringProperty name;
    private final StringProperty version;
    private final IntegerProperty quantity;
    private final DoubleProperty price;
    private final IntegerProperty position;

    public SoftwareDisplay(Software software) {
        this.name = new SimpleStringProperty(software.getName());
        this.version = new SimpleStringProperty(software.getVersion());
        this.quantity = new SimpleIntegerProperty(software.getQuantity());
        this.price = new SimpleDoubleProperty(software.getPrice());
        this.position = new SimpleIntegerProperty(software.getFilePosition());
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getVersion() {
        return version.get();
    }

    public StringProperty versionProperty() {
        return version;
    }

    public void setVersion(String version) {
        this.version.set(version);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public int getPosition() {
        return position.get();
    }

    public IntegerProperty positionProperty() {
        return position;
    }

    public void setPosition(int position) {
        this.position.set(position);
    }
}