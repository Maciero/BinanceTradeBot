package com.binance.connector.futures.bot;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExcelWriter {
    public static void writeDataToExcel(List<String[]> dataArray, List<Boolean> positionStates) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");
            CreationHelper creationHelper = workbook.getCreationHelper();
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

            // Nagłówki kolumn
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Open Time");
            headerRow.createCell(1).setCellValue("Open");
            headerRow.createCell(2).setCellValue("High");
            headerRow.createCell(3).setCellValue("Low");
            headerRow.createCell(4).setCellValue("Close");
            headerRow.createCell(5).setCellValue("End Time");
            headerRow.createCell(6).setCellValue("Position Empty");

            // Ustawienie stylu dla nagłówków
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                headerRow.getCell(i).setCellStyle(dateCellStyle);
            }

            // Odwrócenie listy dataArray i positionStates, aby zacząć od najnowszej daty
            Collections.reverse(dataArray);
            Collections.reverse(positionStates);

            // Wstawianie danych
            for (int rowIndex = 0; rowIndex < dataArray.size(); rowIndex++) {
                String[] data = dataArray.get(rowIndex);
                Row row = sheet.createRow(rowIndex + 1);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // Konwertuj timestampy na czytelną datę
                long timestamp = Long.parseLong(data[0]);
                String openTime = dateFormat.format(new Date(timestamp));
                row.createCell(0).setCellValue(openTime);

                for (int cellIndex = 1; cellIndex < 6; cellIndex++) {
                    if (data.length > cellIndex) {
                        row.createCell(cellIndex).setCellValue(data[cellIndex]);
                    } else {
                        row.createCell(cellIndex).setCellValue("");
                    }
                }

                // Ustawienie formatu dla daty
                row.getCell(0).setCellStyle(dateCellStyle);
                row.getCell(5).setCellStyle(dateCellStyle);

                // Ustawienie wartości w kolumnie "Position Empty" na podstawie listy positionStates
                if (rowIndex < positionStates.size()) {
                    boolean positionState = positionStates.get(rowIndex);
                    row.createCell(6).setCellValue(positionState ? "true" : "false");
                } else {
                    row.createCell(6).setCellValue("");
                }
            }

            // Zapis arkusza do pliku Excel
            try (FileOutputStream fileOut = new FileOutputStream("dane.xlsx")) {
                workbook.write(fileOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}