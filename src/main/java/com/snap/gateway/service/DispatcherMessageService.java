package com.snap.gateway.service;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
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
public class DispatcherMessageService {
	private static final Logger log = LoggerFactory.getLogger(DispatcherMessageService.class);
	private ObjectMapper MAPPER = new ObjectMapper();
	private Map<String, GatewayHandler> gatewayMapping = new HashMap<>();
	
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

			log.info("REQUEST received -- {}", request);
//			OrderRequest req = MAPPER.readValue(request, OrderRequest.class);
			
			// Determine handler for the current request.
//			GatewayHandler handler = getHandler(req);


			Gson gson = new Gson();
//			request = "{" +
//					"\"baseSymbol\":\"LTC\"," +
//					"\"counterSymbol\":\"BTC\"," +
//					"\"volume\":\"0.01\"," +
//					"\"type\":\"BID\"," +
//					"\"status\":\"NEW\"," +
//					"\"price\":\"0.001\"," +
//					"\"orderID\":\"sdlkjigio\""+
//					"}";
			OrderRequest order = gson.fromJson(request, OrderRequest.class);
			order.setPair(new CurrencyPair(order.getBaseSymbol(), order.getCounterSymbol()));
			//OrderRequest order = new OrderRequest("LTC", "BTC", 0.01, Order.OrderType.ASK, Order.OrderStatus.NEW,"");
			handler.trade(order);

//			handler.getPublicData();
			
			if (handler == null) {
//				noHandlerFound(req);
				return;
			}
			
			sender.send(request);
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
}
