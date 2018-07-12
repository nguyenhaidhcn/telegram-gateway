package com.snap.gateway.handler.bitbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class PriceDigit {

    private final static PriceDigit instance = new PriceDigit();
    private static final Logger log = LoggerFactory.getLogger(PriceDigit.class);


    @Value("${FilePairInfo}")
    private String fileName;

    public static Map<String, Integer> Digits;

    public Map<String, Integer> getDigits() {
        return Digits;
    }

    private PriceDigit() {};


    public static PriceDigit getInstance() {
        return instance;
    }

    public void LoadCsv()
    {
        BufferedReader br = null;
        try
        {

            String path =System.getProperty("user.dir");
            path = path + "/PairDigits.csv";

            log.info("Load file:" + path);

            br = new BufferedReader(new FileReader(path));

            String line =  null;
            Digits = new HashMap<>();

            while((line=br.readLine())!=null){
                String str[] = line.split(",");
                Digits.put(str[0], Integer.valueOf(str[1]));

                log.info("Load list pair: "+str[0] + ":" + str[1]);
                }


        } catch (Exception e)
        {
                e.printStackTrace();
        }
    }

}
