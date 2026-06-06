package com.mycompany.project;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class ProjectPhase3Zaid {

    public static void main(String[] args) {

        String filePath = "C:\\Users\\USER\\Desktop\\weatherHistory.csv";

        System.out.println("PIPE BASED PROCESSING");

        try {
            PipedOutputStream pout = new PipedOutputStream();
            PipedInputStream pin = new PipedInputStream();

            pout.connect(pin);

            WeatherReaderThread reader = new WeatherReaderThread(filePath, pout);
            WeatherProcessorThread processor = new WeatherProcessorThread(pin);

            long start = System.nanoTime();

            reader.start();
            processor.start();

            reader.join();
            processor.join();

            long end = System.nanoTime();

            System.out.println("Total Records: " + processor.getTotalRecords());
            System.out.println("Max Temperature: " + processor.getMaxTemperature());
            System.out.println("Match Count: " + processor.getMatchCount());
            System.out.println("Time: " + (end - start) / 1_000_000.0 + " ms");

        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}