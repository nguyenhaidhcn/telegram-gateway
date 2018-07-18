package com.snap.gateway;

import com.snap.gateway.message.QuoteRequest;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class ShareObjectQuote {

    private final static ShareObjectQuote instance = new ShareObjectQuote();


    private ShareObjectQuote() {};

    private static Map<String, QuoteRequest> quoteRequestMap = new HashMap<>();

    public static Map<String, QuoteRequest> getCopy()
    {
        return new HashMap<>(quoteRequestMap);
    }

    public static Map<String, QuoteRequest> getMap()
    {
        return quoteRequestMap;
    }

    public static ShareObjectQuote getInstance() {
        return instance;
    }

    public static TelegramBot telegramBot;

}
