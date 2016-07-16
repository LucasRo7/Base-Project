package com.LS7.util.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.LS7.util.image.Image;

public class Spriteset {
	
	private BufferedImage mainImage;
	private BufferedImage[][] spriteset;
	public int width;
	public int height;
	
	public Spriteset(String path, int horz, int vert){
		mainImage = Image.loadImage(path);
		spriteset = new BufferedImage[vert][horz];
		width=mainImage.getWidth()/horz;
		height=mainImage.getHeight()/vert;
		for(int v = 0;v<vert;v++)
			for(int h = 0;h<horz;h++){
				spriteset[v][h] = new BufferedImage(mainImage.getWidth()/horz,mainImage.getHeight()/vert,BufferedImage.TYPE_INT_ARGB);
				Graphics g = spriteset[v][h].getGraphics();
				g.drawImage(mainImage, -h*(mainImage.getWidth()/horz), -v*(mainImage.getHeight()/vert), null);
				g.dispose();
			}
	}
	
	public BufferedImage getSprite(int x, int y){
		return spriteset[y][x];
	}
	
}
