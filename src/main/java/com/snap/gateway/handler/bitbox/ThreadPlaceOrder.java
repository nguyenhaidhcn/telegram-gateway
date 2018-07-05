package com.snap.gateway.handler.bitbox;

import com.snap.gateway.message.OrderRequest;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.kucoin.service.KucoinCancelOrderParams;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;

import java.math.BigDecimal;

public class ThreadPlaceOrder  implements  Runnable{

    TradeService tradeService;
    OrderRequest orderRequest;
    boolean isCancel;
    public ThreadPlaceOrder(TradeService tradeService, OrderRequest orderRequest , boolean isCancel) {
        this.tradeService = tradeService;
        this.orderRequest = orderRequest;
        this.isCancel = isCancel;
    }


    @Override
    public void run() {

        if(isCancel)
        {
            try {

                System.out.println("Attempting to cancel order " + orderRequest.getOrderID());
                CancelOrderParams cancelParams = new KucoinCancelOrderParams(orderRequest.getPair(), orderRequest.getOrderID(), orderRequest.getType());
                boolean cancelled = tradeService.cancelOrder(cancelParams);

                if (cancelled) {
                    System.out.println("Order successfully canceled.");
                } else {
                    System.out.println("Order not successfully canceled.");
                }


            } catch (Exception e) {
                e.printStackTrace();
                orderRequest.setResultCode(-1);
                orderRequest.setErrorMsg(e.getMessage());
            }
        }
        else
        {
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
}
