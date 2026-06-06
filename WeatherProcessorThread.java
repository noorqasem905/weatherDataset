package com.mycompany.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;

class WeatherProcessorThread extends Thread {
    private PipedInputStream pin;

    private int totalRecords = 0;
    private int matchCount = 0;
    private double maxTemperature = -Double.MAX_VALUE;

    public WeatherProcessorThread(PipedInputStream pin) {
        this.pin = pin;
    }

    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(pin))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] c = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (c.length >= 7) {
                    double temp = Double.parseDouble(c[3].trim());
                    double humidity = Double.parseDouble(c[5].trim());
                    double wind = Double.parseDouble(c[6].trim());

                    totalRecords++;

                    if (temp > maxTemperature) {
                        maxTemperature = temp;
                    }

                    double score = (0.5 * temp) + (0.3 * humidity * 100) - (0.2 * wind);
                    if (score > 30.0) {
                        matchCount++;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }
}
