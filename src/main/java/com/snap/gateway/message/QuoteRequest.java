package com.snap.gateway.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class QuoteRequest {
    public final String symbol;
    public final String baseSymbol;
    public final String counterSymbol;
    public final List<Quote> asks;
    public final List<Quote> bids;
    public final Integer digit;
    public final String exchange;
    public final String msgType;

    public QuoteRequest(
            @JsonProperty("symbol") String symbol,
            @JsonProperty("baseSymbol") String baseSymbol,
            @JsonProperty("counterSymbol") String counterSymbol,
            @JsonProperty("asks") List<Quote> asks,
            @JsonProperty("bids") List<Quote> bids,
            @JsonProperty("digit") Integer digit,
            @JsonProperty("exchange") String exchange,
            @JsonProperty("msgType") String msgType) {
        super();
        this.symbol = symbol;
        this.asks = asks;
        this.bids = bids;
        this.digit = digit;
        this.exchange = exchange;
        this.msgType = msgType;
        this.baseSymbol = baseSymbol;
        this.counterSymbol = counterSymbol;
    }
}
