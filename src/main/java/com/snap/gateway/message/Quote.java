package com.snap.gateway.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Quote

{

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public  BigDecimal price;
    public  BigDecimal quantity;

    public Quote(
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("quantity") BigDecimal quantity) {
        super();
        this.price = price;
        this.quantity = quantity;
    }
}
