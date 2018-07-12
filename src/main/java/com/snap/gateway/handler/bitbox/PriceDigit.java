package com.snap.gateway.handler.bitbox;

import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class PriceDigit {

    private final static PriceDigit instance = new PriceDigit();

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
            br = new BufferedReader(new FileReader(path));

            String line =  null;
            Digits = new HashMap<>();

            while((line=br.readLine())!=null){
                String str[] = line.split(",");
                Digits.put(str[0], Integer.valueOf(str[1]));
                }
            System.out.println(Digits);

        } catch (Exception e)
        {
                e.printStackTrace();
        }
    }

}
