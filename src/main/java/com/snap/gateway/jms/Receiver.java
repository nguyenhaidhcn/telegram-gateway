package com.snap.gateway.jms;

import com.google.gson.Gson;
import com.snap.gateway.BalanceRepository;
import com.snap.gateway.ShareObjectQuote;
import com.snap.gateway.message.QuoteRequest;
import org.knowm.xchange.bitbox.dto.bitbox.BalanceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class Receiver {
	private static final Logger log = LoggerFactory.getLogger(Receiver.class);

	@Autowired
	BalanceRepository balanceRepository;

    @JmsListener(destination = "${OrderResponse.Topic}", containerFactory = "connectionFactory")
    public void receiveOrders(String request) {

    	if(request.contains("The signature is not valid"))
    		return;

    	Date date = new Date();
    	long time = date.getTime();
    	Long lasttime = ShareObjectQuote.notifyMsg.get(request);
    	if(lasttime == null)
		{
			ShareObjectQuote.notifyMsg.put(request, time);
			log.info("OrderResponse.Topic:"+ request );
			ShareObjectQuote.telegramBot.send(request);
		}
		else
		{
			if((time - lasttime) >  600000)
			{
				//send
				log.info("OrderResponse.Topic:"+ request );
				ShareObjectQuote.telegramBot.send(request);
				ShareObjectQuote.notifyMsg.put(request, time);
			}
			else
			{
				log.info("Ignore:"+ request );
			}
		}

    }

	@JmsListener(destination = "${Telegram.Queue}", containerFactory = "connectionFactory")
    public void receiveQueueTele(String request) {
		log.info("Telegram.Queue:"+ request );
		ShareObjectQuote.telegramBot.send(request);
    }


	@JmsListener(destination = "${Quote.Topic}", containerFactory = "connectionFactory")
	public void receiveTopic(String request) {


    	if(ShareObjectQuote.balanceRepository == null)
		{
			ShareObjectQuote.balanceRepository = balanceRepository;
		}

		Map<String, QuoteRequest>  stringQuoteRequestMap = ShareObjectQuote.getMap();
//		ShareObjectQuote.telegramBot.send(request);
//		log.info("received -- {}", request);
		Gson gson = new Gson();

		QuoteRequest msgRequest = gson.fromJson(request, QuoteRequest.class);

		//check stop/start
		{
			if(msgRequest.asks.size() == 0 && msgRequest.asks.size() == 0)
			{
				ShareObjectQuote.telegramBot.send("Quote Stop:"+msgRequest.symbol);
			}
			else {
				QuoteRequest quoteLast = stringQuoteRequestMap.get(msgRequest.symbol);
				if (quoteLast != null && quoteLast.asks.size() == 0 && quoteLast.bids.size() ==0) {
					ShareObjectQuote.telegramBot.send("Quote Start:"+msgRequest.symbol);
				}
			}

		}

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

		msgRequest.id = new Date().getTime();

		stringQuoteRequestMap.put(msgRequest.symbol, msgRequest);
	}

}
