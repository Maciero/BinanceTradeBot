package com.binance.connector.futures.bot;

import com.binance.connector.futures.PrivateConfig;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

public class GetAdlQuantile {

    private static final Logger logger = LoggerFactory.getLogger(GetAdlQuantile.class);

    public static boolean getPositionListIfEmpty() {
        UMFuturesClientImpl client = new UMFuturesClientImpl(PrivateConfig.TESTNET_API_KEY, PrivateConfig.TESTNET_SECRET_KEY, PrivateConfig.TESTNET_BASE_URL);

        try {
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            String result = client.account().getAdlQuantile(parameters);

            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(result, JsonArray.class);

            // Sprawdź, czy JSON jest pusty
            if (jsonArray.isEmpty()) {
                return true;
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                String symbol = jsonObject.get("symbol").getAsString();
                JsonObject adlQuantile = jsonObject.getAsJsonObject("adlQuantile");

                // Sprawdź, czy klucze istnieją i czy nie są nullem
                if (adlQuantile.has("LONG") && !adlQuantile.get("LONG").isJsonNull()
                        && adlQuantile.has("SHORT") && !adlQuantile.get("SHORT").isJsonNull()
                        && adlQuantile.has("BOTH") && !adlQuantile.get("BOTH").isJsonNull()) {

                    int longValue = adlQuantile.get("LONG").getAsInt();
                    int shortValue = adlQuantile.get("SHORT").getAsInt();
                    int bothValue = adlQuantile.get("BOTH").getAsInt();

                    System.out.print("Symbol: " + symbol);
                    System.out.print(" LONG: " + longValue);
                    System.out.print(" SHORT: " + shortValue);
                    System.out.println(" BOTH: " + bothValue);
                } else {
                    System.out.println("Active open position: " + symbol);
                }
            }
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
        return false;
    }
}
