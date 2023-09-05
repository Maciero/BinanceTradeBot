package com.binance.connector.futures.bot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

            System.out.println(humanReadableTimestamp);
            System.out.println("RSI: " + roundedRSI);

        }
        return roundedRsi;
    }

    private static String convertTimestampToHumanReadable(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
