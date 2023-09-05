package com.binance.connector.futures.bot;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;

import static com.binance.connector.futures.bot.TechAnalysisMethods.calculateMACD;
import static com.binance.connector.futures.bot.TechAnalysisMethods.calculateMovingAverage;


public class MarkPriceKlines {


    public static void main(String[] args) {


        UMFuturesClientImpl client = new UMFuturesClientImpl();

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", "BTCUSDT");
        parameters.put("interval", "15m");

        List<Double> closePrices = new ArrayList<>();

        try {
            String result = client.market().markPriceKlines(parameters);

            // Deserializacja JSON do listy tablic
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String[]>>() {
            }.getType();
            List<String[]> dataArray = gson.fromJson(result, listType);


            String formattedOpenDate="";
//                // Przetwarzanie danych
            for (String[] data : dataArray) {

                long timestamp = Long.parseLong(data[0]);
                String open = data[1];
                String high = data[2];
                String low = data[3];
                String close = data[4];
                long endTime = Long.parseLong(data[6]);


                double closeNumber = Double.parseDouble(close); // Cena zamknięcia
                closePrices.add(closeNumber);


                // Konwersja timestamp na czytelną datę
                formattedOpenDate = TechAnalysisMethods.convertTimestampToHumanReadable(timestamp);
                String formattedEndDate = TechAnalysisMethods.convertTimestampToHumanReadable(endTime);

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
            System.out.println("--------------------------------");
            System.out.println(formattedOpenDate);

            int shortTermPeriod = 12; // Okres dla EMA krótkiego
            int longTermPeriod = 26; // Okres dla EMA długiego
            int signalPeriod = 9; // Okres dla sygnału

            MACD macd = calculateMACD(closePrices, shortTermPeriod, longTermPeriod, signalPeriod);
            System.out.println(macd);

            double latestMacd = macd.getLatestMacd(); // Pobiera najnowszą wartość MACD
            double latestSignal = macd.getLatestSignal(); // Pobiera najnowszą wartość Signal Line
            double latestHistogram = macd.getLatestHistogram(); // Pobiera najnowszą wartość Histogram

            System.out.println("Latest Macd: " + latestMacd);
            System.out.println("Latest Signal: " + latestSignal);
            System.out.println("Latest Histogram: " + latestHistogram);

            double ma7 = calculateMovingAverage(closePrices, 7);
            double ma25 = calculateMovingAverage(closePrices, 25);
            double ma99 = calculateMovingAverage(closePrices, 99);

            System.out.println("MA(7): " + ma7);
            System.out.println("MA(25): " + ma25);
            System.out.println("MA(99): " + ma99);

            System.out.println("RSI(14): " + TechAnalysisMethods.calculateRSI(dataArray));
            System.out.println("--------------------------------");

            System.out.println(TechAnalysisMethods.generateTradingSignal(dataArray,closePrices));

        } catch (BinanceConnectorException e) {
        } catch (BinanceClientException e) {
        }


    }
}