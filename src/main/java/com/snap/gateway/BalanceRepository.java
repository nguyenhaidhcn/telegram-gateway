package com.snap.gateway;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by rajeevkumarsingh on 27/06/17.
 */

@Repository
public interface BalanceRepository extends JpaRepository<BalanceDB, Long> {

    @Query("SELECT b FROM BalanceDB b  WHERE currency = :currency and email = :email")
    public List<BalanceDB> find(@Param("currency") String currency, @Param("email") String email);

    @Query("SELECT b FROM BalanceDB b  WHERE currency = :currency and account = :account")
    public BalanceDB findRate(@Param("currency") String currency, @Param("account") String account);

    @Query("SELECT sum(estimate_btc),sum(estimate_eth) FROM BalanceDB  WHERE  email = :email")
    public String sumBalance(@Param("email") String email);

    @Query("SELECT sum(estimate_btc),sum(estimate_eth) FROM BalanceDB")
    public String totalBalance();
}
