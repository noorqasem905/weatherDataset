package com.mycompany.weatherproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeatherProject {

    public static List<Player> generateDummyData(int count) {
        List<Player> dummyList = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            String name = "Player_" + i;
            double pace = 50.0 + (50.0 * random.nextDouble());
            double shooting = 50.0 + (50.0 * random.nextDouble());
            double dribbling = 50.0 + (50.0 * random.nextDouble());

            dummyList.add(new Player(name, pace, shooting, dribbling));
        }
        return dummyList;
    }

    public static void outterProcess(int totalThread, int chunkSize, int totalRecords, List<Player> dummyPlayers) {
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
            
            List<Player> subList = dummyPlayers.subList(formIndex, toIndex);
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
        
        System.out.println("-> [Outer Threading] Total Matches: " + totalMatches);
        System.out.println("-> [Outer Threading] Time for " + totalThread + " threads: " + (endTime - startTime) + " ns");
    }
    
    public static void innerProcess(int totalThread, int chunkSize, int totalRecords, List<Player> dummyPlayers) {
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
        
            List<Player> subList = dummyPlayers.subList(formIndex, toIndex);
            final int threadIdx = i;
            
            innerThreads[i] = new Thread(() -> {
                for (Player p : subList) {
                    double score = 0.4 * p.getPace() + 0.3 * p.getShooting() + 0.3 * p.getDribbling();
                    if (score > 85.0) {
                        innerCounts[threadIdx][0]++;
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
        
        System.out.println("-> [Inner Lambda]    Total Matches: " + totalMatches);
        System.out.println("-> [Inner Lambda]    Time for " + totalThread + " threads: " + (endTime - startTime) + " ns");
    }
    
    public static void main(String[] args) {
        System.out.println("جاري توليد بيانات وهمية للتجربة...");
        List<Player> dummyPlayers = generateDummyData(100000);
        System.out.println("تم توليد " + dummyPlayers.size() + " لاعب بنجاح!\n");

        int[] threadTests = {2, 4, 8, 16, 32, 64, 128};
        int totalRecords = dummyPlayers.size();
        
        for (int totalThread : threadTests) {
            int chunkSize = totalRecords / totalThread;
            
            System.out.println("==================================================");
            System.out.println(" TESTING PERFORMANCE WITH THREAD COUNT: " + totalThread);
            System.out.println("==================================================");
            
            outterProcess(totalThread, chunkSize, totalRecords, dummyPlayers);
            
            System.out.println("--------------------------------------------------");
            
            innerProcess(totalThread, chunkSize, totalRecords, dummyPlayers);
            
            System.out.println();
        }
    }
}