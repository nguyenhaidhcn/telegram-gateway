package com.snap.gateway.message;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Errors {
	SUCCCESS("200"),
	ERROR("400");
	
	
	private final String value;
	Errors(String value) {
        this.value = value;
    }

	@JsonValue
    public String value() {
        return value;
    }

}
