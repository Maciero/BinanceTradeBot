package com.binance.connector.futures.bot;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.TimeS;

import java.text.ParseException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

            List<Bar> bars = new ArrayList<>();
            // Przetwarzanie danych
            for (String[] data : dataArray) {
                long timestamp = Long.parseLong(data[0]);
                double open = Double.parseDouble(data[1]);
                double high = Double.parseDouble(data[2]);
                double low = Double.parseDouble(data[3]);
                double close = Double.parseDouble(data[4]);
                long endTime = Long.parseLong(data[6]);


                // Konwersja timestamp na czytelną datę
                String formattedOpenDate = String.valueOf(convertTimestampToZonedDateTime(timestamp));
                String formattedEndDate = String.valueOf(convertTimestampToZonedDateTime(endTime));

                TimeSeries timeSeries = new BaseTimeSeries();
                BaseBar bar = new BaseBar(
                        Duration.ofMinutes(15), // Przykład: interwał 15 minut
                        ZonedDateTime.parse(formattedOpenDate),
                        open,
                        high,
                        low,
                        close,
                        0.0, // Wolumen (dostosuj)
                        0.0  // Wartość średniej (dostosuj)
                );


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

    private static ZonedDateTime convertTimestampToZonedDateTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        try {
            Date utcDate = sdf.parse(sdf.format(date));
            return ZonedDateTime.ofInstant(utcDate.toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
            return ZonedDateTime.now();
        }
    }
}