package com.snap.gateway;

import com.snap.gateway.handler.bitbox.BitboxQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
	private static final Logger log = LoggerFactory.getLogger(GatewayApplication.class);

	public static void main(String[] args) {
		//todo loop config pair
		{
			BitboxQuote bitboxQuote = new BitboxQuote("ETHBTC");
			new Thread(bitboxQuote).start();
		}
		SpringApplication.run(GatewayApplication.class, args);
	}
}
