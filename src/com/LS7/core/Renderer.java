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
/**
 * Renderer class, responsible for all the rendering done.
 * 
 * @author LucasRo7
 */
public class Renderer extends Canvas implements Runnable {
	
	// Amount of horizontal pixels
    public static int width = 320;
    // Amount of vertical pixels
    public static int height = 240;
	// Scale of pixels
    public static int res = 2;
    // Frames per second
    public static double FPS = 60;
	private static final long serialVersionUID = 1880057858920032660L;
	
	// Image that contains all pixels(integer pixels)
    protected BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	// Array that contains all pixels(integer pixels)
    protected int[] imgpixels = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
	// Array that contains all pixels(Pixel class)
    protected Pixel[] pixels = new Pixel[imgpixels.length];
    
    // Thread that contains the Renderer class
    protected Thread thread;
    
    public Renderer() {
        setPreferredSize(new Dimension(width*res,height*res));
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Pixel.black;
        }
    }
    /**
     * Create and start a thread containing this renderer class
     */
    public void start() {
        thread = new Thread(this,"Renderer Thread");
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
            }
        }
    }
    /**
     * Used to render everything.
     * This method is private, if another class extends Renderer, 
     * it should use graphicsRender(...) or pixelRender()
     */
    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
            return;
        }
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Pixel.black;
        }
        pixelRender();
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
    /**
     * Renders anything that uses the Graphics to render
     * 
     * @param graphics the graphics class used to render
     */
    protected void graphicsRender(Graphics graphics) {
    	// something.render(graphics);
    }
    /**
     * Renders anything that uses per pixel rendering
     * e.g. uses methods like drawPixel(...),
     */
    protected void pixelRender() {
    	// something.render(this);
    	drawLine(width/2, height/2, Input.amx, Input.amy,0.1,true,Pixel.white);
    }
    /**
     * Returns the pixel at the specified position.
     * @param x the pixel's X position
     * @param y the pixel's Y position
     * @return the pixel
     */
    public Pixel getPixel(int x, int y) {
        if (onScreen(x,y))
            return pixels[x + y * width];
        return Pixel.black;
    }
    /**
     * Checks if the position is inside the screen and
     * can be drawn
     * @param x the X position
     * @param y the Y position
     * @return if the position is inside the screen
     */
    public static boolean onScreen(double x, double y){
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    /**
     * Draws a pixel on the screen. This class automatically
     * checks if the pixel is inside the screen
     * @param x the pixel's X position
     * @param y the pixel's Y position
     * @param the Pixel to draw
     */
    public void drawPixel(int x, int y, Pixel color) {
        if (onScreen(x,y))
            pixels[x + y * width] = color.over(getPixel(x,y));
    }
    /**
     * Draws a line between two points on the screen.
     * @param x1 the X position of the first point
     * @param y1 the Y position of the first point
     * @param x2 the X position of the second point
     * @param y2 the Y position of the second point
     * @param precision the precision of the line, 1 is ideal if AA is false,
     *        any value higher than 1 may produce blank spaces within the line and
     *        values too small may cause lag
     * @param AA if this class should use anti-aliasing
     * @param color the Pixel to draw
     */
    public void drawLine(double x1,double y1,double x2,double y2,double precision,boolean AA,Pixel color){
    	double x = x2-x1;
    	double y = y2-y1;
    	double dir = Math.atan2(x, y);
    	double dist = Math.sqrt(x*x+y*y);
    	for(double mag = 0;mag<=dist;mag+=0.1){
    		double nx=x1+mag*Math.sin(dir);
    		double ny=y1+mag*Math.cos(dir);
    		if(AA)
    			drawAAPixels(nx,ny,color);
    		else
    			drawPixel((int)nx,(int)ny,color);
    	}
    }
    /**
     * Used to draw 5 anti-aliased pixels, to make it look better.
     * @param x the pixel's X position
     * @param y the pixel's Y position
     * @param the Pixel to draw
     */
    public void drawAAPixels(double x, double y, Pixel p){
    	double o=0.4;
    	Pixel darker=p.mult(0.1,1,1,1);
    	drawAAPixel(x+o,y+o,darker);
    	drawAAPixel(x-o,y+o,darker);
    	drawAAPixel(x+o,y-o,darker);
    	drawAAPixel(x-o,y-o,darker);
    	drawAAPixel(x  ,y  ,p);
    }
    /**
     * Used to draw a pixel with a smaller alpha value based on the distance to 
     * the center of the nearest pixel, it automatically detects if the pixel is
     * inside the screen.
     * @param x the pixel's X position
     * @param y the pixel's Y position
     * @param the Pixel to draw
     */
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
