package com.LS7.util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Input implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener {
	
	public static interface listener{
		public void keyTyped(KeyEvent e);
		public void keyPress(KeyEvent e);
		public void keyRelease(KeyEvent e);
		public void mouseClick(int lmb);
		public void mouseDown(int i);
		public void mouseDrag(int x, int y);
		public void mouseUp();
	}
	public static int res = 1;
	public static int mouseMoveTolerance = 2;
	public static boolean lock = false;

	public static boolean mouseIn(int x, int y, int w, int h, boolean inclusive){
		if(inclusive)
			return(mx>=x&&my>=y&&mx<=x+w&&my<=y+h);
		else
			return(mx>x&&my>y&&mx<x+w&&my<y+h);
	}
	public static boolean mouseIn(int x, int y, int w, int h){
		return mouseIn(x, y, w, h, true);
	}

	public static double amx = 0;
	public static double amy = 0;
	public static int mx = 0;
	public static int my = 0;
	public static int mz = 0;
	public static int mb = 0;

	public static int lmx = 0;
	public static int lmy = 0;
	public static int lmb = 0;
	
	public static boolean mouseStatic=false;
	public static listener tl;
	public static boolean action,space,escape,shift,ctrl,up,down,left,right,q,e;
	public static boolean[] keys = new boolean[65565];

	public Input(){}
	public Input(int r){
		res=r;
	}
	
	private void updMouse(MouseEvent e){
		if(lock==true)
			return;
		if(e instanceof MouseWheelEvent){
			mz+=((MouseWheelEvent) e).getWheelRotation();
		}else{
			amx=e.getX()*1.0/res;
			amy=e.getY()*1.0/res;
			mx=e.getX()/res;
			my=e.getY()/res;
		}
		if(tl==null)
			return;
		if(mouseStatic){
			if(Math.abs(mx-lmx)>=mouseMoveTolerance||Math.abs(my-lmy)>=mouseMoveTolerance)
				mouseStatic=false;
			else if(e.getButton()!=lmb)
				tl.mouseClick(lmb);
		}else{
			tl.mouseDrag(mx-lmx, my-lmy);
		}
	}
	private void updKeys(){
		action = keys[KeyEvent.VK_ENTER]||keys[KeyEvent.VK_SPACE];
		space = keys[KeyEvent.VK_SPACE];
		escape = keys[KeyEvent.VK_ESCAPE];
		shift = keys[KeyEvent.VK_SHIFT];
		ctrl = keys[KeyEvent.VK_CONTROL];
		up = keys[KeyEvent.VK_W]||keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_S]||keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_A]||keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_D]||keys[KeyEvent.VK_RIGHT];
		q = keys[KeyEvent.VK_Q];
		e = keys[KeyEvent.VK_E];
	}
	
	public void mouseClicked(MouseEvent e) {updMouse(e);}
	public void mouseEntered(MouseEvent e) {updMouse(e);}
	public void mouseExited(MouseEvent e) {updMouse(e);}
	public void mousePressed(MouseEvent e) {
		updMouse(e);
		mb=e.getButton();
		lmx=e.getX();
		lmy=e.getY();
		lmb=e.getButton();
		if(tl!=null)
			tl.mouseDown(e.getButton());
	}
	public void mouseReleased(MouseEvent e) {
		if(tl!=null)
			tl.mouseUp();
		updMouse(e);
		mb=0;
	}
	public void mouseDragged(MouseEvent e) {updMouse(e);}
	public void mouseMoved(MouseEvent e) {updMouse(e);}
	public void mouseWheelMoved(MouseWheelEvent e) {updMouse(e);}
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()]=true;
		updKeys();
		if(tl!=null)
			tl.keyPress(e);
	}
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()]=false;
		updKeys();
		if(tl!=null)
			tl.keyRelease(e);
	}
	public void keyTyped(KeyEvent e) {
		if(tl!=null)
			tl.keyTyped(e);
	}
	public void focusGained(FocusEvent arg0) {}
	public void focusLost(FocusEvent arg0) {
		for(int i=0;i<65565;i++){
			keys[i]=false;
		}
		mb=0;
	}
}
