package com.LS7.util.image;

import java.awt.Graphics;

import com.LS7.util.Math.Matrix3f;
import com.LS7.util.Math.Vector2f;

public class Graphics2D{
	// Matrix:
	//  scale x, x shear, xoff
	//  y shear, scale y, yoff
	//        0,       0, 1
	public Matrix3f matrix=Matrix3f.identity();
	
	public Vector2f transform(Vector2f pos){
		return Vector2f.transformCoordinates(pos, matrix);
	}
	public void drawRect(Graphics g, int x, int y, int w, int h){
		Vector2f p = new Vector2f(x,y);
		Vector2f s = new Vector2f(w,h);
		p = transform(p);
		s = transform(s);
		g.drawRect((int)p.x, (int)p.y, (int)s.x, (int)s.y);
	}
	public void drawLine(Graphics g, int x1, int y1, int x2, int y2){
		Vector2f p = new Vector2f(x1,y1);
		Vector2f s = new Vector2f(x2,y2);
		p = transform(p);
		s = transform(s);
		g.drawRect((int)p.x, (int)p.y, (int)s.x, (int)s.y);
	}
}
