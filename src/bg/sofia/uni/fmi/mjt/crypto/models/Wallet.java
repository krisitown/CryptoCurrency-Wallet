package bg.sofia.uni.fmi.mjt.crypto.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Wallet implements Serializable {
    private Map<String, Double> assets;
    private List<Order> ledger;

    public Wallet() {
        this.assets = new HashMap<>();
        this.assets.put("USD", 0.0);
        this.ledger = new LinkedList<>();
    }

    public Map<String, Double> getAssets() {
        return assets;
    }

    public void addOrder(Order order) {
        this.ledger.add(order);
    }

    public List<Order> getLedger() {
        return this.ledger;
    }
}
