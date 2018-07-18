package com.snap.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


		ThreadTelegram threadTelegram = new ThreadTelegram();
		new Thread(threadTelegram).start();



		SpringApplication.run(GatewayApplication.class, args);
	}
}
