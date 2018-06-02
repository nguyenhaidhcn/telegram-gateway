package com.snap.gateway.handler.kucoin;

import java.io.IOException;
import java.math.BigDecimal;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.kucoin.KucoinExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.kucoin.service.KucoinCancelOrderParams;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.snap.gateway.handler.GatewayHandler;
import com.snap.gateway.message.OrderRequest;

@Component
public class KucoinHandler implements GatewayHandler {
	private static final Logger log = LoggerFactory.getLogger(KucoinHandler.class);

	@Override
	public void trade(OrderRequest orderRequest) {
		ExchangeSpecification exSpec = new KucoinExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("34387");
		exSpec.setApiKey("5b120d483232925061499e66");
		exSpec.setSecretKey("7f6333b5-da43-4a08-ae77-7f580bb61981");
		Exchange kucoin = ExchangeFactory.INSTANCE.createExchange(exSpec);

		try {

			this.OpenLimit(kucoin.getTradeService(), orderRequest);

			this.CancelLimit(kucoin.getTradeService(), orderRequest);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public void OpenLimit(TradeService tradeService, OrderRequest orderRequest) throws IOException {

		ExchangeSpecification exSpec = new KucoinExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("34387");
		exSpec.setApiKey("5b120d483232925061499e66");
		exSpec.setSecretKey("7f6333b5-da43-4a08-ae77-7f580bb61981");
		Exchange kucoin = ExchangeFactory.INSTANCE.createExchange(exSpec);


		LimitOrder limitOrder =
				new LimitOrder.Builder(orderRequest.getType(), orderRequest.getPair())
						.limitPrice(new BigDecimal("0.001"))
						.originalAmount(new BigDecimal(orderRequest.getVolume()))
						.build();

		try {
			String uuid = tradeService.placeLimitOrder(limitOrder);
			System.out.println("Order successfully placed. ID=" + uuid);

			Thread.sleep(7000); // wait for order to propagate

			System.out.println();
			DefaultOpenOrdersParamCurrencyPair orderParams =
					(DefaultOpenOrdersParamCurrencyPair) tradeService.createOpenOrdersParams();
			orderParams.setCurrencyPair(orderRequest.getPair());
			System.out.println(tradeService.getOpenOrders(orderParams));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void CancelLimit(TradeService tradeService, OrderRequest orderRequest) throws IOException {

		ExchangeSpecification exSpec = new KucoinExchange().getDefaultExchangeSpecification();
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

			Thread.sleep(7000); // wait for cancellation to propagate

			System.out.println();
			DefaultOpenOrdersParamCurrencyPair orderParams =
					(DefaultOpenOrdersParamCurrencyPair) tradeService.createOpenOrdersParams();
			orderParams.setCurrencyPair(orderRequest.getPair());
			System.out.println(tradeService.getOpenOrders(orderParams));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 
	 * @throws IOException
	 */
	@Override
	public void getPublicData() throws IOException {
		log.info("Get public data from kucoin");
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(KucoinExchange.class.getName());
	    MarketDataService marketDataService = exchange.getMarketDataService();
	    
	    Ticker ticker = marketDataService.getTicker(new CurrencyPair("LTC", "BTC"));
	    System.out.println(ticker.toString());
	    
	    OrderBook books = marketDataService.getOrderBook(new CurrencyPair("LTC", "BTC"), 10);
	    System.out.println(books.toString());
	    
	}
}
