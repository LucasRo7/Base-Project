package com.LS7;

import java.awt.Graphics;

import javax.swing.JFrame;

import com.LS7.core.*;
import com.LS7.util.Input;
import com.LS7.util.image.Pixel;
/**
 * Main class, responsible to create the JFrame, add Listeners, 
 * Updater and Renderer
 * 
 * @author LucasRo7
 */
public abstract class Main {
	
	/*
	 * Ideal implementation of this project:
	 *  create 3 classes, one will extend Renderer, one will extend Updater and the 
	 *  last one will extend Main, each abstract method should have a description.
	 */
	
	protected Renderer renderer;
	protected Updater updater;
	protected Input input;

	protected JFrame frame;
	
	protected abstract void init();
	/*protected void init(){
		Renderer.setup();
		renderer = new Renderer();
		updater = new Updater();
		input = new Input(Renderer.res);
	}*/
	
	protected void addListeners(Renderer renderer, Input input){
		renderer.addMouseListener(input);
		renderer.addMouseWheelListener(input);
		renderer.addMouseMotionListener(input);
		renderer.addKeyListener(input);
		renderer.addFocusListener(input);
	}
	
	protected void mkJFrame(Renderer renderer){
		frame = new JFrame();
		frame.setTitle(Data.windowName);
		frame.requestFocus();
		frame.setResizable(false);
		frame.add(renderer);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public Main() {
		Data.running=true;
		
		init();
		addListeners(renderer, input);
		mkJFrame(renderer);
		
		renderer.start();
		updater.start();
	}
	/**
	 * Example of the implementation of this project, you should create new classes
	 * extending the ones below in separate files.
	 */
	public static void main(String[] args) {
		Renderer r = new Renderer(){
			private static final long serialVersionUID = 1L;
			protected void graphicsRender(Graphics graphics) {
				graphics.drawRect(Input.mx-10, Input.my-10, 20, 20);
			}
			protected void pixelRender() {
				drawLine(Input.mx, Input.my, width/2, height/2, 0.1, true, Pixel.white);
			}
		};
		Renderer.width=320;
		Renderer.height=240;
		Renderer.res=2;
		Renderer.targetFPS=60;
		r.setup();
		Updater u = new Updater(){
			protected void update() {
				
			}
		};
		new Main(){
			protected void init() {
				renderer=r;
				updater=u;
				input = new Input(Renderer.res);
			}
		};
	}
}
