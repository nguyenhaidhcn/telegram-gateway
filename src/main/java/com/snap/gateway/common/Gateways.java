package com.snap.gateway.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gateways {
	_1broker("1Broker"),
	_1btcxe("1BTCXE"),
	acx("ACX"),
	allcoin("Allcoin"),
	anxpro("ANXPro"),
	bibox("Bibox"),
	binance("Binance"),
	kucoin("Kucoin"),
	bittrex("Bittrex");

	private final String name;
	Gateways(String name) {
        this.name = name;
    }

	@JsonValue
    public String value() {
        return name;
    }
}


