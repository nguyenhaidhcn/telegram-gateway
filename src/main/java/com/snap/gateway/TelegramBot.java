package com.snap.gateway;

import com.snap.gateway.message.QuoteRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Date;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot  {


    @Override
    public void onUpdateReceived(Update update) {

        long chat_id = update.getMessage().getChatId();
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables

            String message_text = update.getMessage().getText();
            if(message_text.equals("/quotes"))
            {
                sendStopStart(chat_id);
            }

            if(message_text.equals("/errors"))
            {
                sendCurrentError(chat_id);
            }

        }
    }



    //query stop/start

    private void sendStopStart(long chat_id)
    {
        String stops = "Stop: ";

        String running = "Running: ";

        {
            Map<String, QuoteRequest> stringQuoteRequestMap= ShareObjectQuote.getCopy();

            for (Map.Entry<String, QuoteRequest> entry : stringQuoteRequestMap.entrySet()) {

                System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
                if(entry.getValue().asks.size() == 0 && entry.getValue().bids.size() == 0)
                {

                    stops += entry.getKey() + ", ";
                }
                {
                    running += entry.getKey() + ", ";
                }
            }


        }

        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(running);

        SendMessage messageStop = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(stops);

        try {
            execute(message); // Sending our message object to user
            execute(messageStop); // Sending our message object to user

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //query current error
    private void sendCurrentError(long chat_id)
    {

        String errors = "Current errors: \n";

        {

            Date date = new Date();
            long time = date.getTime();
            for (Map.Entry<String, Long> entry : ShareObjectQuote.notifyMsg.entrySet()) {

//                System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
                if(time - entry.getValue() <600000)
                {

                    errors += entry.getKey();
                    errors +="\n";
                }

            }


        }

        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(errors);

        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg)
    {

        long id = 0;
        //default group LINE monitoring
        id = ShareObjectQuote.chat_id;
        //group LINE order Failed
        if(msg.contains("Hedge"))
        {
            id = ShareObjectQuote.chat_id_hedge;
        }

        if(msg.contains("Balance"))
        {
            id = ShareObjectQuote.chat_id_balance;
        }


        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(id)
                .setText(msg);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @TelegramBot, it must return 'TelegramBot'
        return "sys_snap_bot";
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
//        return ShareObjectQuote.token;
        return "525968331:AAHjiranH89hLS60L02FMW7wOaWB0gVjiIw";

    }


}
