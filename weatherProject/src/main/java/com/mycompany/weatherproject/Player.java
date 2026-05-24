//This file is only for test for unreal data
/*
    which we use it to work Parallels in the project :)
*/

package com.mycompany.weatherproject;

/**
 *
 * @author Noor Alden Qasem 202211008
 * 
 */

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