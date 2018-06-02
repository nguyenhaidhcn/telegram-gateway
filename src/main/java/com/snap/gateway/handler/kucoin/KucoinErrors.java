package com.snap.gateway.handler.kucoin;

import com.fasterxml.jackson.annotation.JsonValue;

public enum KucoinErrors {
	SUCCCESS("200");
	
	
	private final String value;
	KucoinErrors(String value) {
        this.value = value;
    }

	@JsonValue
    public String value() {
        return value;
    }

}
