package com.binance.connector.futures.bot;

import com.binance.connector.futures.PrivateConfig;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NewOrder {

    private static final double quantity = 1;
    private static final double price = 26500;
    private static final Logger logger = LoggerFactory.getLogger(NewOrder.class);

    private static final double percentage = 0.2; //20%
    private static final double calculatedValue = price * (1 - percentage);


    public static void checkForSignal(Signal signal) {
        if (signal == Signal.BUY) {
            placeBuyOrder();
            placeBuyOrderStopLoss();
            placeBuyOrderTakeProfit();

        } else if (signal == Signal.SELL) {
            placeSellOrder();
            placeSellOrderStopLoss();
            placeSellOrderTakeProfit();
        }
    }

    public static void placeBuyOrder() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        UMFuturesClientImpl client = new UMFuturesClientImpl(
                PrivateConfig.TESTNET_API_KEY,
                PrivateConfig.TESTNET_SECRET_KEY,
                PrivateConfig.TESTNET_BASE_URL
        );

        parameters.put("symbol", "BTCUSDT");

        parameters.put("positionSide", "LONG");
//        parameters.put("stopPrice", "STOP_MARKET");
//        parameters.put("stopPrice", "26000.00");
//        parameters.put("stopPrice", "TAKE_PROFIT_MARKET");
//        parameters.put("activatePrice", "60 000");

        parameters.put("side", "BUY");
        parameters.put("type", "MARKET");
//        parameters.put("timeInForce", "GTC");
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

        parameters.put("symbol", "BTCUSDT");
        parameters.put("side", "SELL");
        parameters.put("type", "STOP");// STOP_MARKET
        parameters.put("quantity", quantity);
        parameters.put("price", String.format("%d", (int) calculatedValue));
        parameters.put("stopPrice", "24000");
        parameters.put("timeInForce", "GTC");
        parameters.put("positionSide", "SHORT");
//        parameters.put("stopPrice","30000");


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

        parameters.put("symbol", "BTCUSDT");
        parameters.put("side", "SELL");
        parameters.put("type", "TAKE_PROFIT");// STOP_MARKET
        parameters.put("quantity", quantity);
        parameters.put("price", "35000");
        parameters.put("stopPrice", "35000");
        parameters.put("timeInForce", "GTC");
        parameters.put("positionSide", "SHORT");


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

        parameters.put("symbol", "BTCUSDT");

        parameters.put("positionSide", "SHORT");
//        parameters.put("stopPrice", "STOP_MARKET");
//        parameters.put("stopPrice", "TAKE_PROFIT_MARKET");
        //        parameters.put("activatePrice", "10 000");

        parameters.put("side", "SELL");
        parameters.put("type", "MARKET");
        parameters.put("timeInForce", "GTC");
//        parameters.put("quantity", quantity);
        parameters.put("price", price);

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

        parameters.put("symbol", "BTCUSDT");
        parameters.put("side", "BUY");
        parameters.put("type", "STOP");// STOP_MARKET
        parameters.put("quantity", quantity);
        parameters.put("price", String.format("%d", (int) calculatedValue));
        parameters.put("stopPrice", "27000");
        parameters.put("timeInForce", "GTC");
        parameters.put("positionSide", "LONG");
//        parameters.put("stopPrice","30000");


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

        parameters.put("symbol", "BTCUSDT");
        parameters.put("side", "BUY");
        parameters.put("type", "TAKE_PROFIT");// STOP_MARKET
        parameters.put("quantity", quantity);
        parameters.put("price", "20000");
        parameters.put("stopPrice", "20000");
        parameters.put("timeInForce", "GTC");
        parameters.put("positionSide", "LONG");


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
}