package com.binance.connector.futures.bot;

import com.binance.connector.futures.PrivateConfig;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.CMWebsocketClientImpl;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public final class KlineStream {
    private static double previousVolume = -1; // Wolumen poprzedniego klinu
    private static List<Double> closePrices = new ArrayList<>();

        private static final int MAX_CLOSE_PRICES = 50; // Maksymalna liczba cen zamknięcia w liście

    private static final Logger logger = LoggerFactory.getLogger(NewOrder.class);
    private static final UMFuturesClientImpl client = new UMFuturesClientImpl(
            PrivateConfig.TESTNET_API_KEY,
            PrivateConfig.TESTNET_SECRET_KEY,
            PrivateConfig.TESTNET_BASE_URL
    );


    public static void main(String[] args) {
        CMWebsocketClientImpl cmClient = new CMWebsocketClientImpl();
        cmClient.klineStream("btcusd_perp", "15m", (event -> {
            JsonObject eventData = JsonParser.parseString(event).getAsJsonObject();
            JsonObject klineData = eventData.getAsJsonObject("k");

            long startTime = klineData.get("t").getAsLong();
            long endTime = klineData.get("T").getAsLong();

            String formattedStartTime = convertEpochToHumanReadable(startTime);
            String formattedEndTime = convertEpochToHumanReadable(endTime);

            String openPrice = klineData.get("o").getAsString();
            String closePrice = klineData.get("c").getAsString();
            String highPrice = klineData.get("h").getAsString();
            String lowPrice = klineData.get("l").getAsString();
            String volume = klineData.get("v").getAsString();

//            double currentVolume = Double.parseDouble(volume);
//            double volumeDifference = calculateVolumeDifference(currentVolume);

            double rsi = calculateRSI(Double.parseDouble(closePrice));

            System.out.println(closePrices.size());
            if (closePrices.size() == MAX_CLOSE_PRICES) {
                setNewOrderDependingOnRSI(rsi);
            }

//            System.out.println("Start Time: " + formattedStartTime);
//            System.out.println("End Time: " + formattedEndTime);
//            System.out.println("Open Price: " + openPrice);
//            System.out.println("Close Price: " + closePrice);
//            System.out.println("High Price: " + highPrice);
//            System.out.println("Low Price: " + lowPrice);
//            System.out.println("Volume: " + volume);
//            System.out.println("Volume Difference: " + volumeDifference);
//            System.out.println("RSI: " + rsi);
//            System.out.println("------------------------");

            // Logowanie zamiast System.out.println
            logger.info("Start Time: {}", formattedStartTime);
            logger.info("End Time: {}", formattedEndTime);
            // Pozostałe logi...


            // Ustawienie aktualnego wolumenu jako poprzedniego dla kolejnego klinu
//            previousVolume = currentVolume;
            // Dodanie aktualnej ceny zamknięcia do listy
            closePrices.add(Double.parseDouble(closePrice));
        }));

//        // Usunięcie zbędnych elementów z listy
//        if (closePrices.size() > MAX_CLOSE_PRICES) {
//            closePrices.remove(0);
//        }
    }

    private static String convertEpochToHumanReadable(long epochTime) {
        Date date = new Date(epochTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    private static double calculateVolumeDifference(double currentVolume) {
        if (previousVolume >= 0) {
            double volumeDifference = currentVolume - previousVolume;
            return volumeDifference;
        } else {
            return 0.0; // Pierwszy klin, brak poprzedniego wolumenu
        }
    }

    private static double calculateRSI(double currentClosePrice) {
//        closePrices.add(currentClosePrice);

        if (closePrices.size() > 50) {
            double sumGain = 0;
            double sumLoss = 0;

            for (int i = 1; i < closePrices.size(); i++) {
                double priceChange = closePrices.get(i) - closePrices.get(i - 1);
                if (priceChange >= 0) {
                    sumGain += priceChange;
                } else {
                    sumLoss += Math.abs(priceChange);

                }
            }

            double avgGain = sumGain / 50;
//            double avgGain = sumGain / 14;
            double avgLoss = sumLoss / 50;
//            double avgLoss = sumLoss / 14;

            double relativeStrength = avgGain / avgLoss;

            double rsi = 100 - (100 / (1 + relativeStrength));

            System.out.println("RSI: " + rsi);
            closePrices.remove(0);

            return rsi;


        } else {
            return -1.0; // Niewystarczająca ilość danych do obliczenia RSI
        }
    }

    private static void setNewOrderDependingOnRSI(double rsi) {
        if ( rsi<90 && rsi > 77) {
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

            UMFuturesClientImpl client = new UMFuturesClientImpl(PrivateConfig.TESTNET_API_KEY, PrivateConfig.TESTNET_SECRET_KEY, PrivateConfig.TESTNET_BASE_URL);

            parameters.put("symbol", "BTCUSDT");
            parameters.put("side", "SELL");
            parameters.put("type", "MARKET");
//        parameters.put("timeInForce", "GTC");
            parameters.put("quantity", 1);
//        parameters.put("price", 28000);

            try {
                String result = client.account().newOrder(parameters);
                logger.info(result);
                System.out.println("SELL ORDER PLACED!");
            } catch (BinanceConnectorException e) {
                logger.error("fullErrMessage: {}", e.getMessage(), e);
            } catch (BinanceClientException e) {
                logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                        e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
            }
        }
        if (rsi>15 && rsi < 25){
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

            UMFuturesClientImpl client = new UMFuturesClientImpl(PrivateConfig.TESTNET_API_KEY, PrivateConfig.TESTNET_SECRET_KEY, PrivateConfig.TESTNET_BASE_URL);

            parameters.put("symbol", "BTCUSDT");
            parameters.put("side", "BUY");
            parameters.put("type", "MARKET");
//        parameters.put("timeInForce", "GTC");
            parameters.put("quantity", 1);
//        parameters.put("price", 28000);

            try {
                String result = client.account().newOrder(parameters);
                logger.info(result);
                System.out.println("BUY ORDER PLACED!");
            } catch (BinanceConnectorException e) {
                logger.error("fullErrMessage: {}", e.getMessage(), e);
            } catch (BinanceClientException e) {
                logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                        e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
            }
        }
        }
    private static boolean shouldExecuteOrder(String symbol, double rsi) {
        if (symbol.equals("BTCUSDT") && ((rsi < 90 && rsi > 77) || (rsi > 15 && rsi < 25))) {
            return true;
        }
        return false;
    }
    }

