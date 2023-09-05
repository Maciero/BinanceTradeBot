package com.binance.connector.futures.bot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TechAnalysisMethods {


    public static double calculateRSI(List<String[]> dataArray) {
        double roundedRsi = 0.0;

        // Przetwarzanie danych
        for (int i = 14; i < dataArray.size(); i++) {
            double sumGain = 0;
            double sumLoss = 0;

            for (int j = i - 14; j < i; j++) {
                double prevClose = Double.parseDouble(dataArray.get(j)[4]);
                double currentClose = Double.parseDouble(dataArray.get(j + 1)[4]);
                double priceChange = currentClose - prevClose;

                if (priceChange > 0) {
                    sumGain += priceChange;
                } else {
                    sumLoss -= priceChange;  // Ujemne wartości zmiany ceny dodajemy jako straty
                }
            }

            double avgGain = sumGain / 14;
            double avgLoss = sumLoss / 14;

            double relativeStrength = avgGain / avgLoss;
            double rsi = 100 - (100 / (1 + relativeStrength));

            // Zaokrąglamy RSI do dwóch miejsc po przecinku
            DecimalFormat df = new DecimalFormat("#.00");
            String roundedRSI = df.format(rsi);
            roundedRSI = roundedRSI.replace(",", ".");

            // Parsujemy zaokrąglone RSI z powrotem na double
            roundedRsi = Double.parseDouble(roundedRSI);

            long epochCloseTimestamp = Long.parseLong(dataArray.get(i)[0]);
            String humanReadableTimestamp = convertTimestampToHumanReadable(epochCloseTimestamp);

            //wyświetlanie obliczeń RSI
//            System.out.println(humanReadableTimestamp);
//            System.out.println("RSI: " + roundedRSI);

        }
        return roundedRsi;
    }

    public static double calculateMovingAverage(List<Double> prices, int period) {
        if (prices.size() < period) {
            throw new IllegalArgumentException("Not enough data points to calculate MA(" + period + ").");
        }
        double sum = 0;
        for (int i = prices.size() - 1; i >= prices.size() - period; i--) {
            sum += prices.get(i);
        }
        return sum / period;
    }

    public static MACD calculateMACD(List<Double> closePrices, int shortTermPeriod, int longTermPeriod, int signalPeriod) {
        int size = closePrices.size();

        if (size < longTermPeriod) {
            throw new IllegalArgumentException("Not enough data points to calculate MACD.");
        }

        double[] shortTermEMA = calculateEMA(closePrices, shortTermPeriod);
        double[] longTermEMA = calculateEMA(closePrices, longTermPeriod);

        // Oblicz różnicę między krótkoterminowym i długoterminowym EMA (MACD Line)
        double[] macdLine = new double[size - longTermPeriod];
        for (int i = 0; i < macdLine.length; i++) {
            macdLine[i] = shortTermEMA[i + longTermPeriod] - longTermEMA[i];
        }

        List<Double> macdLineList = new ArrayList<>(macdLine.length);

        for (int i = 0; i < macdLine.length; i++) {
            macdLineList.add(macdLine[i]);
        }

        // Oblicz sygnał MACD (EMA z MACD Line)
        double[] signalLine = calculateEMA(macdLineList, signalPeriod);

        // Oblicz histogram (różnica między MACD Line a Signal Line)
        double[] histogram = new double[macdLine.length];
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = macdLine[i] - signalLine[i];
        }

        return new MACD(macdLine, signalLine, histogram);
    }

    public static double[] calculateEMA(List<Double> prices, int period) {
        double[] ema = new double[prices.size()];

        double multiplier = 2.0 / (period + 1);
        ema[0] = prices.get(0);

        for (int i = 1; i < prices.size(); i++) {
            ema[i] = (prices.get(i) - ema[i - 1]) * multiplier + ema[i - 1];
        }

        return ema;
    }
    public static Signal generateTradingSignal(List<String[]> dataArray, List<Double> closePrices) {
        double rsi = calculateRSI(dataArray);
        MACD macd = calculateMACD(closePrices, 12, 26, 9); // Przykładowe parametry MACD
        double movingAverage = calculateMovingAverage(closePrices, 14); // Przykładowy okres średniej kroczącej

        // Kryteria dla RSI i MACD
        if (rsi > 70 && macd.getMacdLine()[macd.getMacdLine().length - 1] < macd.getSignalLine()[macd.getSignalLine().length - 1]) {
            return Signal.SELL; // Sygnał do sprzedaży
        } else if (rsi < 30 && macd.getMacdLine()[macd.getMacdLine().length - 1] > macd.getSignalLine()[macd.getSignalLine().length - 1]) {
            return Signal.BUY; // Sygnał do zakupu
        }

//        // Kryteria dla MACD
//        if (macd.getMacdLine()[macd.getMacdLine().length - 1] > macd.getSignalLine()[macd.getSignalLine().length - 1]) {
//            return Signal.BUY; // Sygnał do zakupu
//        } else if (macd.getMacdLine()[macd.getMacdLine().length - 1] < macd.getSignalLine()[macd.getSignalLine().length - 1]) {
//            return Signal.SELL; // Sygnał do sprzedaży
//        }

        // Jeśli nie spełniono żadnych warunków, zwracamy HOLD jako brak sygnału.
        return Signal.HOLD;
    }



    public static String convertTimestampToHumanReadable(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

}
