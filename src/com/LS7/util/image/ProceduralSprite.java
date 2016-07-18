package com.LS7.util.image;

import java.awt.image.BufferedImage;
import java.util.Random;
/**
 * Thanks to Zelimir Fedoran at github since he made this generator, wich I ported 
 * to Java.<br>
 * 
 * Github Repository:
 * <a href=https://github.com/zfedoran/pixel-sprite-generator>Original Github repository</a>
 */
public class ProceduralSprite {
	
	public interface mask{
		public int width();
		public int height();
		public double saturation();
		public double colorVar();
		public double brightnessNoise();
		public double getData(int x, int y);
		public double edgeBrightness();
		public double brightness();
	}
	public static BufferedImage Sprite(mask m, boolean[] options,long seed){
		Random rand = new Random(seed);
		return sprite(m,options,rand);
		
	}
	public static BufferedImage Sprite(mask m, boolean[] options){
		Random rand = new Random();
		return sprite(m,options,rand);
	}
	public static BufferedImage sprite(mask m, boolean[] options,Random rand){
		boolean mirrorX=options[0];
		boolean mirrorY=options[1];
		int w=m.width()*(mirrorX?2:1);
		int h=m.height()*(mirrorY?2:1);
		double[][] data = new double[w][h];
		BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < m.width(); x++)
			for (int y = 0; y < m.height(); y++) {
				double val = m.getData(x, y);
				val=(val*(rand.nextDouble()));
				data[x][y]=val>0.5?1:0;
			}
		if(mirrorX)
			for (int x = 0; x < w/2; x++)
				for (int y = 0; y < h; y++) {
					data[w-x-1][y]=data[x][y];
				}
		if(mirrorY)
			for (int x = 0; x < w; x++)
				for (int y = 0; y < h/2; y++) {
					data[x][h-y-1]=data[x][y];
				}
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++) {
				if(data[x][y]>0){
					if(y-1>=0&&data[x][y-1]==0)
						data[x][y-1]=-1;
					if(y+1<h&&data[x][y+1]==0)
						data[x][y+1]=-1;
					if(x-1>=0&&data[x-1][y]==0)
						data[x-1][y]=-1;
					if(x+1<w&&data[x+1][y]==0)
						data[x+1][y]=-1;
				}
			}
		boolean vertcolor=rand.nextDouble()>0.5;
		double saturation=Math.max(Math.min(m.saturation(), 1), 0);
		double hue = rand.nextDouble();
		int xx,yy,ww,hh;
		if(vertcolor){
			ww=h;
			hh=w;
		}else {
			ww=w;
			hh=h;
		}
        for (xx = 0; xx < ww; xx++) {
        	double newColor = Math.abs(((rand.nextDouble() * 2 - 1) 
        			+ (rand.nextDouble() * 2 - 1) 
        			+ (rand.nextDouble() * 2 - 1)) / 3);
        	if(newColor>(1-m.colorVar()))
        		hue = rand.nextDouble();
            for (yy = 0; yy < hh; yy++) {
            	double val;
        		if(vertcolor){
        			val=data[yy][xx];
        			//index=(yy+xx*hh)*4;
        		}else {
        			val=data[xx][yy];
        			//index=(xx+yy*ww)*4;
        		}
        		Pixel col =Pixel.transparent;
        		if(val!=0){
        			double brightness = (Math.sin((xx*1.0 / ww) * Math.PI) * (1 - m.brightnessNoise()) 
                            + rand.nextDouble() * m.brightnessNoise())*m.brightness();
        			if(val==-1){
        				brightness*=m.edgeBrightness();
        			}
        			col= Pixel.hslPixel.HSLtoRGB(hue,saturation,brightness+0.1);
            		col.alpha=255;
        		}
        		if(vertcolor){
        			bi.setRGB(yy, xx, col.getHex());
        		}else {
        			bi.setRGB(xx, yy, col.getHex());
        		}
            }
        }
        return bi;
	}
	
}
