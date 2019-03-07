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

import java.math.BigDecimal;
import java.util.*;

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

            if(message_text.equals("/balances"))
            {
                querryBalance(chat_id);
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



    //query balance
    private void querryBalance(long chat_id)
    {
        List<String> mailList = new ArrayList<>();
        mailList.add("celinee2017@gmail.com");
        mailList.add("wilson@almegafingroup.com");
        mailList.add("wilsonseah@hotmail.com");
        mailList.add("merrysilvana@gmail.com");
        mailList.add("binance_mail");

        BigDecimal total_btc = new BigDecimal(0);
        BigDecimal total_eth = new BigDecimal(0);
        for(String mail:mailList)
        {
            String msg = "";
            Object o =  ShareObjectQuote.balanceRepository.sumBalance(mail);
//            List<Object> list = Arrays.asList(o);
//            total_btc.add( balanceDB.getEstimate_btc());
//            total_eth.add(balanceDB.getEstimate_eth());
            msg = msg + mail+" estimate balance [btc,eth]:[" +o+"]";
//            msg = msg + mail+" estimate eth:" + balanceDB.getEstimate_eth()+"\n";
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(msg);

            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        String msg = "";
        Object o =  ShareObjectQuote.balanceRepository.totalBalance();
//            List<Object> list = Arrays.asList(o);
//            total_btc.add( balanceDB.getEstimate_btc());
//            total_eth.add(balanceDB.getEstimate_eth());
        msg = msg +" total [btc,eth]:[" +o+"]";
//            msg = msg + mail+" estimate eth:" + balanceDB.getEstimate_eth()+"\n";
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(msg);

        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

//        String msg = "";
//        msg = msg + "total btc:" + total_btc+"\n";
//        msg = msg + "total eth:" + total_eth+"\n";
//        SendMessage message = new SendMessage() // Create a message object object
//                .setChatId(chat_id)
//                .setText(msg);
//
//        try {
//            execute(message); // Sending our message object to user
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }


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

        if(msg.contains("Balance") || msg.contains("balance"))
        {
            id = ShareObjectQuote.chat_id_balance;
        }

        if(msg.contains("TOMO"))
        {
            if(msg.contains("HTTP status code was not OK"))
            {
                System.out.println(msg);
                return;
            }
            id = ShareObjectQuote.chat_id_tomo;
        }


        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(id)
                .setText(msg);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


        //in case ugrent

        if(msg.contains("Ugrent")
                || msg.contains("Account has insufficient balance")
                )
        {
            if(msg.contains("NOT ENOUGH Balance to Order"))
            {
                return;
            }
            if(id == ShareObjectQuote.chat_id_tomo)
            {return;}
            id = ShareObjectQuote.chat_id_ugrent;
            SendMessage message_ugrent = new SendMessage() // Create a message object object
                    .setChatId(id)
                    .setText(msg);
            try {
                execute(message_ugrent); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
