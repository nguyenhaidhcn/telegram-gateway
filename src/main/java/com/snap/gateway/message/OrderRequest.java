package com.snap.gateway.message;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

public class OrderRequest {
    String baseSymbol;
    String counterSymbol;
    double volume;
    Order.OrderType type;
    CurrencyPair pair;
    String orderID;


    public OrderRequest(String baseSymbol, String counterSymbol, double volume, Order.OrderType type, String orderID) {
        this.baseSymbol = baseSymbol;
        this.counterSymbol = counterSymbol;
        this.volume = volume;
        this.type = type;
        this.orderID = orderID;
        pair = new CurrencyPair(baseSymbol, counterSymbol);
    }


    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public CurrencyPair getPair() {
        return pair;
    }

    public void setPair(CurrencyPair pair) {
        this.pair = pair;
    }

    public String getBaseSymbol() {
        return baseSymbol;
    }

    public void setBaseSymbol(String baseSymbol) {
        this.baseSymbol = baseSymbol;
    }

    public String getCounterSymbol() {
        return counterSymbol;
    }

    public void setCounterSymbol(String counterSymbol) {
        this.counterSymbol = counterSymbol;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public Order.OrderType getType() {
        return type;
    }

    public void setType(Order.OrderType type) {
        this.type = type;
    }
}
