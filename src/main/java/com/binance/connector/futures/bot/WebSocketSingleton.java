package com.binance.connector.futures.bot;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;

public class WebSocketSingleton {
    private static UMWebsocketClientImpl client;

    public static UMWebsocketClientImpl getClient() {
        if (client == null) {
            client = new UMWebsocketClientImpl();
        }
        return client;
    }
    public static UMWebsocketClientImpl getClientWithKey() {
        if (client == null) {
            client = new UMWebsocketClientImpl();
        }
        return client;
    }
}
