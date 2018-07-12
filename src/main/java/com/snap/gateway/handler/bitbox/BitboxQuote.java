package com.snap.gateway.handler.bitbox;

import com.google.gson.Gson;
import com.snap.gateway.message.MsgRequest;
import com.snap.gateway.message.OrderRequest;
import com.snap.gateway.message.Quote;
import com.snap.gateway.message.QuoteRequest;
import org.apache.commons.lang3.SerializationUtils;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitbox.BitboxExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.kucoin.service.KucoinCancelOrderParams;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class BitboxQuote implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(BitboxQuote.class);
    private static long lastHitory = 0;
    private final String symbol;
    private final Integer digit;
    private int count = 0;
    private long lastID = 0;
    public BitboxQuote(String sym, Integer digit) {
        this.digit = digit;
        this.symbol = sym;
    }

    public void trade(OrderRequest orderRequest, TradeService tradeService)
    {
        try
        {
            if (orderRequest.getSide() == Side.BUY.ordinal())
            {
                orderRequest.setType(Order.OrderType.BID);
            }
            else
                orderRequest.setType(Order.OrderType.ASK);

            if(orderRequest.getOrderState() ==  SnapOrderState.PENDING.ordinal())
            {
                this.OpenLimit(tradeService, orderRequest);
            }
            else if(orderRequest.getOrderState() ==  SnapOrderState.PLACED.ordinal())
            {
                this.OpenLimit(tradeService, orderRequest);
            }
            else if (orderRequest.getOrderState() == SnapOrderState.CXL_PENDING.ordinal())
            {
                this.CancelLimit(tradeService, orderRequest, false);
            }
            else {
                orderRequest.setErrorMsg("not support order State");
                orderRequest.setResultCode(-1);
            }


        }
        catch (IOException e)
        {
            e.printStackTrace();
            orderRequest.setResultCode(-1);
            orderRequest.setErrorMsg(e.getMessage());
        }
    }


    public void OpenLimit(TradeService tradeService, OrderRequest orderRequest) throws IOException {

        ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
        exSpec.setUserName("34387");
        exSpec.setApiKey("2PuZTxAeBVbXhyyt");
        exSpec.setSecretKey("S46DRo1z6IMPmSfCrNrbef5MpSL7RmYd");
        Exchange kucoin = ExchangeFactory.INSTANCE.createExchange(exSpec);


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


    public void CancelLimit(TradeService tradeService, OrderRequest orderRequest , boolean isALL) throws IOException {

        try {

            System.out.println("Attempting to cancel order " + orderRequest.getOrderID());
            CancelOrderParams cancelParams = new KucoinCancelOrderParams(orderRequest.getPair(), isALL ? "": orderRequest.getOrderID(), orderRequest.getType());
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



    /**
     *
     * @throws IOException
     */
    public void getPosition(MsgRequest request, TradeService tradeService) {
        log.info("Get open position");

        try {
            DefaultOpenOrdersParamCurrencyPair orderParams =
                    (DefaultOpenOrdersParamCurrencyPair) tradeService.createOpenOrdersParams();
            orderParams.setCurrencyPair(request.getOrderRequest().getPair());
//            System.out.println(tradeService.getOpenOrders(orderParams));

            request.setOpenOrders(tradeService.getOpenOrders(orderParams));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            request.setResultCode(-1);
            request.setErrorMsg(ex.getMessage());
        }
    }

    //solution 2. cancel all the place new.
    public void quoteProcess_2(QuoteRequest quoteRequest)
    {
        try {


            log.info("quoteProcess solution 2. cancel all then place new by multithread.");

            ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
            exSpec.setUserName("34387");
            exSpec.setApiKey("2PuZTxAeBVbXhyyt");
            exSpec.setSecretKey("S46DRo1z6IMPmSfCrNrbef5MpSL7RmYd");
            Exchange bitbox = ExchangeFactory.INSTANCE.createExchange(exSpec);


            //get open order
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setBaseSymbol(quoteRequest.baseSymbol);
            orderRequest.setCounterSymbol(quoteRequest.counterSymbol);
            orderRequest.setPair(new CurrencyPair(quoteRequest.baseSymbol, quoteRequest.counterSymbol));
            MsgRequest msgRequest = new MsgRequest(0, "", "", "", 0, orderRequest, null, null);

            this.CancelLimit(bitbox.getTradeService(), orderRequest, true);

            //create thread here to place order.
            //		//open buy.
		for (Quote quote:quoteRequest.bids
			 ) {
				orderRequest.setType(Order.OrderType.BID);
				orderRequest.setVolume(quote.quantity.toString());
				BigDecimal price = quote.price.setScale(quoteRequest.digit, BigDecimal.ROUND_HALF_UP);
				orderRequest.setPrice(price.toString());
//				this.OpenLimit(bitbox.getTradeService(), orderRequest);
                OrderRequest order = new OrderRequest(orderRequest);
                ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(bitbox.getTradeService(), order, null, false);
                new Thread(threadPlaceOrder).start();
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
		}

            Thread.sleep(5);
		for (Quote quote:quoteRequest.bids
                    ) {
                orderRequest.setType(Order.OrderType.BID);
                orderRequest.setVolume(quote.quantity.toString());
                BigDecimal price = quote.price.setScale(quoteRequest.digit, BigDecimal.ROUND_HALF_UP);
                price = price.add(BigDecimal.valueOf(0.001));
                orderRequest.setPrice(price.toString());
//				this.OpenLimit(bitbox.getTradeService(), orderRequest);
                OrderRequest order = new OrderRequest(orderRequest);
                ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(bitbox.getTradeService(), order, null,false);
                new Thread(threadPlaceOrder).start();
            }
            Thread.sleep(5);
            for (Quote quote:quoteRequest.bids
                    ) {
                orderRequest.setType(Order.OrderType.BID);
                orderRequest.setVolume(quote.quantity.toString());
                BigDecimal price = quote.price.setScale(quoteRequest.digit, BigDecimal.ROUND_HALF_UP);
                price = price.add(BigDecimal.valueOf(0.002));
                orderRequest.setPrice(price.toString());
//				this.OpenLimit(bitbox.getTradeService(), orderRequest);
                OrderRequest order = new OrderRequest(orderRequest);
                ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(bitbox.getTradeService(), order, null,false);
                new Thread(threadPlaceOrder).start();
            }
            Thread.sleep(5);
		//open sell
		for (Quote quote:quoteRequest.asks
				) {
				orderRequest.setType(Order.OrderType.ASK);
				orderRequest.setVolume(quote.quantity.toString());
				BigDecimal price = quote.price.setScale(quoteRequest.digit, BigDecimal.ROUND_HALF_UP);
				orderRequest.setPrice(price.toString());

                OrderRequest order = new OrderRequest(orderRequest);
                ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(bitbox.getTradeService(), order, null,false);
                new Thread(threadPlaceOrder).start();
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
		}
            Thread.sleep(5);
            for (Quote quote:quoteRequest.asks
                    ) {
                orderRequest.setType(Order.OrderType.ASK);
                orderRequest.setVolume(quote.quantity.toString());
                BigDecimal price = quote.price.setScale(quoteRequest.digit, BigDecimal.ROUND_HALF_UP);
                price = price.add(BigDecimal.valueOf(0.001));
                orderRequest.setPrice(price.toString());

                OrderRequest order = new OrderRequest(orderRequest);
                ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(bitbox.getTradeService(), order, null,false);
                new Thread(threadPlaceOrder).start();
            }
            Thread.sleep(5);
            for (Quote quote:quoteRequest.asks
                    ) {
                orderRequest.setType(Order.OrderType.ASK);
                orderRequest.setVolume(quote.quantity.toString());
                BigDecimal price = quote.price.setScale(quoteRequest.digit, BigDecimal.ROUND_HALF_UP);
                price = price.add(BigDecimal.valueOf(0.002));
                orderRequest.setPrice(price.toString());

                OrderRequest order = new OrderRequest(orderRequest);
                ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(bitbox.getTradeService(), order, null,false);
                new Thread(threadPlaceOrder).start();
            }


        Thread.sleep(100);
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }

    }

    //solution one. cancel one place one.
    public void quoteProcess_1(QuoteRequest quoteRequest)
    {
        try {
            log.info("quoteProcess solution 1. cancel one then place one");
            ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
            exSpec.setUserName("34387");
            exSpec.setApiKey("2PuZTxAeBVbXhyyt");
            exSpec.setSecretKey("S46DRo1z6IMPmSfCrNrbef5MpSL7RmYd");
            Exchange bitbox = ExchangeFactory.INSTANCE.createExchange(exSpec);


            //get open order
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setBaseSymbol(quoteRequest.baseSymbol);
            orderRequest.setCounterSymbol(quoteRequest.counterSymbol);
            orderRequest.setPair(new CurrencyPair(quoteRequest.baseSymbol, quoteRequest.counterSymbol));
            MsgRequest msgRequest = new MsgRequest(0, "", "", "", 0, orderRequest, null, null);

            //get position
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.getPosition(msgRequest, bitbox.getTradeService());

            OpenOrders openOrders = msgRequest.getOpenOrders();

            if (openOrders.getOpenOrders() == null) {
                openOrders = new OpenOrders(new ArrayList<>(), new ArrayList<>());
                log.info("openOrders 0");
            }

            //syc bids
            List<Quote> quotes = new ArrayList<>(quoteRequest.bids);
            List<LimitOrder> limitOrders = new ArrayList<>(openOrders.getOpenOrders());
            SycBidAsk(bitbox.getTradeService()
                    , quotes,
                    Order.OrderType.BID,
                    limitOrders,
                    orderRequest,
                    this.digit);


            //syc ask
            quotes = new ArrayList<>(quoteRequest.asks);
            limitOrders = limitOrders = new ArrayList<>(openOrders.getOpenOrders());
            SycBidAsk(bitbox.getTradeService()
                    , quotes,
                    Order.OrderType.ASK,
                    limitOrders,
                    orderRequest,
                    this.digit);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error(String.valueOf(e.getStackTrace()));
        }


    }

    public void SycBidAsk(TradeService tradeService,
                          List<Quote> quotes,
                          Order.OrderType orderType,
                          List<LimitOrder> limitOrdersInput,
                          OrderRequest orderRequest,
                          Integer digit){
        List<LimitOrder> limitOrders = new ArrayList<>();
        //filter order type
//        Iterator iteratorOrder = limitOrders.iterator();
        for (LimitOrder limitOrder:limitOrdersInput)
        {
            if(limitOrder.getType() == orderType)
//				limitOrders.remove(limitOrder);
                limitOrders.add(limitOrder);
        }

        //compare side bid ask.
        if(quotes.size() > limitOrders.size())
        {
            log.error("quotesize_bigger: " + quotes.size() + "limit size:" + limitOrders.size());
            //do cancel all, reopen all
//            if(quotes.size() == 0)
//                return;
        }
        else if (quotes.size() < limitOrders.size())
        {
            log.info("quotesize_small: " + quotes.size() + "limit size:" + limitOrders.size());
        }

        //make compare. remove quote, limit order equal
        Iterator quoteInterator = quotes.iterator();

        while (quoteInterator.hasNext())
        {
            Quote quote = (Quote)quoteInterator.next();
            quote.price = quote.price.setScale(digit, BigDecimal.ROUND_HALF_UP);
            //looking Limit order.
            Iterator limitOrderInterator = limitOrders.iterator();
            while (limitOrderInterator.hasNext())
            {
                LimitOrder limitOrder = (LimitOrder)limitOrderInterator.next();
                if (quote.quantity.compareTo(limitOrder.getOriginalAmount()) == 0
                        && quote.price.compareTo(limitOrder.getLimitPrice()) == 0)
                {
                    try {
                        quoteInterator.remove();
                        limitOrderInterator.remove();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        //do cancel, open one by one
        Collections.sort(limitOrders, (a, b) -> a.getLimitPrice().compareTo(b.getLimitPrice()));
        Collections.sort(quotes, (a, b) -> a.price.compareTo(b.price));

        for(LimitOrder limitOrder:limitOrders)
        {
            //do cancel
            orderRequest.setOrderID(limitOrder.getId());

            OrderRequest orderCancel = new OrderRequest(orderRequest);

//            try {
//                this.CancelLimit(tradeService, orderCancel, false);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ThreadPlaceOrder threadPlaceOrderCancel = new ThreadPlaceOrder(tradeService, orderCancel, true);
//            new Thread(threadPlaceOrderCancel).start();
//            try {
//                Thread.sleep();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            //do open.
            if(quotes.size() > 0) {
                Quote quote = quotes.get(0);
                orderRequest.setType(orderType);
                orderRequest.setVolume(quote.quantity.toString());
                BigDecimal price = quote.price.setScale(digit, BigDecimal.ROUND_HALF_UP);
                orderRequest.setPrice(price.toString());
//                this.OpenLimit(tradeService, orderRequest);
                OrderRequest order = new OrderRequest(orderRequest);
                ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(tradeService, order, orderCancel,false);
                new Thread(threadPlaceOrder).start();
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                quotes.remove(quote);
            }
            else
            {
                //do cancel only.
                ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(tradeService, null, orderCancel,true);
                new Thread(threadPlaceOrder).start();
            }
        }

        //do open if remain.
        if(quotes.size() > 0) {
            for(Quote quote:quotes) {

                    log.warn("open remain");
                    orderRequest.setType(orderType);
                    orderRequest.setVolume(quote.quantity.toString());
                    BigDecimal price = quote.price.setScale(digit, BigDecimal.ROUND_HALF_UP);
                    orderRequest.setPrice(price.toString());
                    OrderRequest order = new OrderRequest(orderRequest);
                    ThreadPlaceOrder threadPlaceOrder = new ThreadPlaceOrder(tradeService, order, null, false);
                    new Thread(threadPlaceOrder).start();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }



    }

    @Override
    public void run() {
        while (true)
        {
            try
            {
//                log.info("try get quote:" + symbol);
                Map<String, QuoteRequest> quoteRequestMap = ShareObjectQuote.getCopy();
                QuoteRequest quoteRequest = quoteRequestMap.get(symbol);
                if (quoteRequest == null) {
                    Thread.sleep(50);
                    continue;
                }

                //check lastID
                if(this.lastID == quoteRequest.id && this.lastID >0)
                {
                    Thread.sleep(50);
                    continue;
                }

                this.lastID = quoteRequest.id;
                count ++;
                log.info(quoteRequest.symbol +":" + count);
                this.quoteProcess_1(quoteRequest);
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }

    }
}
