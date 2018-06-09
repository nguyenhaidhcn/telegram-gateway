package com.snap.gateway.message;


import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

public class MsgRequest {

    int resultCode;
    String errorMsg;
    String exchange;
    String msgID;

    OrderRequest orderRequest;

    public MsgRequest(int resultCode, String errorMsg, String exchange, String msgID, OrderRequest orderRequest) {
        this.resultCode = resultCode;
        this.errorMsg = errorMsg;
        this.exchange = exchange;
        this.msgID = msgID;
        this.orderRequest = orderRequest;
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

    public OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }
}
