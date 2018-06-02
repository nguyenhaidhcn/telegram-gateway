package com.snap.gateway.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.snap.gateway.handler.bibox.BiboxHandler;
import com.snap.gateway.handler.bibox.BiboxHandlerCondition;

//import com.snap.gateway.handler.bittrex.BittrexHandler;
//import com.snap.gateway.handler.bittrex.BittrexHandlerCondition;

import com.snap.gateway.handler.kucoin.KucoinHandlerCondition;
import com.snap.gateway.handler.kucoin.KucoinHandler;



@Configuration
public class GatewayHandlerConfig {
	
//	@Bean(name="handler")
//	@Conditional(value=BittrexHandlerCondition.class)
//	public GatewayHandler getBittrexHandler() {
//		return new BittrexHandler();
//	}
	
//	@Bean(name="handler")
//	@Conditional(value=BiboxHandlerCondition.class)
//	public GatewayHandler getBiboxxHandler() {
//		return new BiboxHandler();
//	}
////
	@Bean(name="handler")
	@Conditional(value=KucoinHandlerCondition.class)
	public GatewayHandler getKuconHandler() {
		return new KucoinHandler();
	}

}
