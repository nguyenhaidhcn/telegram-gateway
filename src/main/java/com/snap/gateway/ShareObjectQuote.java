package com.snap.gateway;

import com.snap.gateway.message.QuoteRequest;
import org.knowm.xchange.bitbox.dto.bitbox.BalanceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
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

    public static BalanceRepository balanceRepository;


    public static Map<String, Long> notifyMsg = new HashMap<>();
//    public static String token;
//
//    public static long chat_id;

//    @Value("${bot.token}")
    public static String token;
//    @Value("${chat.id}")
    public static long chat_id =-295025948;

    public static long chat_id_balance =-237206539;

    public static long chat_id_hedge =-195924043;

    public static long chat_id_ugrent =-313626904;



}
