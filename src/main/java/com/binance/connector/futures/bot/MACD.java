package com.binance.connector.futures.bot;

class MACD {
    private final double[] macdLine;
    private final double[] signalLine;
    private final double[] histogram;

    public MACD(double[] macdLine, double[] signalLine, double[] histogram) {
        this.macdLine = macdLine;
        this.signalLine = signalLine;
        this.histogram = histogram;
    }

    public double[] getMacdLine() {
        return macdLine;
    }

    public double[] getSignalLine() {
        return signalLine;
    }

    public double[] getHistogram() {
        return histogram;
    }
    @Override
    public String toString() {
        return "MACD Line: " + formatArray(macdLine) + "\n" +
                "Signal Line: " + formatArray(signalLine) + "\n" +
                "Histogram: " + formatArray(histogram);
    }
    public double getLatestMacd() {
        if (macdLine != null && macdLine.length > 0) {
            return macdLine[macdLine.length - 1]; // Zwraca ostatnią wartość z MACD Line
        } else {
            return 0.0; // Zwraca domyślną wartość, jeśli tablica jest pusta
        }
    }

    public double getLatestSignal() {
        if (signalLine != null && signalLine.length > 0) {
            return signalLine[signalLine.length - 1]; // Zwraca ostatnią wartość z Signal Line
        } else {
            return 0.0; // Zwraca domyślną wartość, jeśli tablica jest pusta
        }
    }

    public double getLatestHistogram() {
        if (histogram != null && histogram.length > 0) {
            return histogram[histogram.length - 1]; // Zwraca ostatnią wartość z Histogram
        } else {
            return 0.0; // Zwraca domyślną wartość, jeśli tablica jest pusta
        }
    }

    private String formatArray(double[] array) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}