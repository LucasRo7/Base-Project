package com.LS7.core;

/**
 * Updater class, responsible for everything done(other than rendering)
 * 
 * @author LucasRo7
 */
public abstract class Updater implements Runnable {

    // Update calls per second
    public static double targetUPS = 60;
    
    // Thread that contains the Updater class
    public Thread thread;

    /**
     * Create and start a thread containing this updater class
     */
    public void start() {
        thread = new Thread(this,"Updater Thread");
        thread.start();
    }
    /**
     * Interrupts the thread containing this renderer class
     */
    public void stop() {
        thread.interrupt();
    }
    /**
     * Main loop
     */
    public void run() {
        long lastNanoTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / targetUPS;
        long timer = System.currentTimeMillis();
        int ticks = 0;
        while (Data.running) {
            long now = System.nanoTime();
            unprocessed += (now - lastNanoTime) / nsPerTick;
            lastNanoTime = now;
            if(unprocessed>=targetUPS){
                System.out.println("Skipping "+(unprocessed-targetUPS));
                unprocessed-=targetUPS;
            }
            while (unprocessed >= 1) {
                update();
                ticks++;
                unprocessed -= 1;
            }
            if (System.currentTimeMillis() - timer >= 1000) {
                timer+=1000;
                Data.tps = ticks;
                ticks = 0;
            }
        }
    }
    /**
     * Used to update everything
     */
    protected abstract void update();
}
