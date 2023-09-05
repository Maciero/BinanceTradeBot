package com.binance.connector.futures.bot;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;


public class MarkPriceKlines {


    public static void main(String[] args) {


        UMFuturesClientImpl client = new UMFuturesClientImpl();

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", "BTCUSDT");
        parameters.put("interval", "15m");

        try {
            String result = client.market().markPriceKlines(parameters);

            // Deserializacja JSON do listy tablic
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String[]>>() {
            }.getType();
            List<String[]> dataArray = gson.fromJson(result, listType);


            TechAnalysisMethods.calculateRSI(dataArray);
//                // Przetwarzanie danych
            for (String[] data : dataArray) {

                long timestamp = Long.parseLong(data[0]);
                String open = data[1];
                String high = data[2];
                String low = data[3];
                String close = data[4];
                long endTime = Long.parseLong(data[6]);


//                // Konwersja timestamp na czytelną datę
//                String formattedOpenDate = convertTimestampToHumanReadable(timestamp);
//                String formattedEndDate = convertTimestampToHumanReadable(endTime);
//
//
//                // Wyświetlanie tylko interesujących wartości
//                System.out.println("OpenTime: " + formattedOpenDate);
//                System.out.println("Open: " + open);
//                System.out.println("High: " + high);
//                System.out.println("Low: " + low);
//                System.out.println("Close: " + close);
//                System.out.println("EndTime: " + formattedEndDate);
//                System.out.println("--------------------------------");

                

            }
        } catch (BinanceConnectorException e) {
        } catch (BinanceClientException e) {
        }



    }
}