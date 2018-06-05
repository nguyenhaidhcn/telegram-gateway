package com.snap.gateway.handler.kucoin;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.snap.gateway.common.Gateways;

public class KucoinHandlerCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String gatewayName = context.getEnvironment().getProperty("gateway.name");
		return Gateways.kucoin.value().equalsIgnoreCase(gatewayName);
	}

}