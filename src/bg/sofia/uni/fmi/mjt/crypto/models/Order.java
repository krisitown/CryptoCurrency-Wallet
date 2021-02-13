package bg.sofia.uni.fmi.mjt.crypto.models;

import java.util.Date;

public class Order {
    private String asset;
    private boolean isSell;
    private double price;
    private double amount;
    private Date time;

    public Order(String asset, boolean isSell, double price, double amount, Date time) {
        this.asset = asset;
        this.isSell = isSell;
        this.price = price;
        this.amount = amount;
        this.time = time;
    }

    public String getAsset() {
        return asset;
    }

    public boolean isSell() {
        return isSell;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }
}
