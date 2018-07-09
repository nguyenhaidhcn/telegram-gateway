package com.snap.gateway.handler.bitbox;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.snap.gateway.common.Gateways;
import com.snap.gateway.jms.Sender;
import com.snap.gateway.message.MsgRequest;
import com.snap.gateway.message.Quote;
import com.snap.gateway.message.QuoteRequest;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.bitbox.BitboxExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.kucoin.service.KucoinCancelOrderParams;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.snap.gateway.handler.GatewayHandler;
import com.snap.gateway.message.OrderRequest;

enum SnapOrderType
{
	NOUSE,
	LIMIT,
	MARKET ,
	ALGO,
	MIDPOINT ,
	STOP 		,
	STOP_LIMIT 	,
	MTL 		,
	PASSIVE 	,
	ODDLOT 		,
	PASS2AGGR 	,
	FORCE 		,
	SHORTS 		,
	FPS 		, //FORCE + SHORT
	SESOPEN 	,
	SESPRECLS 	,
	OrderTypeMax

};

enum SnapOrderState
{
	NOUSE,
	PENDING,
	PLACED ,
	PARTIALFILL,
	FILLED 	,
	CXL_PENDING,
	CXLD 		,
	RPL_PENDING ,
	RPLD 		,
	REJECTED 	,
	REJECTED_SNAP ,
	CXL_REJ 	,
	RPL_REJ 	,
	RPLD_SNAP 	,
	EXPD 		,
	OrderStateMax
};

enum Side
{
	NOUSE,
	BUY 	,
	SELL 	,
	SideMax
}
@Component
public class BitboxHandler implements GatewayHandler {
	private static final Logger log = LoggerFactory.getLogger(BitboxHandler.class);
	private static long lastHitory = 0;

	@Value("${api.key}")
	private String ApiKey;

	@Value("${secret.key}")
	private String SecretKey;

	@Value("${OrderHedge.Topic}")
	private String OrderHedgeTopic;

	@Autowired
	private Sender sender;

	@Override
	public void trade(OrderRequest orderRequest)
	{
		ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("34387");
		exSpec.setApiKey("2PuZTxAeBVbXhyyt");
		exSpec.setSecretKey("S46DRo1z6IMPmSfCrNrbef5MpSL7RmYd");
		Exchange bitbox = ExchangeFactory.INSTANCE.createExchange(exSpec);

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
				this.OpenLimit(bitbox.getTradeService(), orderRequest);
			}
			else if(orderRequest.getOrderState() ==  SnapOrderState.PLACED.ordinal())
			{
				this.OpenLimit(bitbox.getTradeService(), orderRequest);
			}
			else if (orderRequest.getOrderState() == SnapOrderState.CXL_PENDING.ordinal())
			{
				this.CancelLimit(bitbox.getTradeService(), orderRequest);
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
		exSpec.setApiKey("5b120d483232925061499e66");
		exSpec.setSecretKey("7f6333b5-da43-4a08-ae77-7f580bb61981");
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


	public void CancelLimit(TradeService tradeService, OrderRequest orderRequest) throws IOException {

		ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("34387");
		exSpec.setApiKey("5b120d483232925061499e66");
		exSpec.setSecretKey("7f6333b5-da43-4a08-ae77-7f580bb61981");
		Exchange kucoin = ExchangeFactory.INSTANCE.createExchange(exSpec);


		try {

			System.out.println("Attempting to cancel order " + orderRequest.getOrderID());
			CancelOrderParams cancelParams = new KucoinCancelOrderParams(orderRequest.getPair(), orderRequest.getOrderID(), orderRequest.getType());
			boolean cancelled = tradeService.cancelOrder(cancelParams);

			if (cancelled) {
				System.out.println("Order successfully canceled.");
			} else {
				System.out.println("Order not successfully canceled.");
			}

//			Thread.sleep(7000); // wait for cancellation to propagate
//
//			System.out.println();
//			DefaultOpenOrdersParamCurrencyPair orderParams =
//					(DefaultOpenOrdersParamCurrencyPair) tradeService.createOpenOrdersParams();
//			orderParams.setCurrencyPair(orderRequest.getPair());
//			System.out.println(tradeService.getOpenOrders(orderParams));

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
	@Override
	public void getPublicData() throws IOException {
		log.info("Get public data from binance");
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BitboxExchange.class.getName());
		MarketDataService marketDataService = exchange.getMarketDataService();

		Ticker ticker = marketDataService.getTicker(new CurrencyPair("LTC", "BTC"));
		System.out.println(ticker.toString());

		OrderBook books = marketDataService.getOrderBook(new CurrencyPair("LTC", "BTC"), 10);
		System.out.println(books.toString());

	}


	/**
	 *
	 * @throws IOException
	 */
	@Override
	public void getPosition(MsgRequest request) {
		log.info("Get open position");

		try {
			ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
			exSpec.setUserName("34387");
			exSpec.setApiKey("2PuZTxAeBVbXhyyt");
			exSpec.setSecretKey("S46DRo1z6IMPmSfCrNrbef5MpSL7RmYd");
			Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);

			DefaultOpenOrdersParamCurrencyPair orderParams =
					(DefaultOpenOrdersParamCurrencyPair) exchange.getTradeService().createOpenOrdersParams();
			orderParams.setCurrencyPair(request.getOrderRequest().getPair());
			System.out.println(exchange.getTradeService().getOpenOrders(orderParams));

			request.setOpenOrders(exchange.getTradeService().getOpenOrders(orderParams));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			request.setResultCode(-1);
			request.setErrorMsg(ex.getMessage());
		}
	}

	@Override
	public void getHistory(MsgRequest request){
		log.info("Get history");
		try {
			ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
			exSpec.setUserName("34387");
			exSpec.setApiKey("2PuZTxAeBVbXhyyt");
			exSpec.setSecretKey("S46DRo1z6IMPmSfCrNrbef5MpSL7RmYd");
			Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);

			TradeHistoryParamsAll tradeHistoryParamsAll = new TradeHistoryParamsAll();
			tradeHistoryParamsAll.setCurrencyPair(request.getOrderRequest().getPair());

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));


