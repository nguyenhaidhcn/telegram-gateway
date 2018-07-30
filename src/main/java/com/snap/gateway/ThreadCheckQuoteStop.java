package com.snap.gateway;

import com.snap.gateway.message.QuoteRequest;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Date;
import java.util.Map;

public class ThreadCheckQuoteStop implements Runnable{

    @Override
    public void run() {
        while (true)
        {
            try{
                //check aglo stop
                Thread.sleep(60*1000);
                Date date = new Date();
                Map<String, QuoteRequest> stringQuoteRequestMap = ShareObjectQuote.getMap();
                boolean isstop = true;
                for (String key: stringQuoteRequestMap.keySet())
                {

                   if((stringQuoteRequestMap.get(key).id + 60*1000) > date.getTime())
                   {
                       isstop = false;
                       break;
                   }
                }

                //send notify aglo stop
                if(isstop)
                {
                    ShareObjectQuote.telegramBot.send("Quoting stop manually or RateManager error. Hedge stop also.");
                }


            }catch (Exception e)
            {

            }
        }
    }
}
