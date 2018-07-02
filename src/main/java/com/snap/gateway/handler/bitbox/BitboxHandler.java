package com.snap.gateway.handler.bitbox;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import com.snap.gateway.common.Gateways;
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

	@Value("${api.key}")
	private String ApiKey;

	@Value("${secret.key}")
	private String SecretKey;

	@Override
	public void trade(OrderRequest orderRequest)
	{
		ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("34387");
		exSpec.setApiKey(ApiKey);
		exSpec.setSecretKey(SecretKey);
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
			exSpec.setApiKey(ApiKey);
			exSpec.setSecretKey(SecretKey);
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
			exSpec.setApiKey(ApiKey);
			exSpec.setSecretKey(SecretKey);
			Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);

			TradeHistoryParamsAll tradeHistoryParamsAll = new TradeHistoryParamsAll();
			tradeHistoryParamsAll.setCurrencyPair(request.getOrderRequest().getPair());
			Date endTime = new Date();
			Date startTime = new Date(endTime.getTime()-10000);
			tradeHistoryParamsAll.setEndTime(endTime);
			tradeHistoryParamsAll.setStartTime(startTime);

			UserTrades userTrades = exchange.getTradeService().getTradeHistory(tradeHistoryParamsAll);
			request.setUserTrades(userTrades);
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
		//get open order
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setBaseSymbol(quoteRequest.baseSymbol);
		orderRequest.setCounterSymbol(quoteRequest.counterSymbol);
		orderRequest.setPair(new CurrencyPair(quoteRequest.baseSymbol, quoteRequest.counterSymbol));
		MsgRequest msgRequest = new MsgRequest(0, "","", "",0,orderRequest, null, null);

		//get position
		this.getPosition(msgRequest);
		OpenOrders openOrders = msgRequest.getOpenOrders();

		//cancel order
		ExchangeSpecification exSpec = new BitboxExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("34387");
		exSpec.setApiKey(ApiKey);
		exSpec.setSecretKey(SecretKey);
		Exchange bitbox = ExchangeFactory.INSTANCE.createExchange(exSpec);

		for (LimitOrder limitOrder:openOrders.getOpenOrders()
			 ) {

			orderRequest.setOrderID(limitOrder.getId());
			try {
				this.CancelLimit(bitbox.getTradeService(), orderRequest);
			} catch (IOException e) {

				e.printStackTrace();
				log.error("cannot cancel order:", orderRequest.getOrderID());
			}

		}


		//open buy.
		for (Quote quote:quoteRequest.bids
			 ) {
			try {
				orderRequest.setType(Order.OrderType.BID);
				orderRequest.setVolume(quote.quantity.toString());
				orderRequest.setPrice(quote.price.toString());
				this.OpenLimit(bitbox.getTradeService(), orderRequest);
			} catch (IOException e)
			{
				log.error(e.getMessage());
				log.error("Cannot place order");
			}

		}

		//open sell

		for (Quote quote:quoteRequest.asks
				) {
			try {
				orderRequest.setType(Order.OrderType.ASK);
				orderRequest.setVolume(quote.quantity.toString());
				orderRequest.setPrice(quote.price.toString());
				this.OpenLimit(bitbox.getTradeService(), orderRequest);
			} catch (IOException e)
			{
				log.error(e.getMessage());
				log.error("Cannot place order");
			}

		}


	}


}
