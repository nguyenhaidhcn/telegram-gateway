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
    OrderRequest orderCancel;
    boolean isCancel;
    public ThreadPlaceOrder(TradeService tradeService, OrderRequest orderRequest ,OrderRequest orderCancel, boolean isCancel) {
        this.tradeService = tradeService;
        this.orderRequest = orderRequest;
        this.orderCancel = orderCancel;
        this.isCancel = isCancel;
    }


    void Cancel()
    {
        try {

            System.out.println("Attempting to cancel order " + orderCancel.getOrderID());
            CancelOrderParams cancelParams = new KucoinCancelOrderParams(orderCancel.getPair(), orderCancel.getOrderID(), orderCancel.getType());
            boolean cancelled = tradeService.cancelOrder(cancelParams);

            if (cancelled) {
                System.out.println("Order successfully canceled.");
            } else {
                System.out.println("Order not successfully canceled.");
            }

//            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
            orderCancel.setResultCode(-1);
            orderCancel.setErrorMsg(e.getMessage());
        }
    }

    void open()
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

    @Override
    public void run() {

        if(isCancel)
        {
            this.Cancel();
        }
        else if(orderCancel == null)
        {
            //open only
            this.open();

        }
        else {
            //cancel first then open
            this.Cancel();

            this.open();
        }
    }
}
