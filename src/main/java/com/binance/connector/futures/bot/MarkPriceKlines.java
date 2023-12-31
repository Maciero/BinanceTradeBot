package com.binance.connector.futures.bot;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.time.LocalDateTime;
import java.util.*;
import java.lang.reflect.Type;

import static com.binance.connector.futures.bot.TechAnalysisMethods.*;


public class MarkPriceKlines {
    public static void main(String[] args) {
        // Tworzymy nowy obiekt Timer
        Timer timer = new Timer();

        // Ustawiamy zadanie, które ma być wykonywane co 1 minute
        timer.scheduleAtFixedRate(new TradingSignalTask(), 0, 60 * 1000);
    }

    static class TradingSignalTask extends TimerTask {
        @Override
        public void run() {
            // Tutaj wywołujemy kod, który ma być wykonywany co 1 minut
            processTradingSignal();
        }
    }

    public static void processTradingSignal() {

        String symbol = "ETHUSDT";


        UMFuturesClientImpl client = new UMFuturesClientImpl();

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("interval", "15m");

        List<Double> closePrices = new ArrayList<>();
        List<Boolean> positionStates = new ArrayList<>();
        double closeNumber;

        try {
            String result = client.market().markPriceKlines(parameters);

            // Deserializacja JSON do listy tablic
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String[]>>() {
            }.getType();
            List<String[]> dataArray = gson.fromJson(result, listType);


            String formattedOpenDate = "";
//                // Przetwarzanie danych
            for (String[] data : dataArray) {

                long timestamp = Long.parseLong(data[0]);
                String open = data[1];
                String high = data[2];
                String low = data[3];
                String close = data[4];
                long endTime = Long.parseLong(data[6]);


                closeNumber = Double.parseDouble(close); // Cena zamknięcia
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
            System.out.println(symbol + " close price: " + closePrices.get(closePrices.size() - 1));

            int shortTermPeriod = 12; // Okres dla EMA krótkiego
            int longTermPeriod = 26; // Okres dla EMA długiego
            int signalPeriod = 9; // Okres dla sygnału

            MACD macd = calculateMACD(closePrices, shortTermPeriod, longTermPeriod, signalPeriod);
//            System.out.println(macd);

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

            int period = 14; // Okres dla oscylatora stochastycznego
            double stochasticOscillator = calculateStochasticOscillator(dataArray, period);
            System.out.println("Stochastic Oscillator: " + stochasticOscillator);

            System.out.println(TechAnalysisMethods.generateTradingSignal(dataArray, closePrices));

            NewOrder newOrder = new NewOrder(closePrices.get(closePrices.size() - 1));


            if (NewOrder.limitPositionOn) {
                if (!GetAdlQuantile.getPositionListIfEmpty()){
                    NewOrder.limitPositionOn=false;
                }
            }

            if ((GetAdlQuantile.getPositionListIfEmpty() && !NewOrder.limitPositionOn)) {
                newOrder.checkForSignal(TechAnalysisMethods.generateTradingSignal(dataArray, closePrices));
                System.out.println("Limit position: " +NewOrder.limitPositionOn);

            } else if (!GetAdlQuantile.getPositionListIfEmpty()) {
                newOrder.checkForSignalIfgetPositionListIsNotEmpty(TechAnalysisMethods.generateTradingSignal(dataArray, closePrices));
            }


            System.out.println("--------------------------------");


            positionStates.add(GetAdlQuantile.getPositionListIfEmpty());

//            List<Double> list = new ArrayList<>();
//            list.add(1455.5);
//            list.add(1414.5);
//            NewOrder.usedPosition.put("BUY 0",list);

//            LocalDateTime currentTime = LocalDateTime.now();
//
//            NewOrder.dateHolder.add(currentTime);
//            NewOrder.dateHolder.add(currentTime);

            // Zapis danych do pliku Excel
            ExcelWriter.writeDataToExcel(dataArray, positionStates);
            //Zapis mapy do Excel
            ExcelWriterMap.writeDataToExcel(NewOrder.usedPosition, NewOrder.dateHolder);

            // Tworzenie wykresu
            ChartCreator.createLineChart(dataArray);


        } catch (BinanceConnectorException e) {
        } catch (BinanceClientException e) {
        }


    }

}