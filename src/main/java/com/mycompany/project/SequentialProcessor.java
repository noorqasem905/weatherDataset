package com.mycompany.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SequentialProcessor {

    public static List<weatherData> readDataset(String filePath) {
        List<weatherData> weatherList = new ArrayList<>();
        String line;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); 
            
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (columns.length >= 7) {
                    try {
                        String date = columns[0].trim();
                        String summary = columns[1].trim();
                        String precipType = columns[2].trim();
                        
                        double temp = Double.parseDouble(columns[3].trim());
                        double appTemp = Double.parseDouble(columns[4].trim());
                        double humidity = Double.parseDouble(columns[5].trim());
                        double windSpeed = Double.parseDouble(columns[6].trim());
                        
                        weatherList.add(new weatherData(date, summary, precipType, temp, appTemp, humidity, windSpeed));
                    } catch (NumberFormatException nfe) {
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the dataset file: " + e.getMessage());
        }
        return weatherList;
    }

    public static void runSimpleProcessing(List<weatherData> weatherList) {
        if (weatherList == null || weatherList.isEmpty()) {
            System.out.println("---> No records found.");
            return;
        }

        weatherData maxTempRecord = weatherList.get(0);
        
        for (weatherData wd : weatherList) {
            if (wd.getTemperature() > maxTempRecord.getTemperature()) {
                maxTempRecord = wd;
            }
        }

        System.out.println("---> Task: Finding the maximum Temperature value.");
        System.out.println("---> Result: Maximum Temperature found is " + maxTempRecord.getTemperature() + 
                           " C (Date: " + maxTempRecord.getFormattedDate() + ", Summary: " + maxTempRecord.getSummary() + ")");
    }

    public static void runComplexProcessing(List<weatherData> weatherList) {
        if (weatherList == null || weatherList.isEmpty()) {
            System.out.println("---> No records found.");
            return;
        }

        int count = 0;
        System.out.println("---> Task: Calculating Weighted Score & Filtering (Score > 30.0).");
        System.out.println("---> Formula applied: 0.5 * Temperature + 0.3 * (Humidity * 100) - 0.2 * WindSpeed");

        for (weatherData wd : weatherList) {
            double weightedIndex = (0.5 * wd.getTemperature()) + (0.3 * wd.getHumidity() * 100) - (0.2 * wd.getWindSpeed());
            
            if (weightedIndex > 30.0) {
                count++;
            }
        }

        System.out.println("---> Result: Found " + count + " records matching the criteria.");
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\USER\\Desktop\\weatherHistory.csv"; 

        System.out.println("==================================================");
        System.out.println("        SEQUENTIAL PROCESSING OUTPUT");
        System.out.println("==================================================");

        System.out.println("\n[+] Step 1: Reading Dataset...");
        long startRead = System.nanoTime();
        List<weatherData> dataList = readDataset(filePath);
        long endRead = System.nanoTime();
        double durationReadMs = (endRead - startRead) / 1_000_000.0;
        System.out.println("---> Successfully loaded " + dataList.size() + " records from file.");
        System.out.printf("---> Dataset Loading Time: %.2f ms%n", durationReadMs);

        System.out.println("--------------------------------------------------");
        System.out.println("[+] Step 2: Running Simple Processing...");
        
        long startTimeSimple = System.nanoTime();
        runSimpleProcessing(dataList);
        long endTimeSimple = System.nanoTime();
        
        long durationSimpleNs = endTimeSimple - startTimeSimple;
        double durationSimpleMs = durationSimpleNs / 1_000_000.0;
        System.out.printf("---> Simple Processing Time: %,d ns (%.2f ms)%n", durationSimpleNs, durationSimpleMs);

        System.out.println("--------------------------------------------------");
        System.out.println("[+] Step 3: Running Complex Processing...");
        
        long startTimeComplex = System.nanoTime();
        runComplexProcessing(dataList);
        long endTimeComplex = System.nanoTime();
        
        long durationComplexNs = endTimeComplex - startTimeComplex;
        double durationComplexMs = durationComplexNs / 1_000_000.0;
        System.out.printf("---> Complex Processing Time: %,d ns (%.2f ms)%n", durationComplexNs, durationComplexMs);

        System.out.println("==================================================");
        System.out.println("Execution completed successfully.");
    }
}