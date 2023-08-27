package com.binance.connector.futures.bot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MiniTickerStream {

    public static void TickerStream() {
        WebSocketSingleton.getClient().klineStream("btcusdt", "1h", (event) -> {
            JsonObject eventData = JsonParser.parseString(event).getAsJsonObject();
            JsonObject klineData = eventData.getAsJsonObject("k");

            long startTime = klineData.get("t").getAsLong();
            long endTime = klineData.get("T").getAsLong();
            String symbol = klineData.get("s").getAsString();
            String interval = klineData.get("i").getAsString();
            String openPrice = klineData.get("o").getAsString();
            String closePrice = klineData.get("c").getAsString();
            String highPrice = klineData.get("h").getAsString();
            String lowPrice = klineData.get("l").getAsString();
            String volume = klineData.get("v").getAsString();
            int numberOfTrades = klineData.get("n").getAsInt();
            boolean isClosed = klineData.get("x").getAsBoolean();
            String quoteAssetVolume = klineData.get("q").getAsString();
            String takerBuyBaseAssetVolume = klineData.get("V").getAsString();
            String takerBuyQuoteAssetVolume = klineData.get("Q").getAsString();
            String ignore = klineData.get("B").getAsString(); // Ignoruj to pole

            String formattedStartTime = convertEpochToHumanReadable(startTime);
            String formattedEndTime = convertEpochToHumanReadable(endTime);

            System.out.println("Event Type: kline");
            System.out.println("Start Time: " + formattedStartTime);
            System.out.println("End Time: " + formattedEndTime);
            System.out.println("Symbol: " + symbol);
            System.out.println("Interval: " + interval);
            System.out.println("Open Price: " + openPrice);
            System.out.println("Close Price: " + closePrice);
            System.out.println("High Price: " + highPrice);
            System.out.println("Low Price: " + lowPrice);
            System.out.println("Volume: " + volume);
            System.out.println("Number of Trades: " + numberOfTrades);
            System.out.println("Is Closed: " + isClosed);
            System.out.println("Quote Asset Volume: " + quoteAssetVolume);
            System.out.println("Taker Buy Base Asset Volume: " + takerBuyBaseAssetVolume);
            System.out.println("Taker Buy Quote Asset Volume: " + takerBuyQuoteAssetVolume);
            System.out.println("------------------------");
        });
    }

    public static void main(String[] args) {
        TickerStream();
    }

    private static String convertEpochToHumanReadable(long epochTime) {
        Date date = new Date(epochTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
