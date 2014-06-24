package com.rundle.haas.simulator;

import com.rundle.haas.GameEngine;
import com.rundle.haas.GameEventQueueManager;
import com.rundle.haas.PlayerRegistry;
import com.rundle.haas.Table;

/**
 * Created by timothy on 6/21/14.
 */
public class GameSimulator {

    public static void main (String [] args) {

        try {
            for (int i = 0; i < 100; i++) {
                GameEngine gameEngine = new GameEngine();
                gameEngine.start();
                gameEngine.join();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
