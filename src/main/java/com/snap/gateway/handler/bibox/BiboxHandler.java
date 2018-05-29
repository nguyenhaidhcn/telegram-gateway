package com.snap.gateway.handler.bibox;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snap.gateway.handler.GatewayHandler;
import com.snap.gateway.handler.bittrex.BittrexHandler;
import com.snap.gateway.message.OrderRequest;

public class BiboxHandler implements GatewayHandler {
	private static final Logger log = LoggerFactory.getLogger(BittrexHandler.class);

	@Override
	public void trade(OrderRequest order) {
		log.info("BIBOX Order Request");
	}

	@Override
	public void getPublicData() throws IOException {
		log.info("BIBOX getPublic Data");
	}
}