			//set if last history = 0, init last history
			System.out.println("Lasthistory: " + lastHitory);
			if(lastHitory ==0) lastHitory =cal.getTimeInMillis();

			Date startTime = new Date(lastHitory);
			Date endTime = new Date(cal.getTimeInMillis());
			tradeHistoryParamsAll.setEndTime(endTime);
			tradeHistoryParamsAll.setStartTime(startTime);

			UserTrades userTrades = exchange.getTradeService().getTradeHistory(tradeHistoryParamsAll);
			request.setUserTrades(userTrades);
			lastHitory = endTime.getTime();
			System.out.println("Lasthistory: " + lastHitory);

			//hedging to binance
			if(userTrades.getUserTrades().size() == 0)
			{
				System.out.println("history null");
				return;
			}

			for (UserTrade userTrade:userTrades.getUserTrades()
				 ) {
				OrderRequest orderRequest = new OrderRequest();
				orderRequest.setPrice(userTrade.getPrice().toString());
				orderRequest.setBaseSymbol(userTrade.getCurrencyPair().base.getSymbol());
				orderRequest.setCounterSymbol(userTrade.getCurrencyPair().counter.getSymbol());
				orderRequest.setVolume(userTrade.getOriginalAmount().toString());
				orderRequest.setPair(userTrade.getCurrencyPair());
				Order.OrderType orderType = Order.OrderType.BID;
				int orderSide = -1;
				if (userTrade.getType() == Order.OrderType.BID) {
					orderType = Order.OrderType.ASK;
					orderSide = 2;
				}

				if (userTrade.getType() == Order.OrderType.ASK) {
					orderType = Order.OrderType.BID;
					orderSide = 1;
				}

				orderRequest.setType(orderType);
				orderRequest.setSide(orderSide);
				int orderState = SnapOrderState.PENDING.ordinal();
				orderRequest.setOrderState(orderState);

				MsgRequest msgRequest = new MsgRequest(0, "","","", 1,orderRequest,null, null);
				Gson gson = new Gson();

				log.info("Send hedge order to:"+ OrderHedgeTopic );
				sender.send(OrderHedgeTopic, gson.toJson(msgRequest));
			}


		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			request.setResultCode(-1);
			request.setErrorMsg(ex.getMessage());
		}
	}

	@Override
	public void quoteProcess(QuoteRequest quoteRequest)
	{
		log.info("quoteProcess");

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
		MsgRequest msgRequest = new MsgRequest(0, "","", "",0,orderRequest, null, null);

		//get position
		this.getPosition(msgRequest);
		OpenOrders openOrders = msgRequest.getOpenOrders();



		if(openOrders.getOpenOrders() == null)
		{
			openOrders = new OpenOrders(new ArrayList<>(), new ArrayList<>());
		}

		//syc bids
		List<Quote> quotes = quoteRequest.bids;
		List<LimitOrder> limitOrders = new ArrayList<>(openOrders.getOpenOrders());
		SycBidAsk(bitbox.getTradeService()
				, quotes ,
				Order.OrderType.BID,
				limitOrders,
				orderRequest,
				quoteRequest.digit);

		//syc ask
		quotes = quoteRequest.asks;
		limitOrders = limitOrders = new ArrayList<>(openOrders.getOpenOrders());
		SycBidAsk(bitbox.getTradeService()
				, quotes ,
				Order.OrderType.ASK,
				limitOrders,
				orderRequest,
				quoteRequest.digit);


//		//cancel order
//		for (LimitOrder limitOrder:openOrders.getOpenOrders()
//				) {
//
//			orderRequest.setOrderID(limitOrder.getId());
//			try {
//				this.CancelLimit(bitbox.getTradeService(), orderRequest);
//			} catch (IOException e) {
//
//				e.printStackTrace();
//				log.error("cannot cancel order:", orderRequest.getOrderID());
//			}
//
//		}
//
//		//open buy.
//		for (Quote quote:quoteRequest.bids
//			 ) {
//			try {
//				orderRequest.setType(Order.OrderType.BID);
//				orderRequest.setVolume(quote.quantity.toString());
//				BigDecimal price = quote.price.setScale(quoteRequest.digit, BigDecimal.ROUND_HALF_UP);
//				orderRequest.setPrice(price.toString());
//				this.OpenLimit(bitbox.getTradeService(), orderRequest);
//			} catch (IOException e)
//			{
//				log.error(e.getMessage());
//				log.error("Cannot place order");
//			}
//
//		}
//
//		//open sell
//		for (Quote quote:quoteRequest.asks
//				) {
//			try {
//				orderRequest.setType(Order.OrderType.ASK);
//				orderRequest.setVolume(quote.quantity.toString());
//				BigDecimal price = quote.price.setScale(quoteRequest.digit, BigDecimal.ROUND_HALF_UP);
//				orderRequest.setPrice(price.toString());
//				this.OpenLimit(bitbox.getTradeService(), orderRequest);
//			} catch (IOException e)
//			{
//				log.error(e.getMessage());
//				log.error("Cannot place order");
//			}
//
//		}

		//get history check hedging
//		this.getHistory(msgRequest);

	}

	public void SycBidAsk(TradeService tradeService,
							 List<Quote> quotes,
							 Order.OrderType orderType,
							 List<LimitOrder> limitOrders,
							 OrderRequest orderRequest,
						     Integer digit){
		//filter order type
		Iterator iteratorOrder = limitOrders.iterator();
		while (iteratorOrder.hasNext())
		{
			LimitOrder limitOrder = (LimitOrder)iteratorOrder.next();
			if(limitOrder.getType() != orderType)
//				limitOrders.remove(limitOrder);
				iteratorOrder.remove();
		}

		//compare side bid ask.
		if(quotes.size() != limitOrders.size())
		{
			log.error("Diffirent quote size: " + quotes.size() + "limit size:" + limitOrders.size());
			//do cancel all, reopen all
		}

		//make compare. remove quote, limit order equal
		Iterator quoteInterator = quotes.iterator();

		while (quoteInterator.hasNext())
		{
			Quote quote = (Quote)quoteInterator.next();
			//looking Limit order.
			Iterator limitOrderInterator = limitOrders.iterator();
			while (limitOrderInterator.hasNext())
			{
				LimitOrder limitOrder = (LimitOrder)limitOrderInterator.next();
				if (quote.quantity == limitOrder.getOriginalAmount()
						&& quote.price == limitOrder.getLimitPrice())
				{
					quoteInterator.remove();
					limitOrderInterator.remove();
				}
			}
		}

		//do cancel, open one by one
		for(LimitOrder limitOrder:limitOrders)
		{
			//do cancel
			orderRequest.setOrderID(limitOrder.getId());
			try {
				this.CancelLimit(tradeService, orderRequest);
			} catch (IOException e) {

				e.printStackTrace();
				log.error("cannot cancel order:", orderRequest.getOrderID());
			}

			//do open.
			if(quotes.size() > 0) {
				Quote quote = quotes.get(0);
				try {
					orderRequest.setType(orderType);
					orderRequest.setVolume(quote.quantity.toString());
					BigDecimal price = quote.price.setScale(digit, BigDecimal.ROUND_HALF_UP);
					orderRequest.setPrice(price.toString());
					this.OpenLimit(tradeService, orderRequest);
				} catch (IOException e) {
					log.error(e.getMessage());
					log.error("Cannot place order");
				}

				quotes.remove(quote);
			}
		}


		//do open if remain.
		if(quotes.size() > 0) {
			for(Quote quote:quotes) {
				try {
					orderRequest.setType(orderType);
					orderRequest.setVolume(quote.quantity.toString());
					BigDecimal price = quote.price.setScale(digit, BigDecimal.ROUND_HALF_UP);
					orderRequest.setPrice(price.toString());
					this.OpenLimit(tradeService, orderRequest);
				} catch (IOException e) {
					log.error(e.getMessage());
					log.error("Cannot place order");
				}
			}
		}



	}


}
