package com.snap.gateway.handler.bitbox;

import com.snap.gateway.jms.Sender;
import com.snap.gateway.message.Quote;
import com.snap.gateway.message.QuoteRequest;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class ShareObjectQuote {

    private final static ShareObjectQuote instance = new ShareObjectQuote();

    public static Sender sender;

    public static String ApiKey;

    public static String SecretKey;

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

}
