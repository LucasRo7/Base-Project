package com.LS7.util.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {

	public static void betweenBubbles(double b1R,double b1x,double b1y,double b2R,double b2x,double b2y,int size,Graphics g){
		double r = b1R+b2R;
		double mx=(b1x+b2x)/2;
		double my=(b1y+b2y)/2;
		double xd=b2x-b1x;
		double yd=b2y-b1y;
		double dist = Math.sqrt(xd*xd+yd*yd);
		if(dist<r-1){
			double angle=Math.atan2(xd,yd)+Math.PI/2;
			double cos = Math.cos((5*dist/Math.PI/r));
			cos=Math.sqrt(cos);
			double mag = cos*r/2+1;
			double nx=(mx)+mag*Math.sin(angle);
			double ny=(my)+mag*Math.cos(angle);
			double onx=(mx)+mag*Math.sin(angle+Math.PI);
			double ony=(my)+mag*Math.cos(angle+Math.PI);
			line(g,nx,ny,onx,ony,size);
		}
	}
	
	public static void line(Graphics g, double x1, double y1, double x2, double y2, int s){
		for(int xx=-s;xx<s;xx++)
			for(int yy=-s;yy<s;yy++)
				g.drawLine((int)(x1+xx), (int)(y1+yy), (int)(x2+xx), (int)(y2+yy));
	}
	
	public static BufferedImage loadImage(String relativePath){
		try {
			return ImageIO.read(Image.class.getResource(relativePath));
		} catch (IOException e) {
			e.printStackTrace();
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		}
	}
}
