package com.snap.gateway.handler.bittrex;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BittrexErrors {
	SUCCCESS("200");
	
	
	private final String value;
	BittrexErrors(String value) {
        this.value = value;
    }

	@JsonValue
    public String value() {
        return value;
    }

}
