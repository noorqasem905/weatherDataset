package com.mycompany.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PipedOutputStream;

class WeatherReaderThread extends Thread {
    private String filePath;
    private PipedOutputStream pout;

    public WeatherReaderThread(String filePath, PipedOutputStream pout) {
        this.filePath = "C:\\Users\\USER\\Desktop\\weatherHistory.csv";
        this.pout = pout;
    }

    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath));
             PrintWriter pw = new PrintWriter(pout, true)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                pw.println(line);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}