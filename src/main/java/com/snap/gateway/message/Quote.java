package com.snap.gateway.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Quote

{

    public final BigDecimal price;
    public final BigDecimal quantity;

    public Quote(
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("quantity") BigDecimal quantity) {
        super();
        this.price = price;
        this.quantity = quantity;
    }
}
