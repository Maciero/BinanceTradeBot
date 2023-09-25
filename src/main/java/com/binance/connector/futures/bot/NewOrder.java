package com.binance.connector.futures.bot;

import com.binance.connector.futures.PrivateConfig;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewOrder {


    public NewOrder(double closeNumber) {
        price = closeNumber;
    }

    private static final double quantity = 1;
    private static double price;
    private static final Logger logger = LoggerFactory.getLogger(NewOrder.class);

    private static final double percentage = 0.1; //10%

    public static final Map<String, List<Double>> usedPosition = new LinkedHashMap<>();

    private static StringBuilder stringBuilder = new StringBuilder();
    private static List<Double> allPrices = new ArrayList<>();
    private static int counter = 0;

    public void checkForSignal(Signal signal) {
        if (signal == Signal.BUY) {
            placeBuyOrder();
            placeBuyOrderStopLoss();
            placeBuyOrderTakeProfit();


            //Wartości do mapy
            stringBuilder.append(signal).append(" ").append(counter);
            allPrices.add(price);
            counter++;
            System.out.println(stringBuilder);
            System.out.println(allPrices.get(0));

        } else if (signal == Signal.SELL) {
            placeSellOrder();
            placeSellOrderStopLoss();
            placeSellOrderTakeProfit();

            //Wartości do mapy
            stringBuilder.append(signal).append(" ").append(counter);
            allPrices.add(price);
            counter++;
        }
    }

    public void checkForSignalIfgetPositionListIsNotEmpty(Signal signal) {
        if (signal == Signal.BUY) {
            placeBuyOrderForShortPosition();

        } else if (signal == Signal.SELL) {
            placeSellOrderForLongPosition();
        }
    }

    public static void placeBuyOrder() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", "BUY");
        parameters.put("positionSide", "LONG");
        parameters.put("type", "MARKET");
//        parameters.put("type", "LIMIT");
        parameters.put("quantity", quantity);
//        parameters.put("price", price);

        try {
            String result = client.account().newOrder(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    public static void placeBuyOrderStopLoss() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

//        parameters.put("symbol", "BTCUSDT");
//        parameters.put("side", "SELL");
//        parameters.put("type", "STOP");// STOP_MARKET
//        parameters.put("quantity", quantity);
//        parameters.put("price", String.format("%d", (int) calculatedValue));
//        parameters.put("stopPrice", "24000");
//        parameters.put("timeInForce", "GTC");
//        parameters.put("positionSide", "SHORT");
////        parameters.put("stopPrice","String.format("%d", (int) calculatedValue)");

        double calculatedValue = price * (1 - percentage);
        int calculatedValue1 = (int) calculatedValue;
        System.out.println(calculatedValue1);

        System.out.println("CENA WEJSCIOWA " + price);
        System.out.println("WARTOSĆ " + calculatedValue);

        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", "SELL");
        parameters.put("positionSide", "LONG");
        parameters.put("type", "STOP_MARKET");
        parameters.put("stopPrice", calculatedValue1);
        parameters.put("closePosition", true);
        parameters.put("timeInForce", "GTE_GTC");
        parameters.put("workingType", "MARK_PRICE");
        parameters.put("priceProtect", true);


        try {
            String result = client.account().newOrder(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    public static void placeBuyOrderTakeProfit() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", "SELL");
        parameters.put("positionSide", "LONG");
        parameters.put("type", "TAKE_PROFIT_MARKET");
        parameters.put("stopPrice", 3000);
        parameters.put("closePosition", true);
        parameters.put("timeInForce", "GTE_GTC");
        parameters.put("workingType", "MARK_PRICE");
        parameters.put("priceProtect", true);

        try {
            String result = client.account().newOrder(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    public static void placeSellOrder() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", "SELL");
        parameters.put("positionSide", "SHORT");
        parameters.put("type", "MARKET");
        parameters.put("quantity", quantity);
//        parameters.put("type", "LIMIT");
//        parameters.put("price", price);

        try {
            String result = client.account().newOrder(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    public static void placeSellOrderStopLoss() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

//        parameters.put("symbol", "BTCUSDT");
//        parameters.put("side", "BUY");
//        parameters.put("type", "STOP");// STOP_MARKET
//        parameters.put("quantity", quantity);
//        parameters.put("price", String.format("%d", (int) calculatedValue));
//        parameters.put("stopPrice", "27000");
//        parameters.put("timeInForce", "GTC");
//        parameters.put("positionSide", "SHORT");
////        parameters.put("stopPrice","30000");


        double calculatedValue = price * (1 + percentage);
        int calculatedValue1 = (int) calculatedValue;

        System.out.println("CENA WEJSCIOWA " + price);
        System.out.println("WARTOSĆ " + calculatedValue);

        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", "BUY");
        parameters.put("positionSide", "SHORT");
        parameters.put("type", "STOP_MARKET");
        parameters.put("stopPrice", calculatedValue1);
        parameters.put("closePosition", true);
        parameters.put("timeInForce", "GTE_GTC");
        parameters.put("workingType", "MARK_PRICE");
        parameters.put("priceProtect", true);


        try {
            String result = client.account().newOrder(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    public static void placeSellOrderTakeProfit() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

//        parameters.put("symbol", "BTCUSDT");
//        parameters.put("side", "BUY");
//        parameters.put("type", "TAKE_PROFIT");// STOP_MARKET
//        parameters.put("quantity", quantity);
//        parameters.put("price", "20000");
//        parameters.put("stopPrice", "20000");
//        parameters.put("timeInForce", "GTC");
//        parameters.put("positionSide", "LONG");

        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", "BUY");
        parameters.put("positionSide", "SHORT");
        parameters.put("type", "TAKE_PROFIT_MARKET");
        parameters.put("stopPrice", 1000);
        parameters.put("closePosition", true);
        parameters.put("timeInForce", "GTE_GTC");
        parameters.put("workingType", "MARK_PRICE");
        parameters.put("priceProtect", true);


        try {
            String result = client.account().newOrder(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    public static void placeBuyOrderForShortPosition() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", "BUY");
        parameters.put("positionSide", "SHORT");
        parameters.put("type", "MARKET");
//        parameters.put("type", "LIMIT");
        parameters.put("quantity", quantity);
//        parameters.put("price", price);

        try {
            String result = client.account().newOrder(parameters);
            logger.info(result);

            allPrices.add(price);
            usedPosition.put(String.valueOf(stringBuilder),allPrices);

            stringBuilder.delete(0, stringBuilder.length());
            allPrices.clear();

            // Wyświetl wszystkie wartości w mapie
            for (Map.Entry<String, List<Double>> entry : usedPosition.entrySet()) {
                String key = entry.getKey();
                List<Double> values = entry.getValue();

                System.out.println("Klucz: " + key);
                System.out.println("Wartości:");

                for (Double value : values) {
                    System.out.println(value);
                }

                System.out.println("----");
            }


        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    public static void placeSellOrderForLongPosition() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", "SELL");
        parameters.put("positionSide", "LONG");
        parameters.put("type", "MARKET");
//        parameters.put("type", "LIMIT");
        parameters.put("quantity", quantity);
//        parameters.put("price", price);

        try {
            String result = client.account().newOrder(parameters);
            logger.info(result);

            allPrices.add(price);
            usedPosition.put(String.valueOf(stringBuilder),allPrices);

            stringBuilder.delete(0, stringBuilder.length());
            allPrices.clear();

            // Wyświetl wszystkie wartości w mapie
            for (Map.Entry<String, List<Double>> entry : usedPosition.entrySet()) {
                String key = entry.getKey();
                List<Double> values = entry.getValue();

                System.out.println("Klucz: " + key);
                System.out.println("Wartości:");

                for (Double value : values) {
                    System.out.println(value);
                }

                System.out.println("----");
            }


        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }
}