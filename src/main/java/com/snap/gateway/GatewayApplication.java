package com.snap.gateway;

import com.snap.gateway.handler.bitbox.BitboxQuote;
import com.snap.gateway.handler.bitbox.PriceDigit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class GatewayApplication {
	private static final Logger log = LoggerFactory.getLogger(GatewayApplication.class);

	public static void main(String[] args) {
		//todo loop config pair

		PriceDigit.getInstance().LoadCsv();

		Map<String, Integer> mapPair = PriceDigit.getInstance().getDigits();
		for (String key :mapPair.keySet()
			 ) {

			{
				BitboxQuote bitboxQuote = new BitboxQuote(key, mapPair.get(key));
				new Thread(bitboxQuote).start();
			}

		}
		




		SpringApplication.run(GatewayApplication.class, args);
	}
}
