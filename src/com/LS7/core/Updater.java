package com.LS7.core;

public class Updater implements Runnable {

    public Thread thread;

    public Updater() {
        
    }

    public void start() {
        thread = new Thread(this,"Updater Thread");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public void run() {
        // This code is SOOO old :/
        long lastNanoTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / 60;
        long timer = System.currentTimeMillis();
        int ticks = 0;
        while (Data.running) {
            long now = System.nanoTime();
            unprocessed += (now - lastNanoTime) / nsPerTick;
            lastNanoTime = now;
            if(unprocessed>=6000){
                System.out.println("Skipping "+(unprocessed-6000));
                unprocessed-=6000;
            }
            while (unprocessed >= 1) {
                tick();
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

    protected void tick() {
        
    }
}
