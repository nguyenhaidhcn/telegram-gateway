package com.snap.gateway.handler.binance;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BinanceErrors {
	SUCCCESS("200");
	
	
	private final String value;
	BinanceErrors(String value) {
        this.value = value;
    }

	@JsonValue
    public String value() {
        return value;
    }

}
