package com.snap.gateway;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "balance")
@EntityListeners(AuditingEntityListener.class)
public class BalanceDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currency;

    private String account;

    private String email;

    private BigDecimal balance;

    private Long updateId;

    private BigDecimal rate_eth;

    private BigDecimal rate_btc;

    private BigDecimal estimate_eth;

    private BigDecimal estimate_btc;

    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BalanceDB() {
    }

    @Override
    public String toString() {
        return "BalanceDB{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", account='" + account + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                ", updateId=" + updateId +
                ", rate_eth=" + rate_eth +
                ", rate_btc=" + rate_btc +
                ", estimate_eth=" + estimate_eth +
                ", estimate_btc=" + estimate_btc +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    public BigDecimal getRate_eth() {
        return rate_eth;
    }

    public void setRate_eth(BigDecimal rate_eth) {
        this.rate_eth = rate_eth;
    }

    public BigDecimal getRate_btc() {
        return rate_btc;
    }

    public void setRate_btc(BigDecimal rate_btc) {
        this.rate_btc = rate_btc;
    }

    public BigDecimal getEstimate_eth() {
        return estimate_eth;
    }

    public void setEstimate_eth(BigDecimal estimate_eth) {
        this.estimate_eth = estimate_eth;
    }

    public BigDecimal getEstimate_btc() {
        return estimate_btc;
    }

    public void setEstimate_btc(BigDecimal estimate_btc) {
        this.estimate_btc = estimate_btc;
    }
}

