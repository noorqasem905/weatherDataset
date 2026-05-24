package com.mycompany.weatherproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Zaid Alqudsi 202120148
 * 
 * */


public class SequentialProcessor {

    public static List<File> readDataset(String folderPath) {
        List<File> fileList = new ArrayList<>();
        
        File folder = new File(folderPath);
        
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Error: Provided path is not a valid directory: " + folderPath);
            return fileList;
        }
        
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));
        
        if (files == null || files.length == 0) {
            System.err.println("Warning: No XLSX files found in directory: " + folderPath);
            return fileList;
        }
        
        for (File file : files) {
            fileList.add(file);
        }
        return fileList;
    }

    public static List<weatherData> readRowsFromExcelFile(File file) {
        List<weatherData> weatherList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;
            
            for (Row row : sheet) {
                if (isHeader) { 
                    isHeader = false; 
                    continue; 
                }
                
                if (row.getPhysicalNumberOfCells() >= 7) {
                    try {
                        String date = row.getCell(0).toString().trim();
                        String summary = row.getCell(1).toString().trim();
                        String precipType = row.getCell(2).toString().trim();
                        
                        double temp = Double.parseDouble(row.getCell(3).toString().trim());
                        double appTemp = Double.parseDouble(row.getCell(4).toString().trim());
                        double humidity = Double.parseDouble(row.getCell(5).toString().trim());
                        double windSpeed = Double.parseDouble(row.getCell(6).toString().trim());
                        
                        weatherList.add(new weatherData(date, summary, precipType, temp, appTemp, humidity, windSpeed));
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
        }
        return weatherList;
    }

    public static void runSimpleProcessing(List<File> fileList) {
        if (fileList == null || fileList.isEmpty()) {
            System.out.println("---> No records found.");
            return;
        }

        weatherData maxTempRecord = null;
        
        for (File file : fileList) {
            List<weatherData> weatherList = readRowsFromExcelFile(file);
            for (weatherData wd : weatherList) {
                if (maxTempRecord == null || wd.getTemperature() > maxTempRecord.getTemperature()) {
                    maxTempRecord = wd;
                }
            }
        }

        if (maxTempRecord != null) {
            System.out.println("---> Task: Finding the maximum Temperature value.");
            System.out.println("---> Result: Maximum Temperature found is " + maxTempRecord.getTemperature() + 
                               " C (Date: " + maxTempRecord.getFormattedDate() + ", Summary: " + maxTempRecord.getSummary() + ")");
        } else {
            System.out.println("---> No records found.");
        }
    }

    public static void runComplexProcessing(List<File> fileList) {
        if (fileList == null || fileList.isEmpty()) {
            System.out.println("---> No records found.");
            return;
        }

        int count = 0;
        System.out.println("---> Task: Calculating Weighted Score & Filtering (Score > 30.0).");
        System.out.println("---> Formula applied: 0.5 * Temperature + 0.3 * (Humidity * 100) - 0.2 * WindSpeed");

        for (File file : fileList) {
            List<weatherData> weatherList = readRowsFromExcelFile(file);
            for (weatherData wd : weatherList) {
                double weightedIndex = (0.5 * wd.getTemperature()) + (0.3 * wd.getHumidity() * 100) - (0.2 * wd.getWindSpeed());
                
                if (weightedIndex > 30.0) {
                    count++;
                }
            }
        }

        System.out.println("---> Result: Found " + count + " records matching the criteria.");
    }
}