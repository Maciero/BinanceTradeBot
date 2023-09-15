package com.binance.connector.futures.bot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChartCreator {
    public static void createLineChart(List<String[]> dataArray) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Dodawanie danych do wykresu
        for (String[] data : dataArray) {
            String openTime = data[0];
            double close = Double.parseDouble(data[4]);
            dataset.addValue(close, "Close", openTime);
        }

        // Tworzenie wykresu
        JFreeChart chart = ChartFactory.createLineChart(
                "Wykres cen zamknięcia", // Tytuł wykresu
                "Czas",                // Etykieta osi X
                "Cena zamknięcia",     // Etykieta osi Y
                dataset,               // Dane
                PlotOrientation.VERTICAL,
                true,                  // Inkludowanie legendy
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        // Odczytujemy kolory z pliku Excel i dostosowujemy kolor wykresu
        for (int i = 0; i < dataArray.size(); i++) {
            String chartColor = dataArray.get(i)[7]; // Kolor z kolumny "Chart Color"
            Color lineColor = chartColor.equalsIgnoreCase("red") ? Color.RED : Color.BLUE;
            renderer.setSeriesPaint(i, lineColor);
        }

        // Zakres cenowy na osi Y
        double minClose = findMinClose(dataArray);
        double maxClose = findMaxClose(dataArray);
        plot.getRangeAxis().setRange(minClose, maxClose);

        // Zapisywanie wykresu do pliku
        try {
            ChartUtils.saveChartAsPNG(new File("wykres.png"), chart, 800, 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double findMinClose(List<String[]> dataArray) {
        double minClose = Double.MAX_VALUE;
        for (String[] data : dataArray) {
            double close = Double.parseDouble(data[4]);
            if (close < minClose) {
                minClose = close;
            }
        }
        return minClose - 100;
    }

    private static double findMaxClose(List<String[]> dataArray) {
        double maxClose = Double.MIN_VALUE;
        for (String[] data : dataArray) {
            double close = Double.parseDouble(data[4]);
            if (close > maxClose) {
                maxClose = close;
            }
        }
        return maxClose + 100;
    }
}