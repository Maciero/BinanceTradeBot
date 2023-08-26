package com.binance.connector.futures.bot;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;

public class MiniTickerStream {
    public static void main(String[] args) {
        UMWebsocketClientImpl client = new UMWebsocketClientImpl();
        client.miniTickerStream("btcusdt", ((event) -> {
            System.out.println(event);

        }));
    }
}
