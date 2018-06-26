package com.snap.gateway.handler;

import com.snap.gateway.handler.binance.BinanceHandler;
import com.snap.gateway.handler.binance.BinanceHandlerCondition;
import com.snap.gateway.handler.bitbox.BitboxHandler;
import com.snap.gateway.handler.bitbox.BitboxHandlerCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

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
//	@Conditional(value=BitboxHandlerCondition.class)
//	public GatewayHandler getBiboxxHandler() {
//		return new BitboxHandler();
//	}
////
	@Bean(name="handler")
	@Conditional(value=KucoinHandlerCondition.class)
	public GatewayHandler getKuconHandler() {
		return new KucoinHandler();
	}

	@Bean(name="handler")
	@Conditional(value=BinanceHandlerCondition.class)
	public GatewayHandler getBinanceHandler() {
		return new BinanceHandler();
	}

	@Bean(name="handler")
	@Conditional(value=BitboxHandlerCondition.class)
	public GatewayHandler getBitboxHandler() {
		return new BitboxHandler();
	}



}
