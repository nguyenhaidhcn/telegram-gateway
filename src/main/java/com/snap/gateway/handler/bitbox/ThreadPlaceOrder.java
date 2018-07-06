package com.snap.gateway.handler.bitbox;

import com.snap.gateway.message.OrderRequest;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.kucoin.service.KucoinCancelOrderParams;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

public class ThreadPlaceOrder  implements  Runnable{

    private static final Logger log = LoggerFactory.getLogger(BitboxHandler.class);

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
            Date star_date = new Date();
            log.info("Attempting to cancel order " + orderCancel.getOrderID());
            CancelOrderParams cancelParams = new KucoinCancelOrderParams(orderCancel.getPair(), orderCancel.getOrderID(), orderCancel.getType());
            boolean cancelled = tradeService.cancelOrder(cancelParams);
            Date finish_date = new Date();
            log.info("Time to canceled " +orderCancel.getOrderID()  +":"+ (finish_date.getTime() - star_date.getTime()));
            if (cancelled) {
                log.info("Order successfully canceled.");
            } else {
                log.error("Order not successfully canceled.");
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

            Date star_date = new Date();
            String uuid = tradeService.placeLimitOrder(limitOrder);
            System.out.println("Order successfully placed. ID=" + uuid);
            orderRequest.setOrderID(uuid);
            Date finish_date = new Date();
            log.info("Time to place: "+uuid +":" + (finish_date.getTime() - star_date.getTime()));

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
