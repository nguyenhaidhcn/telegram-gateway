package com.snap.gateway.service;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.snap.gateway.handler.bitbox.BitboxHandler;
import com.snap.gateway.message.MsgRequest;
import com.snap.gateway.message.QuoteRequest;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snap.gateway.handler.GatewayHandler;
import com.snap.gateway.jms.Sender;
import com.snap.gateway.message.OrderRequest;

@Service
public class DispatcherMessageService implements Runnable{
	private static final Logger log = LoggerFactory.getLogger(DispatcherMessageService.class);
	private ObjectMapper MAPPER = new ObjectMapper();
	private Map<String, GatewayHandler> gatewayMapping = new HashMap<>();

	public String msg;

	@Autowired
	private Sender sender;
	
	@Autowired
	private GatewayHandler handler;
	
	/**
	 * Process Request to dispatcher to correct gateway
	 * @param request
	 */
	public void processRequest(String request) {
		try {

			log.info("OrderRequest received -- {}", request);
			Gson gson = new Gson();
			MsgRequest msgRequest = gson.fromJson(request, MsgRequest.class);
			msgRequest.getOrderRequest().setPair(new CurrencyPair(msgRequest.getOrderRequest().getBaseSymbol(), msgRequest.getOrderRequest().getCounterSymbol()));
			//OrderRequest order = new OrderRequest("LTC", "BTC", 0.01, Order.OrderType.ASK, Order.OrderStatus.NEW,"");
			if (msgRequest.getMsgType() == 2) {
				handler.getPosition(msgRequest);
			}
			else if (msgRequest.getMsgType() == 1)
			{
				handler.trade(msgRequest.getOrderRequest());

			}
			else if (msgRequest.getMsgType() == 3)
			{
				handler.getHistory(msgRequest);
			}
//			handler.getPublicData();
			
			if (handler == null) {
//				noHandlerFound(req);
				return;
			}

			msgRequest.setResultCode(msgRequest.getOrderRequest().getResultCode());
			msgRequest.setErrorMsg(msgRequest.getOrderRequest().getErrorMsg());

			sender.send(gson.toJson(msgRequest));
		} catch (Exception ex) {
			log.info("Invalid request format {}", request);
			log.info("Exception Info: ", ex);
			
		}
	}


	public void processQuote(String request) {
		try {

			log.info("processQuote received -- {}", request);
			Gson gson = new Gson();

			QuoteRequest msgRequest = gson.fromJson(request, QuoteRequest.class);
			//init base.counter symbol
			String symbol = msgRequest.symbol;
			symbol = symbol.toUpperCase();
			msgRequest.baseSymbol = symbol.substring(0,3);
			msgRequest.counterSymbol = symbol.substring(3,symbol.length());
			if (msgRequest.counterSymbol.compareTo("USDT") == 0)
			{
				msgRequest.digit = 2;
			}
			else {
				msgRequest.digit = 6;
			}
			//get open position

			//cancel order

			//place new order
			handler = new BitboxHandler();
			handler.quoteProcess(msgRequest);


		} catch (Exception ex) {
			log.info("Invalid request format {}", request);
			log.info("Exception Info: ", ex);

		}
	}



	private void initGatewayMappings () {
		
	}
	
	private GatewayHandler getHandler(OrderRequest req) {
		return null;  
	}
	
	private void noHandlerFound(OrderRequest req) {
		//StringFor
	}

	@Override
	public void run() {

		this.processQuote(msg);
	}
}
