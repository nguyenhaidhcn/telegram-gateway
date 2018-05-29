package com.snap.gateway.handler;

import java.io.IOException;

import com.snap.gateway.message.OrderRequest;

public interface GatewayHandler {
	void trade(OrderRequest order);
	void getPublicData() throws IOException;
}
