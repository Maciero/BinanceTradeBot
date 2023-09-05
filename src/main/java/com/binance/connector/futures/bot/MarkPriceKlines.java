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


//                // Przetwarzanie danych
            for (String[] data : dataArray) {
                long timestamp = Long.parseLong(data[0]);

                String open = data[1];
                String high = data[2];
                String low = data[3];
                String close = data[4];
                long endTime = Long.parseLong(data[6]);


                // Konwersja timestamp na czytelną datę
                String formattedOpenDate = convertTimestampToHumanReadable(timestamp);
                String formattedEndDate = convertTimestampToHumanReadable(endTime);


                for (int i = 14; i < dataArray.size(); i++) {
                    double sumGain = 0;
                    double sumLoss = 0;

                    for (int j = i - 14; j < i; j++) {
                        double prevClose = Double.parseDouble(dataArray.get(j)[4]);
                        double currentClose = Double.parseDouble(dataArray.get(j + 1)[4]);
                        double priceChange = currentClose - prevClose;

                        if (priceChange > 0) {
                            sumGain += priceChange;
                        } else {
                            sumLoss -= priceChange;  // Ujemne wartości zmiany ceny dodajemy jako straty
                        }
                    }

                    double avgGain = sumGain / 14;
                    double avgLoss = sumLoss / 14;

                    double relativeStrength = avgGain / avgLoss;
                    double rsi = 100 - (100 / (1 + relativeStrength));

                    // Zaokrąglamy RSI do dwóch miejsc po przecinku
                    DecimalFormat df = new DecimalFormat("#.00");
                    String roundedRSI = df.format(rsi);

                    long epochCloseTimestamp = Long.parseLong(dataArray.get(i)[0]);
                    String humanReadableTimestamp = convertTimestampToHumanReadable(epochCloseTimestamp);

                    System.out.println("RSI at timestamp " + humanReadableTimestamp + ": " + roundedRSI);
                }


                // Wyświetlanie tylko interesujących wartości
                System.out.println("OpenTime: " + formattedOpenDate);
                System.out.println("Open: " + open);
                System.out.println("High: " + high);
                System.out.println("Low: " + low);
                System.out.println("Close: " + close);
                System.out.println("EndTime: " + formattedEndDate);
                System.out.println("--------------------------------");


            }
        } catch (BinanceConnectorException e) {
        } catch (BinanceClientException e) {
        }

    }

    private static String convertTimestampToHumanReadable(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}