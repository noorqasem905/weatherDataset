package com.mycompany.weatherproject;

import java.io.File;
import java.util.List;

/**
 *
 * @author Noor Alden Qasem 202211008
 * 
 * */
public class OuterThreadProcessor implements Runnable{
    // Edit it if you want unreal data <Player>
    List<File> weather;
    private int matchCount = 0;
    public OuterThreadProcessor(List<File> weather) {
        this.weather = weather;
    }

    
    @Override
    public void run() {
        for (File file : weather) {
            List<weatherData> rows = SequentialProcessor.readRowsFromExcelFile(file);
            for (weatherData p : rows) {
                double score = (0.5 * p.getTemperature()) + (0.3 * p.getHumidity() * 100) - (0.2 * p.getWindSpeed());
            if (score > 30.0) {
                    matchCount++;
            }
            }
        }
    }
    public int getMatchCount() {
        return matchCount;
    }
    
}