package com.binance.connector.futures.bot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TechnicalAnalysisMethods {

    private static final int WINDOW_SIZE = 100;
    private static List<Double> closePrices = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(TechnicalAnalysisMethods.class);

    public static void TickerStream() {
        WebSocketSingleton.getClient().klineStream("btcusdt", "1h", (event) -> {
            JsonObject eventData = JsonParser.parseString(event).getAsJsonObject();
            JsonObject klineData = eventData.getAsJsonObject("k");

            double closePrice = klineData.get("c").getAsDouble();
            updateClosePrices(closePrice);

            double rsi = calculateRSI();

            if (rsi < 30 && rsi != -1) {
                NewOrder.placeBuyOrder();
            }

            System.out.println("RSI: " + rsi);
            System.out.println("------------------------");
        });
    }

    private static void updateClosePrices(double closePrice) {
        if (closePrices.size() >= WINDOW_SIZE) {
            closePrices.remove(0);
        }
        closePrices.add(closePrice);
    }

    private static double calculateRSI() {
        if (closePrices.size() >= WINDOW_SIZE) {
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

            double avgGain = sumGain / WINDOW_SIZE;
            double avgLoss = sumLoss / WINDOW_SIZE;

            double relativeStrength = avgGain / avgLoss;

            return 100 - (100 / (1 + relativeStrength));
        } else {
            return -1.0; // Niewystarczająca ilość danych do obliczenia RSI
        }
    }

    private static double calculateMovingAverage(int period) {
        if (closePrices.size() >= period) {
            double sum = 0;
            for (int i = closePrices.size() - period; i < closePrices.size(); i++) {
                sum += closePrices.get(i);
            }
            return sum / period;
        } else {
            return -1.0; // Niewystarczająca ilość danych do obliczenia MA
        }
    }

    private static double calculateEMA(int period) {
        if (closePrices.size() >= period) {
            double multiplier = 2.0 / (period + 1);
            double ema = closePrices.get(closePrices.size() - period);

            for (int i = closePrices.size() - period + 1; i < closePrices.size(); i++) {
                ema = (closePrices.get(i) - ema) * multiplier + ema;
            }
            return ema;
        } else {
            return -1.0; // Niewystarczająca ilość danych do obliczenia EMA
        }
    }

    private static double calculateMACD(int shortPeriod, int longPeriod, int signalPeriod) {
        double shortEMA = calculateEMA(shortPeriod);
        double longEMA = calculateEMA(longPeriod);

        if (shortEMA != -1.0 && longEMA != -1.0) {
            double macd = shortEMA - longEMA;
            double signalLine = calculateEMA(signalPeriod);
            return macd - signalLine;
        } else {
            return -1.0; // Niewystarczająca ilość danych do obliczenia MACD
        }
    }

    private static double calculateStochasticOscillator(int period) {
        if (closePrices.size() >= period) {
            double minLow = closePrices.get(closePrices.size() - period);
            double maxHigh = closePrices.get(closePrices.size() - period);

            for (int i = closePrices.size() - period + 1; i < closePrices.size(); i++) {
                minLow = Math.min(minLow, closePrices.get(i));
                maxHigh = Math.max(maxHigh, closePrices.get(i));
            }

            return ((closePrices.get(closePrices.size() - 1) - minLow) / (maxHigh - minLow)) * 100;
        } else {
            return -1.0; // Niewystarczająca ilość danych do obliczenia Stochastic Oscillator
        }
    }
}