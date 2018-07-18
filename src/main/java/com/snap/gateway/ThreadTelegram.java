package com.snap.gateway;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class ThreadTelegram  implements Runnable{

    @Override
    public void run() {
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {

            ShareObjectQuote.telegramBot = new TelegramBot();
            botsApi.registerBot(ShareObjectQuote.telegramBot);

            Thread.sleep(7);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
