package com.binance.connector.futures.bot;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExcelWriterMap {
    public static void writeDataToExcel(Map<String, List<Double>> usedPosition, List<LocalDateTime> dateHolder) {
        String excelFilePath = "usedPosition.xlsx";

        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            Sheet sheet = workbook.createSheet("Used Position");

            int rowNum = 0;

            // Utwórz nagłówki
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Key");
            headerRow.createCell(2).setCellValue("Value");

            // Wstaw daty, klucze i wartości
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (LocalDateTime dateTime : dateHolder) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                // Wstaw datę
                Cell dateCell = row.createCell(colNum++);
                dateCell.setCellValue(dateTime.format(formatter));

                // Wstaw klucz i wartość
                for (Map.Entry<String, List<Double>> entry : usedPosition.entrySet()) {
                    String key = entry.getKey();
                    List<Double> values = entry.getValue();

                    row.createCell(colNum++).setCellValue(key);

                    if (!values.isEmpty()) {
                        Double value = values.remove(0); // Pobierz i usuń pierwszą wartość
                        row.createCell(colNum++).setCellValue(value);
                    } else {
                        colNum++; // Pominięcie kolumny wartości, jeśli brak danych
                    }
                }
            }

            workbook.write(outputStream);
            System.out.println("Plik Excel został utworzony pomyślnie.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}