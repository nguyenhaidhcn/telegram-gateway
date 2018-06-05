package com.snap.gateway.message;


import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

public class OrderRequest {
    String baseSymbol;
    String counterSymbol;
    String volume;
    String price;

    //buy sell...
    Order.OrderType type;

    //new, cancel..
    Order.OrderStatus status;

    CurrencyPair pair;
    String orderID;
    String msgID;
    int resultCode;
    String errorMsg;

    public OrderRequest(String msg) {


    }

    public OrderRequest(String baseSymbol, String counterSymbol, String volume, String price, Order.OrderType type, Order.OrderStatus status, CurrencyPair pair, String orderID, String msgID, int resultCode, String errorMsg) {
        this.baseSymbol = baseSymbol;
        this.counterSymbol = counterSymbol;
        this.volume = volume;
        this.price = price;
        this.type = type;
        this.status = status;
        this.pair = pair;
        this.orderID = orderID;
        this.msgID = msgID;
        this.resultCode = resultCode;
        this.errorMsg = errorMsg;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Order.OrderType getType() {
        return type;
    }

    public void setType(Order.OrderType type) {
        this.type = type;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }

    public CurrencyPair getPair() {
        return pair;
    }

    public void setPair(CurrencyPair pair) {
        this.pair = pair;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
