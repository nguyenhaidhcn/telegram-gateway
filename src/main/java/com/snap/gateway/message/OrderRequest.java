package com.snap.gateway.message;


import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

public class OrderRequest {
    String baseSymbol;
    String counterSymbol;
    String volume;
    String price;

    //buy sell...
    int side;

    //buy sell...
    Order.OrderType type;

    //new, cancel..
    int orderState;

    CurrencyPair pair;
    String orderID;
    String msgID;
    int resultCode;
    String errorMsg;


    public OrderRequest(String msg) {


    }

    public OrderRequest() {


    }

    public Order.OrderType getType() {
        return type;
    }

    public void setType(Order.OrderType type) {
        this.type = type;
    }

    public OrderRequest(String baseSymbol, String counterSymbol, String volume, String price, int side, int orderState, CurrencyPair pair, String orderID, String msgID, int resultCode, String errorMsg) {
        this.baseSymbol = baseSymbol;
        this.counterSymbol = counterSymbol;
        this.volume = volume;
        this.price = price;
        this.side = side;
        this.orderState = orderState;
        this.pair = pair;
        this.orderID = orderID;
        this.msgID = msgID;
        this.resultCode = resultCode;
        this.errorMsg = errorMsg;
    }


    public OrderRequest( OrderRequest request) {
        this.baseSymbol = request.baseSymbol;
        this.counterSymbol = request.counterSymbol;
        this.volume = request.volume;
        this.price = request.price;
        this.side = request.side;
        this.orderState = request.orderState;
        this.pair = request.pair;
        this.orderID = request.orderID;
        this.msgID = request.msgID;
        this.resultCode = request.resultCode;
        this.errorMsg = request.errorMsg;
        this.type = request.type;
    }



    public String getBaseSymbol() {
        return baseSymbol;
    }

    public void setBaseSymbol(String baseSymbol) {
        this.baseSymbol = baseSymbol;
    }

//    public Object clone() throws CloneNotSupportedException {
//        return super.clone();
//    }

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

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
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
