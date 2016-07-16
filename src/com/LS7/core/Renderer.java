package com.LS7.core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.LS7.util.Input;
import com.LS7.util.image.Pixel;

public class Renderer extends Canvas implements Runnable {
	
    public static int res = 2;
    public static int width = 640/res;
    public static int height = 480/res;
    public static double FPS = 59.5;
    protected static final long serialVersionUID = 1L;

    protected BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    protected int[] imgpixels = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
    protected Pixel[] pixels = new Pixel[imgpixels.length];
    protected boolean rendering = false;

    public long lt=0;
    public long time=0;

    protected Thread thread;

    public Renderer() {
        setPreferredSize(new Dimension(width*res,height*res));
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Pixel.black;
        }
        lt=System.currentTimeMillis();
    }

    public void start() {
        thread = new Thread(this,"Renderer Thread");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public synchronized void run() {
        long lastNanoTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / FPS;
        long timer = System.currentTimeMillis();
        int  frames = 0;
        while (Data.running) {
            long now = System.nanoTime();
            unprocessed += (now - lastNanoTime) / nsPerTick;
            lastNanoTime = now;
            if(unprocessed>=10){
                System.out.println("Skipping "+(unprocessed-10));
                unprocessed-=10;
            }
            while (unprocessed >= 1) {
                render();
                frames++;
                unprocessed -= 1;
                if (System.currentTimeMillis() - timer >= 1000) {
                    timer+=1000;
                    Data.fps = frames;
                    frames = 0;
                }
                //System.out.println(""+((System.nanoTime() - lastNanoTime) / nsPerTick));
            }
        }
    }
    
    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
            return;
        }
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Pixel.black;
        }
        rendering = true;
        pixelRender();
        rendering = false;
        for (int i = 0; i < imgpixels.length; i++) {
            imgpixels[i] = pixels[i].getHex();
        }
        Graphics g = bi.getGraphics();
        graphicsRender(g);
        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(bi, 0, 0, width*res, height*res, null);
        g.setColor(new Color(0,0,0,128));
        g.fillRect(0, 0, 50, 36);
        g.setColor(Color.white);
        g.drawString("FPS:"+Data.fps, 0, 16);
        g.drawString("TPS:"+Data.tps, 0, 32);
        bs.show();
        g.dispose();
    }

    protected void graphicsRender(Graphics g) {
        
    }
    protected void pixelRender() {
    	
    }

    public Pixel getPixel(int x, int y) {
        if (rendering) {
            if (onScreen(x,y))
                return pixels[x + y * width];
            return Pixel.black;
        } else {
            new Exception("Attempt to get Pixel while not rendering").printStackTrace();
        }
        return null;
    }
    public static boolean onScreen(double x, double y){
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    public void drawPixel(int x, int y, Pixel p) {
        if (rendering) {
            if (onScreen(x,y))
                pixels[x + y * width] = p;
        } else {
            new Exception("Attempt to draw Pixel while not rendering").printStackTrace();
        }
    }
    public void drawPixel(double x, double y, Pixel p) {
        if (rendering) {
            if (onScreen(x,y))
                pixels[((int)x) + ((int)y) * width] = p;
        } else {
            new Exception("Attempt to draw Pixel while not rendering").printStackTrace();
        }
    }
    public void drawLine(Pixel col, double x1,double y1,double x2,double y2){
    	double x = x2-x1;
    	double y = y2-y1;
    	double dir = Math.atan2(x, y);
    	double dist = Math.sqrt(x*x+y*y);
    	for(double mag = 0;mag<=dist;mag+=0.1){
    		double nx=x1+mag*Math.sin(dir);
    		double ny=y1+mag*Math.cos(dir);
    		drawAAPixels(nx,ny,col);
    	}
    }
    public void drawLine(Pixel col, int x1,int y1,int x2,int y2){
    	double x = x2-x1;
    	double y = y2-y1;
    	double dir = Math.atan2(x, y);
    	double dist = Math.sqrt(x*x+y*y);
    	for(double mag = 0;mag<=dist;mag+=0.1){
    		double nx=x1+mag*Math.sin(dir);
    		double ny=y1+mag*Math.cos(dir);
    		drawPixel((int)nx,(int)ny,col);
    	}
    }
    public void drawAAPixels(double x, double y, Pixel p){
    	double o=0.4;
    	Pixel darker=p.mult(0.1,1,1,1);
    	drawAAPixel(x+o,y+o,darker);
    	drawAAPixel(x-o,y+o,darker);
    	drawAAPixel(x+o,y-o,darker);
    	drawAAPixel(x-o,y-o,darker);
    	drawAAPixel(x  ,y  ,p);
    }
    public void drawAAPixel(double x, double y, Pixel p){
    	if(!onScreen((int)x,(int)y))
    		return;
		double px= x-(((int)x)+0.5);
    	double py= y-(((int)y)+0.5);
        double d = 1-Math.sqrt(px*px+py*py)*1.7;
    	if(Input.action){
	        px= (0.5+Math.abs(Math.round(x)-x));
	        py= (0.5+Math.abs(Math.round(y)-y));
	        d=px*py;
    	}
        Pixel color=p.mult(d, 1, 1, 1);
        drawPixel((int)x,(int)y,color.over(getPixel((int)x,(int)y)));
    }
}
