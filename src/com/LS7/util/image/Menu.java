package com.LS7.util.image;

import java.awt.Graphics;

public class Menu {
	
	public static Spriteset sprites = new Spriteset("/com/LS7/res/Menu.png",3,3);
	
	public static void window(Graphics g, int x, int y, int w, int h){
		x=(x/sprites.width);
		y=(y/sprites.height);
		w=(w/sprites.width);
		h=(h/sprites.height);
		for(int xx=x;xx<=x+w;x+=sprites.width)
			for(int yy=y;yy<=y+h;y+=sprites.height){
				if(xx==x){
					if(yy==y)
						g.drawImage(sprites.getSprite(0, 0), x*sprites.width, y*sprites.height, null);
					else if(yy==y+h)
						g.drawImage(sprites.getSprite(0, 1), x*sprites.width, y*sprites.height, null);
					else
						g.drawImage(sprites.getSprite(0, 2), x*sprites.width, y*sprites.height, null);
				}else if(xx==x+w){
					if(yy==y)
						g.drawImage(sprites.getSprite(2, 0), x*sprites.width, y*sprites.height, null);
					else if(yy==y+h)
						g.drawImage(sprites.getSprite(2, 1), x*sprites.width, y*sprites.height, null);
					else
						g.drawImage(sprites.getSprite(2, 2), x*sprites.width, y*sprites.height, null);
				}else{
					if(yy==y)
						g.drawImage(sprites.getSprite(1, 0), x*sprites.width, y*sprites.height, null);
					else if(yy==y+h)
						g.drawImage(sprites.getSprite(1, 1), x*sprites.width, y*sprites.height, null);
					else
						g.drawImage(sprites.getSprite(1, 2), x*sprites.width, y*sprites.height, null);
				}
			}
	}
	
}
