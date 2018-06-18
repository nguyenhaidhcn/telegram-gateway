package com.snap.gateway.message;


import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;

public class MsgRequest {

    int resultCode;
    String errorMsg;
    String exchange;
    String msgID;
    int msgType;


    OrderRequest orderRequest;

    OpenOrders openOrders;

    public MsgRequest(int resultCode, String errorMsg, String exchange, String msgID, int msgType, OrderRequest orderRequest, OpenOrders openOrders) {

        this.resultCode = resultCode;
        this.errorMsg = errorMsg;
        this.exchange = exchange;
        this.msgID = msgID;
        this.msgType = msgType;
        this.orderRequest = orderRequest;
        this.openOrders = openOrders;
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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public OpenOrders getOpenOrders() {
        return openOrders;
    }

    public void setOpenOrders(OpenOrders openOrders) {
        this.openOrders = openOrders;
    }
}
