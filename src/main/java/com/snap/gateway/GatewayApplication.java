package com.snap.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Map;

@SpringBootApplication
public class GatewayApplication {



	private static final Logger log = LoggerFactory.getLogger(GatewayApplication.class);

	public static void main(String[] args) {



//		ShareObjectQuote.token =  token;
//		ShareObjectQuote.chat_id =  new Long(chat_id);
		ThreadTelegram threadTelegram = new ThreadTelegram();
		new Thread(threadTelegram).start();

		ThreadCheckQuoteStop threadCheckQuoteStop = new ThreadCheckQuoteStop();
		new Thread(threadCheckQuoteStop).start();



		SpringApplication.run(GatewayApplication.class, args);
	}
}
