package com.snap.gateway.handler.bitbox;

import java.io.IOException;

import com.snap.gateway.message.MsgRequest;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitbox.BitboxExchange;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snap.gateway.handler.GatewayHandler;
import com.snap.gateway.message.OrderRequest;
import org.springframework.beans.factory.annotation.Value;

public class BitboxHandler implements GatewayHandler {
	private static final Logger log = LoggerFactory.getLogger(BitboxHandler.class);

	@Override
	public void trade(OrderRequest order) {
		log.info("BITBOX Order Request");
	}

	@Override
	public void getPublicData() throws IOException {
		log.info("BIBOX getPublic Data");
	}

	@Value("${api.key}")
	private String ApiKey;

	@Value("${secret.key}")
	private String SecretKey;


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
}
