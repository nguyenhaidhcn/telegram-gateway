package com.snap.gateway.jms;

import com.google.gson.Gson;
import com.snap.gateway.handler.bitbox.ShareObjectQuote;
import com.snap.gateway.message.QuoteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.snap.gateway.service.DispatcherMessageService;

import java.util.Date;
import java.util.Map;

@Component
public class Receiver {
	private static final Logger log = LoggerFactory.getLogger(Sender.class);
	
	@Autowired
	private DispatcherMessageService dispatcher;

	@Autowired
	Sender sender;
	
	@JmsListener(destination = "${OrderRequest.Topic}", containerFactory = "connectionFactory")
    public void receiveQueue(String request) {
		dispatcher.processRequest(request);
    }
	
	@JmsListener(destination = "${Quote.Topic}", containerFactory = "connectionFactory")
    public void receiveTopic(String request) {
		//log.info(request);
		//dispatcher.processRequest(request);

//		Runnable playerOne = new DispatcherMessageService();
//		((DispatcherMessageService) playerOne).msg = request;
//		new Thread(playerOne).start();
//		dispatcher.processQuote(request);

		log.info("received -- {}", request);
		Gson gson = new Gson();

		QuoteRequest msgRequest = gson.fromJson(request, QuoteRequest.class);
		//init base.counter symbol
		String symbol = msgRequest.symbol;
		symbol = symbol.toUpperCase();

		boolean hasUSDT = false;
		hasUSDT = symbol.contains("USDT");

		if (symbol.length() < 4)
        {
            log.error("Bad symbol" + symbol);
            return;
        }

		if(hasUSDT)
        {
            msgRequest.baseSymbol = symbol.substring(0,symbol.length() -4);
		    msgRequest.counterSymbol = symbol.substring(symbol.length() -4,symbol.length());
        }
        else
        {
            msgRequest.baseSymbol = symbol.substring(0,symbol.length() -3);
            msgRequest.counterSymbol = symbol.substring(symbol.length() -3,symbol.length());
        }

//		msgRequest.baseSymbol = symbol.substring(0,3);
//		msgRequest.counterSymbol = symbol.substring(3,symbol.length());


//
//		if (msgRequest.counterSymbol.compareTo("USDT") == 0)
//		{
//			msgRequest.digit = 2;
//		}
//		else if(msgRequest.baseSymbol.compareTo("XRP") == 0){
//			msgRequest.digit = 8;
//		}
//		else {
//			msgRequest.digit = 6;
//		}

		msgRequest.id = new Date().getTime();
		Map<String, QuoteRequest>  stringQuoteRequestMap = ShareObjectQuote.getMap();
		ShareObjectQuote.sender = sender;
		stringQuoteRequestMap.put(msgRequest.symbol, msgRequest);

    }
}
