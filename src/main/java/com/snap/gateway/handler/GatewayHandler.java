package com.snap.gateway.handler;

import java.io.IOException;

import com.snap.gateway.message.MsgRequest;
import com.snap.gateway.message.OrderRequest;
import com.snap.gateway.message.QuoteRequest;

public interface GatewayHandler {
	void trade(OrderRequest order);
	void getPublicData() throws IOException;
	void getPosition(MsgRequest request);
	void getHistory(MsgRequest request);
	void quoteProcess(QuoteRequest quoteRequest);
}
