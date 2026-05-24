package com.mycompany.weatherproject;

import static com.mycompany.weatherproject.SequentialProcessor.readDataset;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/*
    -------------------Our team------------------------
    * Noor Alden Qasem -> Parallel Programming 202211008
    * Zaid Alqudsi-> parsing Data & Sequential 202120148
    * Abdulrahman Al-Lahham -> QA & Performance Analyst 202311254

*/


/**
 *
 * @author Noor Alden Qasem 
 * */

public class WeatherProject {

    public static void writeToFile(String fileName, String text) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
            out.println(text);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public static List<weatherData> generateDummyData(int count) {
        List<weatherData> dummyList = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            String date = "2026-05-" + (1 + random.nextInt(30));
            String summary = "Summary_" + i;
            String precipType = random.nextBoolean() ? "rain" : "snow";
            double temp = -5.0 + (45.0 * random.nextDouble());
            double appTemp = temp + (-2.0 + (4.0 * random.nextDouble()));
            double humidity = random.nextDouble();
            double windSpeed = 0.0 + (35.0 * random.nextDouble());

            dummyList.add(new weatherData(date, summary, precipType, temp, appTemp, humidity, windSpeed));
        }
        return dummyList;
    }

    public static void outterProcess(int totalThread, int chunkSize, int totalRecords, List<File> fileList, String reportFileName) {
        OuterThreadProcessor[] thds = new OuterThreadProcessor[totalThread];
        Thread[] thread = new Thread[totalThread];
        int toIndex;
        
        long startTime = System.nanoTime();
        for (int i = 0; i < totalThread; i++) {
            int formIndex = i * chunkSize;
           
            if (i == totalThread - 1)
                toIndex = totalRecords;
            else
                toIndex = formIndex + chunkSize;
            
            List<File> subList = fileList.subList(formIndex, toIndex);
            thds[i] = new OuterThreadProcessor(subList);
            thread[i] = new Thread(thds[i]);
            thread[i].start();
        }
        
        try {
            for (int i = 0; i < thread.length; i++)
                thread[i].join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        long endTime = System.nanoTime();
        
        int totalMatches = 0;
        for (int i = 0; i < thread.length; i++) {
            totalMatches += thds[i].getMatchCount();
        }
        
        String outputMatches = "-> [Outer Threading] Total Matches: " + totalMatches;
        String outputTime = "-> [Outer Threading] Time for " + totalThread + " threads: " + (endTime - startTime) + " ns";
        
        System.out.println(outputMatches);
        System.out.println(outputTime);
        writeToFile(reportFileName, outputMatches);
        writeToFile(reportFileName, outputTime);
    }
    
    public static void innerProcess(int totalThread, int chunkSize, int totalRecords, List<File> fileList, String reportFileName) {
        Thread[] innerThreads = new Thread[totalThread];
        int[][] innerCounts = new int[totalThread][1]; 
        int toIndex;
        long startTime = System.nanoTime();
        
        for (int i = 0; i < totalThread; i++) {
            int formIndex = i * chunkSize;
            if (i == totalThread - 1)
                toIndex = totalRecords;
            else
                toIndex = formIndex + chunkSize;            
        
            List<File> subList = fileList.subList(formIndex, toIndex);
            final int threadIdx = i;
            
            innerThreads[i] = new Thread(() -> {
                for (File file : subList) {
                    List<weatherData> rows = SequentialProcessor.readRowsFromExcelFile(file);
                    for (weatherData p : rows) {
                        double score = (0.5 * p.getTemperature()) + (0.3 * p.getHumidity() * 100) - (0.2 * p.getWindSpeed());
                        if (score > 30.0) {
                            innerCounts[threadIdx][0]++;
                        }
                    }
                }
            });
            innerThreads[i].start();
        }
        
        try {
            for (int i = 0; i < innerThreads.length; i++)
                innerThreads[i].join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        long endTime = System.nanoTime();
        
        int totalMatches = 0;
        for (int i = 0; i < totalThread; i++) {
            totalMatches += innerCounts[i][0];
        }
        
        String outputMatches = "-> [Inner Lambda]    Total Matches: " + totalMatches;
        String outputTime = "-> [Inner Lambda]    Time for " + totalThread + " threads: " + (endTime - startTime) + " ns";
        
        System.out.println(outputMatches);
        System.out.println(outputTime);
        writeToFile(reportFileName, outputMatches);
        writeToFile(reportFileName, outputTime);
    }
    
    
    
    public static void main(String[] args) {
        // Path of WeatherDataset
        String filePath = "/home/nqasem/Desktop/unversity/Weather/"; 

        System.out.println("==================================================");
        System.out.println("        SEQUENTIAL PROCESSING OUTPUT");
        System.out.println("==================================================");

        // I would like to set Reading data in files
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportFileName = "Performance_Report_" + timestamp + ".txt";

        writeToFile(reportFileName, "==================================================");
        writeToFile(reportFileName, "        SEQUENTIAL PROCESSING OUTPUT");
        writeToFile(reportFileName, "==================================================");
        
        // Reading Dataset
        List<File> fileList = readDataset(filePath);

        // Test in mult thread
        int[] threadTests = {2, 4, 8, 16, 32, 64, 128};
        int totalRecords = fileList.size(); //  saving number of xlsx

        System.out.println("\nRunning Sequential Processors...");
        writeToFile(reportFileName, "\nRunning Sequential Processors...");
        
        // calculate time of process
        long seqSimpleStart = System.nanoTime();
        SequentialProcessor.runSimpleProcessing(fileList);
        long seqSimpleEnd = System.nanoTime();
        String seqSimpleTime = "-> [Sequential Simple] Time: " + (seqSimpleEnd - seqSimpleStart) + " ns";
        System.out.println(seqSimpleTime + "\n");
        writeToFile(reportFileName, seqSimpleTime + "\n");

        long seqComplexStart = System.nanoTime();
        SequentialProcessor.runComplexProcessing(fileList);
        long seqComplexEnd = System.nanoTime();
        String seqComplexTime = "-> [Sequential Complex] Time: " + (seqComplexEnd - seqComplexStart) + " ns";
        System.out.println(seqComplexTime + "\n");
        writeToFile(reportFileName, seqComplexTime + "\n");

        for (int totalThread : threadTests) {
            int chunkSize = totalRecords / totalThread;
            if (chunkSize == 0) chunkSize = 1;
            
            System.out.println("==================================================");
            System.out.println(" TESTING PERFORMANCE WITH THREAD COUNT: " + totalThread);
            System.out.println("==================================================");
            
            writeToFile(reportFileName, "==================================================");
            writeToFile(reportFileName, " TESTING PERFORMANCE WITH THREAD COUNT: " + totalThread);
            writeToFile(reportFileName, "==================================================");
            
            outterProcess(totalThread, chunkSize, totalRecords, fileList, reportFileName);
            
            System.out.println("--------------------------------------------------");
            writeToFile(reportFileName, "--------------------------------------------------");
            
            innerProcess(totalThread, chunkSize, totalRecords, fileList, reportFileName);
            
            System.out.println();
            writeToFile(reportFileName, "");
        }
        System.out.println("🎉----------------------------- Check Your Files -----------------------------\n");
        System.out.println("🎉 Proccess Done :). Results saved to: " + reportFileName + "\n Thanks Dr.Nada :) ");
    }
}

/*

//This main code is only for Test for unreal data

//   which we use it to work Parallel in the project :)



    public static void main(String[] args) {
        System.out.println("Create unreal data");
        List<Player> dataList = generateDummyData(100000);
        System.out.println("generate Data" + dummyPlayers.size() + " done, Player Data\n");
        int totalRecords = dummyPlayers.size();


        int[] threadTests = {2, 4, 8, 16, 32, 64, 128};
        int totalRecords = dummyPlayers.size();

        for (int totalThread : threadTests) {
            int chunkSize = totalRecords / totalThread;
            
            System.out.println("==================================================");
            System.out.println(" TESTING PERFORMANCE WITH THREAD COUNT: " + totalThread);
            System.out.println("==================================================");
            
            outterProcess(totalThread, chunkSize, totalRecords, dataList);
            
            System.out.println("--------------------------------------------------");
            
            innerProcess(totalThread, chunkSize, totalRecords, dataList);
            
            System.out.println();
        }
    }
*/