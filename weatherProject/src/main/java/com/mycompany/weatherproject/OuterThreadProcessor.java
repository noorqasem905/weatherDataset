
package com.mycompany.weatherproject;

import java.util.List;

/**
 *
 * @author nqasem
 */
public class OuterThreadProcessor implements Runnable{
    List<Player> weather;
    private int matchCount = 0;
    public OuterThreadProcessor(List<Player> weather) {
        this.weather = weather;
    }

    
    @Override
    public void run() {
        for (Player p : weather) {
            double score = 0.4 * p.getPace() + 0.3 * p.getShooting() + 0.3 * p.getDribbling();
        if (score > 85.0) {
                matchCount++;
        }
        }
    }
    public int getMatchCount() {
        return matchCount;
    }
    
}
