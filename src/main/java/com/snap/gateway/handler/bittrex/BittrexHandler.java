//package com.snap.gateway.handler.bittrex;
//
//import java.io.IOException;
//
//import org.knowm.xchange.Exchange;
//import org.knowm.xchange.ExchangeFactory;
//import org.knowm.xchange.ExchangeSpecification;
//import org.knowm.xchange.bittrex.BittrexExchange;
//import org.knowm.xchange.currency.CurrencyPair;
//import org.knowm.xchange.dto.account.AccountInfo;
//import org.knowm.xchange.dto.marketdata.OrderBook;
//import org.knowm.xchange.dto.marketdata.Ticker;
//import org.knowm.xchange.dto.trade.OpenOrders;
//import org.knowm.xchange.service.account.AccountService;
//import org.knowm.xchange.service.marketdata.MarketDataService;
//import org.knowm.xchange.service.trade.TradeService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import com.snap.gateway.handler.GatewayHandler;
//import com.snap.gateway.message.OrderRequest;
//
//@Component
//public class BittrexHandler implements GatewayHandler {
//	private static final Logger log = LoggerFactory.getLogger(BittrexHandler.class);
//
//	@Override
//	public void trade(OrderRequest orderRequest) {
//		ExchangeSpecification exSpec = new BittrexExchange().getDefaultExchangeSpecification();
//		exSpec.setUserName("34387");
//		exSpec.setApiKey("a4SDmpl9s6xWJS5fkKRT6yn41vXuY0AM");
//		exSpec.setSecretKey("sisJixU6Xd0d1yr6w02EHCb9UwYzTNuj");
//		Exchange bittrex = ExchangeFactory.INSTANCE.createExchange(exSpec);
//
//		// Get the account information
//		AccountService accountService = bittrex.getAccountService();
//		TradeService tradeService = bittrex.getTradeService();
//
//		AccountInfo accountInfo;
//		try {
//			accountInfo = accountService.getAccountInfo();
//			OpenOrders orders = tradeService.getOpenOrders();
//			orders.getAllOpenOrders().forEach( (order) -> log.info(order.toString()));
//			System.out.println(accountInfo.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 *
//	 * @throws IOException
//	 */
//	@Override
//	public void getPublicData() throws IOException {
//		log.info("Get public data from Bittrex");
//		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName());
//	    MarketDataService marketDataService = exchange.getMarketDataService();
//
//	    Ticker ticker = marketDataService.getTicker(new CurrencyPair("LTC", "BTC"));
//	    System.out.println(ticker.toString());
//
//	    OrderBook books = marketDataService.getOrderBook(new CurrencyPair("LTC", "BTC"), 10);
//	    System.out.println(books.toString());
//
//	}
//}
