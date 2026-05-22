package com.mycompany.weatherproject;

public class Player {
    private String name;
    private double pace;
    private double shooting;
    private double dribbling;

    public Player(String name, double pace, double shooting, double dribbling) {
        this.name = name;
        this.pace = pace;
        this.shooting = shooting;
        this.dribbling = dribbling;
    }

    // الـ Getters الـضرورية لشغل التوازي والمعادلات
    public String getName() { return name; }
    public double getPace() { return pace; }
    public double getShooting() { return shooting; }
    public double getDribbling() { return dribbling; }
    @Override
    public String toString() {
    return String.format("Player{Name='%s', Pace=%.1f, Shooting=%.1f, Dribbling=%.1f}", 
            name, pace, shooting, dribbling);
}
}