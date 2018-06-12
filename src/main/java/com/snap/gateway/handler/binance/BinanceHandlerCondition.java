package com.snap.gateway.handler.binance;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.snap.gateway.common.Gateways;

public class BinanceHandlerCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String gatewayName = context.getEnvironment().getProperty("gateway.name");
		return Gateways.binance.value().equalsIgnoreCase(gatewayName);
	}

}
