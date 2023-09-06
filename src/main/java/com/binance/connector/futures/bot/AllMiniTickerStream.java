package com.binance.connector.futures.bot;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;

//wszystkie symbole kryptowalut
public final class AllMiniTickerStream {
    private AllMiniTickerStream() {
    }

    public static void main(String[] args) {
        UMWebsocketClientImpl client = new UMWebsocketClientImpl();
        client.allMiniTickerStream(((event) -> {
            System.out.println(event);
            client.closeAllConnections();
        }));
    }
}
