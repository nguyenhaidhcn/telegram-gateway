package com.snap.gateway.handler.bitbox;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BitboxErrors {
	SUCCCESS("200");
	
	
	private final String value;
	BitboxErrors(String value) {
        this.value = value;
    }

	@JsonValue
    public String value() {
        return value;
    }

}
