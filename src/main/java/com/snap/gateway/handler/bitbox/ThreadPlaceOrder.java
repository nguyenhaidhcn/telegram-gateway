package com.snap.gateway.handler.bitbox;

import com.snap.gateway.message.OrderRequest;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.trade.TradeService;

import java.math.BigDecimal;

public class ThreadPlaceOrder  implements  Runnable{

    TradeService tradeService;
    OrderRequest orderRequest;

    public ThreadPlaceOrder(TradeService tradeService, OrderRequest orderRequest) {
        this.tradeService = tradeService;
        this.orderRequest = orderRequest;
    }


    @Override
    public void run() {
        LimitOrder limitOrder =
                new LimitOrder.Builder(orderRequest.getType(), orderRequest.getPair())
                        .limitPrice(new BigDecimal(orderRequest.getPrice()))
                        .originalAmount(new BigDecimal(orderRequest.getVolume()))
                        .build();

        try {
            String uuid = tradeService.placeLimitOrder(limitOrder);
            System.out.println("Order successfully placed. ID=" + uuid);
            orderRequest.setOrderID(uuid);

        } catch (Exception e) {
            e.printStackTrace();
            orderRequest.setResultCode(-1);
            orderRequest.setErrorMsg(e.getMessage());
        }
    }
}
